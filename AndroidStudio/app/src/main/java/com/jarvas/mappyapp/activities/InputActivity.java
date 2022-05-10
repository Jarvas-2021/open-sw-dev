package com.jarvas.mappyapp.activities;

import static com.jarvas.mappyapp.Network.Client.client_msg;
import static com.jarvas.mappyapp.activities.MainActivity.currentLocation;
import static com.jarvas.mappyapp.activities.MainActivity.currentPlace;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.Scenario;
import com.jarvas.mappyapp.adapter.LocationAdapter;
import com.jarvas.mappyapp.kakao_api.ApiClient;
import com.jarvas.mappyapp.kakao_api.ApiInterface;
import com.jarvas.mappyapp.listener.EventListener;
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
import com.naver.speech.clientapi.SpeechRecognitionResult;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InputActivity extends Activity {
    private TextToSpeech tts;
    RecyclerView recyclerView1;
    RecyclerView recyclerView2;
    RecyclerView recyclerView3;
    EditText searchEdit1;
    EditText searchEdit2;
    EditText searchEdit3;
    Button okButton;
    Bus bus = BusProvider.getInstance();
    //private ActivityResultLauncher<Intent> resultLauncher;

    String startAddressText;
    String destinationAddressText;
    String wayPointAddressText;
    String mAddressText;
    String mPlaceNameText;

    String startTimeText;
    String destinationTimeText;

    String searchAddressText;

    Integer checkTime = 0;
    String resultTime = "";

    Map<String, String> map = new HashMap<String, String>();
    Set<String> set = map.keySet();

    Bus bus2 = BusProvider.getInstance();

    EventListener eventListener = new EventListener();

    ProgressDialog progressDialog;

    ArrayList<Document> documentArrayList;
    ArrayList<Document> documentArrayList2;
    LocationAdapter locationAdapter;
    LocationAdapter locationAdapter2;
    LinearLayoutManager layoutManager;
    LinearLayoutManager layoutManager2;

    String intentStartPlace;
    String intentDestinationPlace;
    String intentStartTime;
    String intentDestinationTime;

    public boolean textToSpeechIsInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_input);
        bus2.register(this);
        initView();

        // todo - 서버 확인 후 callbackActivity 함수 제거
        //callbackActivity();
        getProcessIntentAndKey();

        Calendar calendar = Calendar.getInstance();
        String current_hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String current_min = String.valueOf(calendar.get(Calendar.MINUTE));

        Button stButton = findViewById(R.id.startTimeButton);
        Button dtButton = findViewById(R.id.destinationTimeButton);

        Intent intent = getIntent();
        intentStartPlace = intent.getStringExtra("start_place_scene");
        intentDestinationPlace = intent.getStringExtra("arrive_place_scene");
        intentStartTime = intent.getStringExtra("start_time_scene");
        intentDestinationTime = intent.getStringExtra("arrive_time_scene");
        //intentStartPlace = "";
        //intentDestinationPlace = "안양천";
        //intentStartTime = "5시30분";
        //intentDestinationTime = "";
//intent.getStringExtra("arrive_place_scene").length()!=0 ||


        // if intentStartTime이 들어오면 checkTime=1로 하고 resultTime 값넣어주기
        // if intentDestTime이 들어오면 checkTime=2로 하고 resultTime에 값넣어주기

        // intentDestinationPlace가 ""아니면 으로 바꾸기
        // 도착지만 받아올때
        if (!Util.isStringEmpty(intentDestinationPlace)) {
            // 검색 텍스처 Listener
            // if intentStartPlace가 ""이면 current SetText추가
            //searchEdit1.setText(currentLocation); //현재값
            if (Util.isStringEmpty(intentStartPlace)) {
                System.out.println("여기실행1");
//                Location loc_current;
//                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//                loc_current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                double cur_lat = loc_current.getLatitude();
//                double cur_lon = loc_current.getLongitude();
//                currentLocation = Util.getCompleteAddressString(getApplicationContext(),cur_lat,cur_lon).replace("\n","");
                currentLocation = currentPlace.getCurrentLocation();
                searchEdit1.setText(currentLocation);
                System.out.println(currentLocation);
                startAddressText = currentLocation;
            }
            else {
                System.out.println("여기실행2"+intentStartTime);
                searchEdit1.setText(intentStartPlace);
            }
            System.out.println("실행으으아ㅡ아ㅡ아으ㅏ으ㅏㅡ아으");
            recyclerView1.setVisibility(View.GONE);
            searchEdit2.setText(intentDestinationPlace);
            recyclerView2.setVisibility(View.GONE);
        }


        stButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //출발시간 TimePickerDialog 띄우기
                showStartTime();
                stButton.setBackgroundResource(R.drawable.icon_active_time);
                dtButton.setBackgroundResource(R.drawable.icon_inactive_time);
            }
        });


        dtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //출발시간 TimePickerDialog 띄우기
                showDestinationTime();
                dtButton.setBackgroundResource(R.drawable.icon_active_time);
                stButton.setBackgroundResource(R.drawable.icon_inactive_time);
            }
        });


    }


    public String convertDataFormat(String d) {
        System.out.println("data1"+d);
        d = d.replace(" ","");
        System.out.println("data2"+d);
        d = d.replace("시",":");
        System.out.println("data3"+d);
        d = d.replace("분","");
        System.out.println("data4"+d);
        return d;
    }

    void showStartTime() {
        Calendar calendar = Calendar.getInstance();
        String current_hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String current_min = String.valueOf(calendar.get(Calendar.MINUTE));
        checkTime = 1;

        TimePickerDialog.OnTimeSetListener mTimeSetListener =
                new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //tv.setText(hourOfDay+"시"+minute+"분");
                        startTimeText = hourOfDay + ":" + minute;
                        Toast.makeText(getApplicationContext(),
                                startTimeText, Toast.LENGTH_SHORT)
                                .show();

                    }
                };
        TimePickerDialog oDialog = new TimePickerDialog(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog,
                mTimeSetListener, Integer.parseInt(current_hour), Integer.parseInt(current_min), false);
        oDialog.show();

    }

    void showDestinationTime() {
        Calendar calendar = Calendar.getInstance();
        String current_hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String current_min = String.valueOf(calendar.get(Calendar.MINUTE));
        checkTime = 2;

        TimePickerDialog.OnTimeSetListener mTimeSetListener =
                new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //tv.setText(hourOfDay+"시"+minute+"분");
                        destinationTimeText = hourOfDay + ":" + minute;
                        Toast.makeText(getApplicationContext(),
                                destinationTimeText, Toast.LENGTH_SHORT)
                                .show();
                    }
                };
        TimePickerDialog oDialog = new TimePickerDialog(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog,
                mTimeSetListener, Integer.parseInt(current_hour), Integer.parseInt(current_min), false);
        oDialog.show();
    }

    //확인 버튼 클릭
//    public void mOnClose(View v) {
//        //데이터 전달하기
//        Intent intent = new Intent();
//        intent.putExtra("startingTime", startTimeText);
//        intent.putExtra("destinationTime", destinationTimeText);
//        setResult(RESULT_OK, intent);
//        //액티비티(팝업) 닫기
//        finish();
//    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }

//    public void callbackActivity() {
//        //액티비티 콜백 함수
//        resultLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        if (result.getResultCode() == RESULT_OK) {
//                            System.out.println("callbackActivity 함수 실행");
//                            Intent intent = result.getData();
//                            int CallType = intent.getIntExtra("CallType", 0);
//                            if (CallType == 0) {
//                                //실행될 코드
//                            } else if (CallType == 1) {
//                                //실행될 코드
//                            } else if (CallType == 2) {
//                                //실행될 코드
//                            }
//                        }
//                    }
//                });
//    }

    private void initView() {
        //바인딩
        searchEdit1 = findViewById(R.id.editText);  //출발지
        searchEdit2 = findViewById(R.id.editText3); //도착지
        recyclerView1 = findViewById(R.id.recyclerview1);
        recyclerView2 = findViewById(R.id.recyclerview2);
        okButton = findViewById(R.id.okButton);

        documentArrayList = new ArrayList<>(); //지역명 검색 결과 리스트
        documentArrayList2 = new ArrayList<>(); //지역명 검색 결과 리스트
        locationAdapter = new LocationAdapter(documentArrayList, getApplicationContext(), searchEdit1, recyclerView1);
        locationAdapter2 = new LocationAdapter(documentArrayList2, getApplicationContext(), searchEdit2, recyclerView2);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView1.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL)); //아래구분선 세팅
        recyclerView1.setLayoutManager(layoutManager);
        recyclerView1.setAdapter(locationAdapter);

        recyclerView2.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL)); //아래구분선 세팅
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView2.setAdapter(locationAdapter2);


        // 검색 텍스처 Listener
        //eventListener.addTextChangedListenerEventStart(searchEdit1, recyclerView1, documentArrayList, locationAdapter);
        //eventListener.addTextChangedListenerEventDestination(searchEdit2, recyclerView2, documentArrayList, locationAdapter2);
        searchEdit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 입력하기 전에
                recyclerView1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() >= 1) {
                    System.out.println("지금실행된다이거");
                    documentArrayList.clear();
                    locationAdapter.clear();
                    locationAdapter.notifyDataSetChanged();
                    ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<CategoryResult> call = apiInterface.getSearchLocation(StringResource.getStringResource(ContextStorage.getCtx(), R.string.restapi_key), charSequence.toString(), 15);
                    call.enqueue(new Callback<CategoryResult>() {
                        @Override
                        public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                for (Document document : response.body().getDocuments()) {
                                    locationAdapter.addItem(document);
                                }
                                locationAdapter.notifyDataSetChanged();

                                if (documentArrayList.size()!=0) {
                                    startAddressText = documentArrayList.get(0).getAddressName();
                                    System.out.println("start result" + documentArrayList.get(0).getAddressName());
                                }
                                if (!Util.isStringEmpty(intentStartPlace)) {
                                    System.out.println("intentstartif"+startAddressText);
                                }
                            } else {
                                Log.e("onResponse ERROR", response.message());
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {

                        }
                    });
                } else {
                    if (charSequence.length() <= 0) {
                        recyclerView1.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 입력이 끝났을 때

            }
        });

        searchEdit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 입력하기 전에
                recyclerView2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() >= 1) {
                    documentArrayList2.clear();
                    locationAdapter2.clear();
                    locationAdapter2.notifyDataSetChanged();
                    ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<CategoryResult> call = apiInterface.getSearchLocation(StringResource.getStringResource(ContextStorage.getCtx(), R.string.restapi_key), charSequence.toString(), 15);
                    call.enqueue(new Callback<CategoryResult>() {
                        @Override
                        public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                for (Document document : response.body().getDocuments()) {
                                    locationAdapter2.addItem(document);
                                }
                                locationAdapter2.notifyDataSetChanged();

                            } else {
                                Log.e("onResponse ERROR", response.message());
                            }

                            // 첫번째걸로 선택해줌
                            if (documentArrayList2.size()!=0) {
                                destinationAddressText = documentArrayList2.get(0).getAddressName();
                                System.out.println("destination result" + documentArrayList2.get(0).getAddressName());
                            }
                            if (!Util.isStringEmpty(intentDestinationPlace)) {
                                System.out.println("intentdestif"+destinationAddressText);
                                if (!Util.isStringEmpty(intentStartTime)) {
                                    checkTime=1;
                                } else if (!Util.isStringEmpty(intentDestinationTime)) {
                                    checkTime=2;
                                }
                                System.out.println("idfsdfsdf"+checkTime);
                                if (checkTime == 1) {
                                    System.out.println("dsfaazzz"+intentStartTime);
                                    resultTime = convertDataFormat(intentStartTime);
                                    System.out.println("sdfsdfsdf"+resultTime);
                                } else if (checkTime == 2) {
                                    resultTime = convertDataFormat(intentDestinationTime);
                                } else if (checkTime == 0) {
                                    resultTime = calculateCurrentTime();
                                }
                                System.out.println("resultTime : " + resultTime);

                                if (destinationAddressText == null) {
                                    Toast.makeText(InputActivity.this, "도착지를 입력해주세요.", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (startAddressText == null) {
                                        startAddressText = currentLocation;
                                    }
                                    putIntentAndStartActivity();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {

                        }
                    });
                } else {
                    if (charSequence.length() <= 0) {
                        recyclerView2.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 입력이 끝났을 때


            }
        });


        // setOnFocusChangeListener
        eventListener.setOnFocusChangeListenerEvent(searchEdit1, recyclerView1);
        eventListener.setOnFocusChangeListenerEvent(searchEdit2, recyclerView2);


        // setOnClickListener
        eventListener.setOnClickListenerEvent(searchEdit1);
        eventListener.setOnClickListenerEvent(searchEdit2);

        // editText 검색 텍스처이벤트


        okButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                Log.i("BUTTON", "okButton click");
                getAddressText();
                System.out.println("StartAddress: " + startAddressText + "DestAddress: " + destinationAddressText);

                System.out.println();

                if (checkTime == 1) {
                    resultTime = startTimeText;
                } else if (checkTime == 2) {
                    resultTime = destinationTimeText;
                } else if (checkTime == 0) {
                    resultTime = calculateCurrentTime();
                }
                System.out.println("resultTime : " + resultTime);

                if (destinationAddressText == null) {
                    Toast.makeText(InputActivity.this, "도착지를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    if (startAddressText == null) {
                        startAddressText = currentLocation;
                    }
                    putIntentAndStartActivity();
                }

                //ServerThread serverThread = new ServerThread(startAddressText,destinationAddressText,resultTime,checkTime);
                //serverThread.run();
                //Thread.State state = serverThread.getState();
                //ServerThreadMock serverThreadMock = new ServerThreadMock(startAddressText, destinationAddressText,resultTime,checkTime);
                //serverThreadMock.run();
                //Thread.State state = serverThreadMock.getState();

            }
        });
    }

    public String calculateCurrentTime() {
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        Date now = new Date();
        String currentTime = sf.format(now);
        return currentTime;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getAddressText() {
        for (String str : set) {
            //출발지
            if (Objects.equals(map.get(str), searchEdit1.getText().toString())) {
                startAddressText = str;
            }
            //도착지
            if (Objects.equals(map.get(str), searchEdit2.getText().toString())) {
                destinationAddressText = str;
            }
        }
    }

    public void getProcessIntentAndKey() {
        //Intent 받아오기
        Intent processIntent = getIntent();
        currentLocation = processIntent.getStringExtra("currentLocation");
        searchEdit1.setText(currentLocation);
        recyclerView1.setVisibility(View.GONE);
        Bundle b = processIntent.getExtras();

        //Key 값 받기
        if (b != null) {
            Iterator<String> iter = b.keySet().iterator();
            String key = "";
            while (iter.hasNext()) {
                key = iter.next();
            }
            switch (key) {
                case IntentKey.PLACE_SEARCH_SET_STARTING:
                    processIntent(processIntent, IntentKey.PLACE_SEARCH_SET_STARTING, searchEdit1, startAddressText, recyclerView1, 1);
                    break;
                case IntentKey.PLACE_SEARCH_SET_DESTINATION:
                    processIntent(processIntent, IntentKey.PLACE_SEARCH_SET_DESTINATION, searchEdit2, destinationAddressText, recyclerView2, 2);
                    break;
            }
        }
    }

    public void startResultActivity() {
        Intent intent = new Intent(InputActivity.this, ResultActivity.class);
        //intent.putExtra("startAddressText", startAddressText);
        //intent.putExtra("destinationAddressText", destinationAddressText);
        startActivity(intent);
    }

    public void putIntentAndStartActivity() {
        Intent intent = new Intent(InputActivity.this, ResultActivity.class);
        intent.putExtra("startAddressText", startAddressText);
        intent.putExtra("destinationAddressText", destinationAddressText);
        intent.putExtra("resultTime", resultTime);
        intent.putExtra("checkTIme", checkTime);
        startActivity(intent);
    }

    private void processIntent(Intent intent, String key, EditText searchEdit, String addressText, RecyclerView recyclerView, Integer check) {
        if (intent != null) {
            Document document = intent.getParcelableExtra(key);
            if (document != null) {
                getDocumentValues(document, searchEdit, addressText, recyclerView, check);
            }
        }
    }

    private void getDocumentValues(Document document, EditText searchEdit, String addressText, RecyclerView recyclerView, Integer check) {
        searchEdit.setText(document.getPlaceName());
        addressText = document.getAddressName();
        if (check == 1) {
            startAddressText = addressText;
        } else if (check == 2) {
            destinationAddressText = addressText;
        }
        recyclerView.setVisibility(View.GONE);
    }

    public void mOnPopupClick(View v, Integer i) {
        //데이터 담아서 팝업(액티비티) 호출
        Intent intent = new Intent(getApplicationContext(), TimePopupActivity.class);
        intent.putExtra("CallType", i);
        //resultLauncher.launch(intent);
        getApplicationContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public void finish() {
        super.finish();
        bus2.unregister(this); //이액티비티 떠나면 정류소 해제해줌
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

    //검색예시 클릭시 이벤트 오토버스
    @Subscribe
    public void search(Document document) {
        //public항상 붙여줘야함
        Log.i("OTTO", "ottobus event input activity");
        Toast.makeText(getApplicationContext(), document.getPlaceName() + " 검색", Toast.LENGTH_SHORT).show();
        System.out.println("search 이벤트 오토버스 실행 input");
        mAddressText = document.getAddressName();
        mPlaceNameText = document.getPlaceName();
        map.put(mAddressText, mPlaceNameText);
    }
}
