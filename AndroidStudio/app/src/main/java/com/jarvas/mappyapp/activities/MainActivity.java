package com.jarvas.mappyapp.activities;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.Scenario;
import com.jarvas.mappyapp.adapter.LocationAdapter;
import com.jarvas.mappyapp.kakao_api.ApiClient;
import com.jarvas.mappyapp.kakao_api.ApiInterface;
import com.jarvas.mappyapp.listener.NaverRecognizer;
import com.jarvas.mappyapp.models.category_search.CategoryResult;
import com.jarvas.mappyapp.models.category_search.Document;
import com.jarvas.mappyapp.utils.AudioWriterPCM;
import com.jarvas.mappyapp.utils.BusProvider;
import com.jarvas.mappyapp.utils.ContextStorage;
import com.jarvas.mappyapp.utils.CurrentPlace;
import com.jarvas.mappyapp.utils.IntentKey;
import com.jarvas.mappyapp.utils.StringResource;
import com.jarvas.mappyapp.utils.Util;
import com.naver.speech.clientapi.SpeechConfig;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MapView.MapViewEventListener, MapView.POIItemEventListener, MapView.OpenAPIKeyAuthenticationResultListener,MapView.CurrentLocationEventListener {
    final static String TAG = "MapTAG";

    public static String currentLocation;

    private FloatingActionButton action_mic;

    MapView mMapView;
    ViewGroup mMapViewContainer;
    RecyclerView recyclerView;
    EditText mSearchEdit;

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab1,fab_input;
    RelativeLayout mLoaderLayout;
    SpeechRecognizer mRecognizer;

    MapPoint currentMapPoint;
    private double mCurrentLng; //Long = X, Lat = Y???
    private double mCurrentLat;
    private double mSearchLng = -1;
    private double mSearchLat = -1;
    public String mSearchName;
    public String mSearchAddress;
    boolean isTrackingMode = false; //????????? ???????????? (3?????? ?????? ???????????? ?????? ????????? ?????? true?????? stop ?????? ????????? false??? ??????)
    Bus bus = BusProvider.getInstance();

    ArrayList<Document> documentArrayList = new ArrayList<>(); //????????? ?????? ?????? ?????????
    MapPOIItem searchMarker = new MapPOIItem();
    public static CurrentPlace currentPlace = new CurrentPlace();

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO};

    String intentSearchPlace;

    Intent sttIntent;
//    SpeechRecognizer mRecognizer;
    TextToSpeech tts;
    Button sttBtn;
    EditText txtSystem;
    EditText txtInMsg;
    Context cThis;

    Boolean currentCheck = false;
    final int PERMISSION = 1;
    public static Boolean trigger = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        bus.register(this); //????????? ??????
        txtInMsg = (EditText) findViewById(R.id.txtInMsg);
        txtSystem = (EditText) findViewById(R.id.txtSystem);
        sttBtn = (Button)findViewById(R.id.sttStart);
        cThis = this;

        setStt();
        startWithTD();

    }

    @Override
    public void onStart() {
        initView();

//        Handler mHandler = new Handler();
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("?????????????????????????????????");
//                currentLocation = Util.getCompleteAddressString(getApplicationContext(),mCurrentLat,mCurrentLng).replace("\n","");
//                currentPlace.setCurrentLocation(currentLocation);
//
//            }
//        },7000);

        intentSearchPlace="";
        Intent intent = getIntent();
        intentSearchPlace = intent.getStringExtra("search_place_scene");
        //intentSearchPlace = "??????????????? ???????????????";
        if (!Util.isStringEmpty(intentSearchPlace)) {
            System.out.println("????????????????????????????????????????????????");
            mSearchEdit.setText(intentSearchPlace);
            recyclerView.setVisibility(View.GONE);
        }
        super.onStart();
    }

    @Override
    public void onResume() {
        setStt();
        startWithTD();
        super.onResume();
    }

    public void startWithTD(){
        //????????? ???????????? ???????????? 1????????? ?????? ?????? ??????
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("?????? ?????? ?????? ??????");
                txtSystem.setText("?????? ?????????--?????? ??????-----------"+"\r\n"+txtSystem.getText());
                sttBtn.performClick();
            }
        },1000);
    }


    public void setStt() {
        //????????????
        System.out.println("startRecognizer");
        Log.i("Re","start??????");
        sttIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        sttIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getApplicationContext().getPackageName());
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");//????????? ??????
        mRecognizer=SpeechRecognizer.createSpeechRecognizer(cThis);
        mRecognizer.setRecognitionListener(listener);
        System.out.println("startRecognizer");
    }


    public RecognitionListener listener=new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            System.out.println("onREADY");
            txtSystem.setText("onReadyForSpeech..........."+"\r\n"+txtSystem.getText());
        }

        @Override
        public void onBeginningOfSpeech() {
            System.out.println("onBEGINNING");
            txtSystem.setText("???????????? ?????? ????????????..........."+"\r\n"+txtSystem.getText());
        }

        @Override
        public void onRmsChanged(float v) {
            System.out.println("onRms");
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
            txtSystem.setText("onBufferReceived..........."+"\r\n"+txtSystem.getText());
        }

        @Override
        public void onEndOfSpeech() {
            txtSystem.setText("onEndOfSpeech..........."+"\r\n"+txtSystem.getText());
            System.out.println("onEndOfSpeech");
        }

        @Override
        public void onError(int i) {
            txtSystem.setText("????????? ?????? ?????? ?????????..........."+"\r\n"+txtSystem.getText());
            System.out.println("onError"+i);
            mRecognizer.startListening(sttIntent);
        }

        @Override
        public void onResults(Bundle results) {
            String key= "";
            trigger = false;
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult =results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            if (checkTriggerWord(mResult)) trigger = true;
            if (trigger == true) {
                mRecognizer.destroy();


                currentLocation = Util.getCompleteAddressString(getApplicationContext(),mCurrentLat,mCurrentLng).replace("\n","");
                currentPlace.setCurrentLocation(currentLocation);
                System.out.println("currentLocation : "+currentLocation);
                Intent show_intent = new Intent(getApplicationContext(), ShowDataActivity.class);
                show_intent.putExtra("currentLocation",currentLocation);
                startActivity(show_intent);

            }
            mResult.toArray(rs);
            System.out.println("trigger "+trigger);
            //System.out.println(rs[0]+"\r\n"+txtInMsg.getText()+trigger+"stt result");
            txtInMsg.setText(rs[0]+"\r\n"+txtInMsg.getText()+trigger);
            mRecognizer.startListening(sttIntent);

        }

        public Boolean checkTriggerWord(ArrayList<String> values){
            for (String v : values){
                if (v.equals("??????")) return true;
                if (v.equals("??????")) return true;
                if (v.equals("??????")) return true;
                if (v.equals("??????")) return true;
                if (v.equals("??????")) return true;

                if (v.equals("?????????")) return true;
                if (v.equals("?????????")) return true;
                if (v.equals("?????????")) return true;
                if (v.equals("?????????")) return true;
                if (v.equals("?????????")) return true;

                if (v.equals("?????????")) return true;
                if (v.equals("?????????")) return true;
                if (v.equals("?????????")) return true;
                if (v.equals("?????????")) return true;
                if (v.equals("?????????")) return true;

                if (v.equals("?????? ???")) return true;
                if (v.equals("?????? ???")) return true;
                if (v.equals("?????? ???")) return true;
                if (v.equals("?????? ???")) return true;
                if (v.equals("?????? ???")) return true;

                if (v.equals("?????? ???")) return true;
                if (v.equals("?????? ???")) return true;
                if (v.equals("?????? ???")) return true;
                if (v.equals("?????? ???")) return true;
                if (v.equals("?????? ???")) return true;

                if (v.equals("??????")) return true;
                if (v.equals("??????")) return true;

            }
            return false;
        }

        @Override
        public void onPartialResults(Bundle bundle) {
            txtSystem.setText("onPartialResults..........."+"\r\n"+txtSystem.getText());
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
            txtSystem.setText("onEvent..........."+"\r\n"+txtSystem.getText());
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        Toast toast = Toast.makeText(getApplicationContext(),"", Toast.LENGTH_LONG);

        switch(item.getItemId())
        {
            case R.id.menu1:
                Intent intent = new Intent(getApplicationContext(), StarActivity.class);
                startActivity(intent);
                break;
            case R.id.menu2:
                Intent intent2 = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        // ???????????????
        mSearchEdit = findViewById(R.id.map_et_search);
        fab1 = findViewById(R.id.fab1);
        fab_input = findViewById(R.id.fab_input);
        action_mic = findViewById(R.id.Action_Mic);



        //stopTrackingFab = findViewById(R.id.fab_stop_tracking);
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mMapView = new MapView(this);
        mMapViewContainer = findViewById(R.id.map_view);
        mMapViewContainer.addView(mMapView);
        recyclerView = findViewById(R.id.map_recyclerview);
        LocationAdapter locationAdapter = new LocationAdapter(documentArrayList, getApplicationContext(), mSearchEdit, recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false); //????????????????????? ??????
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL)); //??????????????? ??????
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(locationAdapter);

        // ??? ?????????
        mMapView.setMapViewEventListener(this);
        mMapView.setPOIItemEventListener(this);

        mMapView.setOpenAPIKeyAuthenticationResultListener(this);

        //???????????????
        fab1.setOnClickListener(this);
        fab_input.setOnClickListener(this);
        action_mic.setOnClickListener(this);
        sttBtn.setOnClickListener(this);
        //stopTrackingFab.setOnClickListener(this);

        //??? ????????? (???????????? ????????????)
        mMapView.setCurrentLocationEventListener(this);
        if (!checkLocationServiceStatus()){
            showDialogForLocationServiceSetting();}
        else{
            checkRunTimePermission();
        }

        // ????????? ?????? ??????
        checkRecordPermission();

        //setCurrentLocationTrackingMode (????????? ???????????? ?????? ???????????? ???????????????.)
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        mLoaderLayout.setVisibility(View.VISIBLE);
        currentCheck=true;


        // editText ?????? ??????????????????
        mSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // ???????????? ??????
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() >= 1) {
                    // if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                    documentArrayList.clear();
                    locationAdapter.clear();
                    locationAdapter.notifyDataSetChanged();
                    ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<CategoryResult> call = apiInterface.getSearchLocation(StringResource.getStringResource(ContextStorage.getCtx(),R.string.restapi_key), charSequence.toString(), 15);
                    call.enqueue(new Callback<CategoryResult>() {
                        @Override
                        public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                for (Document document : response.body().getDocuments()) {
                                    locationAdapter.addItem(document);
                                }
                                locationAdapter.notifyDataSetChanged();
                            } else {
                                Log.e("test", response.message());
                            }
                            // ??????????????? ????????????
//                            if (documentArrayList.size()!=0) {
//                                mSearchAddress = documentArrayList.get(0).getAddressName();
//                                System.out.println("search result" + documentArrayList.get(0).getAddressName());
//
//
////                                Handler mHandler = new Handler();
////                                mHandler.postDelayed(new Runnable() {
////                                    @Override
////                                    public void run() {
////                                        System.out.println("????????????????????????????????????");
////                                        //BusProvider.getInstance().post(documentArrayList.get(0));
////                                    }
////                                },3000);
//                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {

                        }
                    });
                    //}
                    //mLastClickTime = SystemClock.elapsedRealtime();
                } else {
                    if (charSequence.length() <= 0) {
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // ????????? ????????? ???
            }
        });

        mSearchEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                } else {
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
        mSearchEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "????????????????????? ????????? ??????????????????", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab1:
                Toast.makeText(this, "??????????????? ??????", Toast.LENGTH_SHORT).show();
                //searchDetailFab.setVisibility(View.GONE);
                mLoaderLayout.setVisibility(View.VISIBLE);
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
                //stopTrackingFab.setVisibility(View.VISIBLE);
                mLoaderLayout.setVisibility(View.GONE);
                break;

            case R.id.fab_input:
                currentLocation = Util.getCompleteAddressString(getApplicationContext(),mCurrentLat,mCurrentLng).replace("\n","");
                currentPlace.setCurrentLocation(currentLocation);
                System.out.println("currentLocation : "+currentLocation);
                Intent intent = new Intent(getApplicationContext(), InputActivity.class);
                intent.putExtra("currentLocation",currentLocation);
                searchMarker.setAlpha(0);
                mRecognizer.destroy();
                startActivity(intent);
                break;

            case R.id.Action_Mic:
                //tts.stop();
                //tts.shutdown();
                mRecognizer.destroy();
                currentLocation = Util.getCompleteAddressString(getApplicationContext(),mCurrentLat,mCurrentLng).replace("\n","");
                currentPlace.setCurrentLocation(currentLocation);
                System.out.println("currentLocation : "+currentLocation);
                Intent show_intent = new Intent(getApplicationContext(), ShowDataActivity.class);
                show_intent.putExtra("currentLocation",currentLocation);
//                Intent intent_show = new Intent(getApplicationContext(), ShowDataActivity.class);
                startActivity(show_intent);
                break;

            case R.id.sttStart:
                System.out.println("???????????? ??????!");
                if(ContextCompat.checkSelfPermission(cThis, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.RECORD_AUDIO},1);
                    //????????? ???????????? ?????? ??????
                }else{
                    //????????? ????????? ??????
                    try {
                        mRecognizer.startListening(sttIntent);
                        System.out.println("?????? ??????");
                    }catch (SecurityException e){e.printStackTrace();}
                }
                break;
        }
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {
    }

    //??? ?????? ????????? ??????
    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        //???????????????????????? ??????????????? ????????? ???????????????
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onDaumMapOpenAPIKeyAuthenticationResult(MapView mapView, int i, String s) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    // ????????? ???????????? ??????( ?????????????????? ?????? ?????? ?????????????????? ????????? ??????)
    public void showMap(Uri geoLocation) {
        Intent intent;
        try {
            Toast.makeText(getApplicationContext(), "?????????????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
            intent = new Intent(Intent.ACTION_VIEW, geoLocation);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "??????????????? ??????????????? ???????????????. ????????????????????? ????????????.", Toast.LENGTH_SHORT).show();
            intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/details?id=net.daum.android.map&hl=ko"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
        Log.e("test", "test : Call POI");
        double lat = mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude;
        double lng = mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude;
        Toast.makeText(this, mapPOIItem.getItemName(), Toast.LENGTH_SHORT).show();
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this);
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
        builder.setTitle("??????????????????");
        builder.setSingleChoiceItems(new String[]{"?????? ??????", "?????????: ???????????? ??????","?????????: ???????????? ??????"}, 3, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int index) {
                if (index == 0) {
                    //mLoaderLayout.setVisibility(View.VISIBLE);
                    ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<CategoryResult> call = apiInterface.getSearchLocationDetail(StringResource.getStringResource(ContextStorage.getCtx(),R.string.restapi_key), mapPOIItem.getItemName(), String.valueOf(lat), String.valueOf(lng), 1);
                    call.enqueue(new Callback<CategoryResult>() {
                        @Override
                        public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                            mLoaderLayout.setVisibility(View.GONE);
                            if (response.isSuccessful()) {
                                Intent intent = new Intent(MainActivity.this, PlaceDetailActivity.class);
                                assert response.body() != null;
                                intent.putExtra(IntentKey.PLACE_SEARCH_DETAIL_EXTRA, response.body().getDocuments().get(0));
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onFailure(Call<CategoryResult> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "??????????????? ?????? ??????????????? ????????????.", Toast.LENGTH_SHORT).show();
                            mLoaderLayout.setVisibility(View.GONE);
                            Intent intent = new Intent(MainActivity.this, PlaceDetailActivity.class);
                            startActivity(intent);
                        }
                    });
                } else if (index == 1) {
                    mLoaderLayout.setVisibility(View.VISIBLE);
                    ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<CategoryResult> call = apiInterface.getSearchLocationDetail(StringResource.getStringResource(ContextStorage.getCtx(),R.string.restapi_key), mapPOIItem.getItemName(), String.valueOf(lat), String.valueOf(lng), 1);
                    call.enqueue(new Callback<CategoryResult>() {
                        @Override
                        public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                            mLoaderLayout.setVisibility(View.GONE);
                            if (response.isSuccessful()) {
                                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                                assert response.body() != null;
                                intent.putExtra(IntentKey.PLACE_SEARCH_SET_STARTING, response.body().getDocuments().get(0));
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onFailure(Call<CategoryResult> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "??????????????? ?????? ???????????? ?????? ??? ??? ????????????.", Toast.LENGTH_SHORT).show();
                            mLoaderLayout.setVisibility(View.GONE);
                            Intent intent = new Intent(MainActivity.this, InputActivity.class);
                            startActivity(intent);
                        }
                    });
                }
                else if (index == 2 ){
                    mLoaderLayout.setVisibility(View.VISIBLE);
                    ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<CategoryResult> call = apiInterface.getSearchLocationDetail(StringResource.getStringResource(ContextStorage.getCtx(),R.string.restapi_key), mapPOIItem.getItemName(), String.valueOf(lat), String.valueOf(lng), 1);
                    call.enqueue(new Callback<CategoryResult>() {
                        @Override
                        public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                            mLoaderLayout.setVisibility(View.GONE);
                            if (response.isSuccessful()) {
                                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                                assert response.body() != null;
                                intent.putExtra(IntentKey.PLACE_SEARCH_SET_DESTINATION, response.body().getDocuments().get(0));
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onFailure(Call<CategoryResult> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "??????????????? ?????? ???????????? ?????? ??? ??? ????????????.", Toast.LENGTH_SHORT).show();
                            mLoaderLayout.setVisibility(View.GONE);
                            Intent intent = new Intent(MainActivity.this, InputActivity.class);
                            startActivity(intent);
                        }
                    });

                }

            }
        });
        builder.addButton("??????", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        mSearchName = "???????????? ??????";
        mSearchLng = mapPointGeo.longitude;
        mSearchLat = mapPointGeo.latitude;
        mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(mSearchLat, mSearchLng), true);
        searchMarker.setItemName(mSearchName);
        MapPoint mapPoint2 = MapPoint.mapPointWithGeoCoord(mSearchLat, mSearchLng);
        searchMarker.setMapPoint(mapPoint2);
        searchMarker.setMarkerType(MapPOIItem.MarkerType.BluePin); // ???????????? ???????????? BluePin ?????? ??????.
        searchMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // ????????? ???????????????, ???????????? ???????????? RedPin ?????? ??????.
        searchMarker.setDraggable(true);
        mMapView.addPOIItem(searchMarker);
    }

    //???????????? ????????? ????????? ????????????
    @Subscribe
    public void search(Document document) {
        //public?????? ???????????????
        Log.i("OTTO","ottobus event");
        Toast.makeText(getApplicationContext(), document.getPlaceName() + " ??????", Toast.LENGTH_SHORT).show();
        System.out.println("search ????????? ???????????? ??????");
        mSearchAddress = document.getAddressName();
        mSearchName = document.getPlaceName();
        mSearchLng = Double.parseDouble(document.getX());
        mSearchLat = Double.parseDouble(document.getY());
        mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(mSearchLat, mSearchLng), true);
        mMapView.removePOIItem(searchMarker);
        searchMarker.setItemName(mSearchName);
        searchMarker.setTag(10000);
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(mSearchLat, mSearchLng);
        searchMarker.setMapPoint(mapPoint);
        searchMarker.setMarkerType(MapPOIItem.MarkerType.BluePin); // ???????????? ???????????? BluePin ?????? ??????.
        searchMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // ????????? ???????????????, ???????????? ???????????? RedPin ?????? ??????.
        //?????? ????????? ???????????? ??????
        searchMarker.setDraggable(true);
        mMapView.addPOIItem(searchMarker);
    }

    // ?????? ?????? ???????????? setCurrentLocationEventListener
    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        Log.i(TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
        currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude);
        //??? ????????? ?????? ?????? ??????
        mMapView.setMapCenterPoint(currentMapPoint, true);
        //??????????????? ?????? ?????? ??????
        mCurrentLat = mapPointGeo.latitude;
        mCurrentLng = mapPointGeo.longitude;
        Log.d(TAG, "???????????? => " + mCurrentLat + "  " + mCurrentLng);
        mLoaderLayout.setVisibility(View.GONE);
        //????????? ????????? ?????? ?????? ???????????? ??????????????? ??????, ????????? ?????? ?????????????????? ???????????? ??????????????? ?????? ??????
        if (!isTrackingMode) {
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        }
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {
        Log.i(TAG, "onCurrentLocationUpdateFailed");
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {
        Log.i(TAG, "onCurrentLocationUpdateCancelled");
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
    }
    // ?????? ?????? ?????? ??????
    void checkRunTimePermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED){
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,REQUIRED_PERMISSIONS[0])){
                Toast.makeText(MainActivity.this,"??? ?????? ??????????????? ?????? ?????? ????????? ???????????????.",Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(MainActivity.this,REQUIRED_PERMISSIONS,PERMISSIONS_REQUEST_CODE);
            }else{
                ActivityCompat.requestPermissions(MainActivity.this,REQUIRED_PERMISSIONS,PERMISSIONS_REQUEST_CODE);
            }
        }
    }
    // ????????? ?????? ?????? ??????
    void checkRecordPermission() {
        int hasFineRecordPermission = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.RECORD_AUDIO);
        if (hasFineRecordPermission == PackageManager.PERMISSION_GRANTED){
            if(!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,REQUIRED_PERMISSIONS[1])){
                Toast.makeText(MainActivity.this,"??? ?????? ??????????????? ????????? ?????? ????????? ???????????????.",Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(MainActivity.this,REQUIRED_PERMISSIONS,PERMISSIONS_REQUEST_CODE);
            }else{
                ActivityCompat.requestPermissions(MainActivity.this,REQUIRED_PERMISSIONS,PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    private void showDialogForLocationServiceSetting(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("?????? ????????? ????????????");
        builder.setMessage("?????? ???????????? ?????? ?????? ???????????? ???????????????.");
        builder.setCancelable(true);
        builder.setPositiveButton("??????",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent,GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.create().show();
    }

    public boolean checkLocationServiceStatus(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }




    @Override
    public void finish() {
        super.finish();
        bus.unregister(this);//??????????????? ????????? ????????? ????????????
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mMapView.setShowCurrentLocationMarker(false);
        mRecognizer.destroy();
        System.out.println("destroy main");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}