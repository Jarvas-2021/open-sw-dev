package com.jarvas.mappyapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.adapter.ResultRecyclerAdapter;
import com.jarvas.mappyapp.crawling_server_api.getServer.RetrofitServiceImplFactoryGetServer;
import com.jarvas.mappyapp.crawling_server_api.postServer.RetrofitServiceImplFactoryPostServer;
import com.jarvas.mappyapp.models.ResultItem;
import com.jarvas.mappyapp.models.Route;
import com.jarvas.mappyapp.utils.ContextStorage;
import com.jarvas.mappyapp.utils.StringResource;
import com.jarvas.mappyapp.utils.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    //private TextView textViewResult;
    private TextToSpeech tts;

    final static private String ServerUrl = StringResource.getStringResource(ContextStorage.getCtx(), R.string.ServerUrl);

    private RecyclerView mRecyclerView;
    private ArrayList<ResultItem> mResultItems;
    private ResultRecyclerAdapter mRecyclerAdapter;

    private String resultTimeResult;
    private Integer checkTimeResult;

    private com.jarvas.mappyapp.dialog.ProgressDialog customProgressDialog;


    EditText txtSystem;
    EditText txtInMsg;
    Button sttBtn;
    Context cThis;
    public static Boolean triggerinput = false;
    Intent sttIntent;
    SpeechRecognizer mRecognizer;
    ContextStorage contextStorage = new ContextStorage();


    //private ResultRecyclerAdapter mRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        //textViewResult = findViewById(R.id.exampletextview);
        checkTimeResult = 0;
        Intent secondIntent = getIntent();
        String startAddressText = secondIntent.getStringExtra("startAddressText");
        String destinationAddressText = secondIntent.getStringExtra("destinationAddressText");
        String resultTime = secondIntent.getStringExtra("resultTime");
        Integer checkTime = secondIntent.getExtras().getInt("checkTime");

        cThis = this;
        txtInMsg = (EditText) findViewById(R.id.txtInMsg);
        txtSystem = (EditText) findViewById(R.id.txtSystem);
        sttBtn = (Button) findViewById(R.id.sttStart);

        tts = new TextToSpeech(this, this);


        customProgressDialog = new com.jarvas.mappyapp.dialog.ProgressDialog(ResultActivity.this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        resultTimeResult = resultTime;
        if (checkTime == 1 || checkTime == 0) {
            checkTimeResult = 1;
        } else {
            checkTimeResult = 2;
        }

        mRecyclerView = findViewById(R.id.result_recyclerView);

        System.out.println("11111111111111");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        System.out.println("12222222222");
        mResultItems = new ArrayList<>();
        System.out.println("33333333333");

        // todo - Dummy Data & Server ??????
        // Call Server
        callServer(startAddressText, destinationAddressText);

        // Dummy Data
        //getRouteValuesDummyData();


//        // TTS??? ???????????? OnInitListener??? ????????? ??????.
//        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if (status != ERROR) {
//                    // ????????? ????????????.
//                    tts.setLanguage(Locale.KOREAN);
//                }
//            }
//        });
//        if (Build.VERSION.SDK_INT >= 23) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO}, PERMISSION);
//        }
        setStt();
        startWithTD();

        // todo - ?????? tts recyclerview ??? data ????????? ?????????
        //tts.speak(textViewResult.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void speakOut() {
        contextStorage.setCheckTTS(false);
        CharSequence text = "";
        tts.setPitch((float) 1); // ?????? ??? ?????? ??????
        tts.setSpeechRate((float) 1); // ?????? ?????? ??????

        // ??? ?????? ????????????: ?????? ????????? ??? ?????????
        // ??? ?????? ????????????: 1. TextToSpeech.QUEUE_FLUSH - ???????????? ?????? ????????? ?????? ?????? TTS??? ?????? ??????
        //                 2. TextToSpeech.QUEUE_ADD - ???????????? ?????? ????????? ?????? ?????? ?????? TTS??? ?????? ??????
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id1");
        System.out.println("???????????????2");


        //tts ??????x
        while (tts.isSpeaking()) {
            System.out.println("?????? ???????????????");
        }
        System.out.println("??? ??????");
        contextStorage.setCheckTTS(true);
        System.out.println("??? ??????" + contextStorage.getCheckTTS());
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("?????????????????????????????????speakout result");
            }
        }, 7000);
        System.out.println("??? ??????2");
        contextStorage.setCheckTTS(true);
        System.out.println("??? ??????2" + contextStorage.getCheckTTS());

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void speakOut2(Integer data) {
        contextStorage.setCheckTTS(false);
        //CharSequence text = data;
        tts.setPitch((float) 1); // ?????? ??? ?????? ??????
        tts.setSpeechRate((float) 1); // ?????? ?????? ??????

        // ??? ?????? ????????????: ?????? ????????? ??? ?????????
        // ??? ?????? ????????????: 1. TextToSpeech.QUEUE_FLUSH - ???????????? ?????? ????????? ?????? ?????? TTS??? ?????? ??????
        //                 2. TextToSpeech.QUEUE_ADD - ???????????? ?????? ????????? ?????? ?????? ?????? TTS??? ?????? ??????

        if (data==4) {
            tts.speak("?????? ?????????"+mResultItems.get(0).getTime().toString()+"?????????.", TextToSpeech.QUEUE_FLUSH, null, "id3");
        }
        else {
            tts.speak(mResultItems.get(data).getPath().toString(), TextToSpeech.QUEUE_FLUSH, null, "id2");
        }

        //tts ??????x

//        while (tts.isSpeaking()) {
//            System.out.println("?????? ???????????????");
//        }
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("?????????????????????????????????speakout");
            }
        }, 7000);
        System.out.println("??? ??????2");
        contextStorage.setCheckTTS(true);
        System.out.println("??? ??????2" + contextStorage.getCheckTTS());
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onInit(int status) { // OnInitListener??? ????????? TTS ?????????
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.KOREA); // TTS?????? ???????????? ??????

            if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
                Log.e("TTS", "This Language is not supported");
            } else {
                System.out.println("???????????????");
                speakOut();
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("?????????????????????????????????speakout");
                    }
                }, 7000);
            }
        } else {
            Log.e("TTS", "Initialization Failed!");
        }
    }

    public void startWithTD() {
        //????????? ???????????? ???????????? 1????????? ?????? ?????? ??????
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("?????? ?????? ?????? ??????");
                //txtSystem.setText("?????? ?????????--?????? ??????-----------"+"\r\n"+txtSystem.getText());
                sttBtn.performClick();
            }
        }, 5000);
    }


    public void setStt() {
        //????????????
        System.out.println("startRecognizer");
        Log.i("Re", "start??????");
        sttIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        sttIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplicationContext().getPackageName());
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");//????????? ??????
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(cThis);
        mRecognizer.setRecognitionListener(listener);
        mRecognizer.startListening(sttIntent);
        System.out.println("startRecognizer");
    }


    public RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            System.out.println("onREADY");
            //txtSystem.setText("onReadyForSpeech..........."+"\r\n"+txtSystem.getText());
        }

        @Override
        public void onBeginningOfSpeech() {
            System.out.println("onBEGINNING");
            //txtSystem.setText("???????????? ?????? ????????????..........."+"\r\n"+txtSystem.getText());
        }

        @Override
        public void onRmsChanged(float v) {
            System.out.println("onRms result");
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
            //txtSystem.setText("onBufferReceived..........."+"\r\n"+txtSystem.getText());
        }

        @Override
        public void onEndOfSpeech() {
            //txtSystem.setText("onEndOfSpeech..........."+"\r\n"+txtSystem.getText());
            System.out.println("onEndOfSpeech");
        }

        @Override
        public void onError(int i) {
            //txtSystem.setText("????????? ?????? ?????? ?????????..........."+"\r\n"+txtSystem.getText());
            System.out.println("onError" + i);
            mRecognizer.startListening(sttIntent);
        }

        public int check_input_start(ArrayList<String> msg) {
            if (msg.contains("??????") || msg.contains("?????????") || msg.contains("1") || msg.contains("???")) {
                System.out.println("????????? input start");
                return 0;
            }
            if (msg.contains("??????") || msg.contains("?????????") || msg.contains("2")) {
                System.out.println("????????? input start");
                return 1;
            }
            if (msg.contains("??????") || msg.contains("?????????") || msg.contains("3")) {
                System.out.println("????????? input start");
                return 2;
            }
            if (msg.contains("??????") || msg.contains("?????????") || msg.contains("4")) {
                System.out.println("????????? input start");
                return 3;
            }
            if (msg.contains("?????????") || msg.contains("??????")) {
                return 4;
            }
            if (msg.contains("??????") || msg.contains("??????")) {
                return 5;
            }

            return 0;
        }

        @Override
        public void onResults(Bundle results) {
            String key = "";
            triggerinput = false;

            key = SpeechRecognizer.RESULTS_RECOGNITION;
            int num = -1;
            ArrayList<String> mResult = results.getStringArrayList(key);
            System.out.println("mresult"+mResult);
            if (mResult.get(0).contains("??????") || mResult.get(0).contains("?????????") || mResult.get(0).contains("??? ??????") || mResult.get(0).contains("??????") ||mResult.get(0).contains("??? ?????? ?????? ???")) {
                System.out.println("????????? input start");
                num= 0;
            }
            if (mResult.get(0).contains("??????") || mResult.get(0).contains("?????????") ) {
                System.out.println("????????? input start");
                num= 1;
            }
            if (mResult.get(0).contains("??????") || mResult.get(0).contains("?????????") ) {
                System.out.println("????????? input start");
                num= 2;
            }
            if (mResult.get(0).contains("??????") || mResult.get(0).contains("?????????") ) {
                System.out.println("????????? input start");
                num= 3;
            }
            if (mResult.get(0).contains("?????????") || mResult.get(0).contains("??????")) {
                num= 4;
            }

//            switch (check_input_start(mResult)) {
//                case 0:
//                    num = 0;
//                case 1:
//                    num = 1;
//                case 2:
//                    num = 2;
//                case 3:
//                    num = 3;
//                case 4:
//                    num = 4;
//            }
            System.out.println("num"+num);
            String[] rs = new String[mResult.size()];
            if (num!=-1) {
                speakOut2(num);
            }
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.out.println("?????????????????????????????????");

                }
            },6000);

            mRecognizer.startListening(sttIntent);
            if (checkTriggerWord(mResult)) triggerinput = true;
            if (triggerinput == true) {

                //mRecognizer.destroy();


                //currentLocation = Util.getCompleteAddressString(getApplicationContext(),mCurrentLat,mCurrentLng).replace("\n","");
                //currentPlace.setCurrentLocation(currentLocation);
                //System.out.println("currentLocation : "+currentLocation);
                //Intent show_intent = new Intent(getApplicationContext(), ShowDataActivity.class);
                //show_intent.putExtra("currentLocation",currentLocation);
                //startActivity(show_intent);

            }
            mResult.toArray(rs);
            //System.out.println("trigger "+trigger);
            //System.out.println(rs[0]+"\r\n"+txtInMsg.getText()+trigger+"stt result");
            //txtInMsg.setText(rs[0]+"\r\n"+txtInMsg.getText()+trigger);
            //mRecognizer.startListening(sttIntent);

        }

        public Boolean checkTriggerWord(ArrayList<String> values) {
            for (String v : values) {
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
            //txtSystem.setText("onPartialResults..........."+"\r\n"+txtSystem.getText());
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
            //txtSystem.setText("onEvent..........."+"\r\n"+txtSystem.getText());
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);

        switch (item.getItemId()) {
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


    private void callServer(String startAddressText, String destinationAddressText) {
        customProgressDialog.show();
        Call<String> m = RetrofitServiceImplFactoryPostServer.serverPost().sendAddress(startAddressText, destinationAddressText);
        m.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast.makeText(ContextStorage.getCtx(), "????????? ?????? ?????????????????? : ", Toast.LENGTH_SHORT).show();

                Call<List<Route>> m2 = RetrofitServiceImplFactoryGetServer.serverCon2().getMlist();
                System.out.println("???????????? ???");
                m2.enqueue(new Callback<List<Route>>() {
                    @Override
                    public void onResponse(Call<List<Route>> call, Response<List<Route>> response) {
                        Log.i("RESPONSE", "onResponse");
                        if (!response.isSuccessful()) {
                            Log.i("RESPONSE", "if???");
                            //textViewResult.setText("Code: " + response.code());
                            Toast.makeText(ContextStorage.getCtx(), "Code: " + response.code(), Toast.LENGTH_SHORT).show();
                            System.out.println(response.message());
                            return;
                        }

                        List<Route> routes = response.body();
                        Log.i("RESPONSE", routes.toString());
                        //todo-????????????
                        getRouteValues(routes);
                        customProgressDialog.dismiss();
                        mRecyclerAdapter = new ResultRecyclerAdapter(startAddressText, destinationAddressText);
                        mRecyclerView.setAdapter(mRecyclerAdapter);
                        mRecyclerAdapter.setResultList(mResultItems);
                    }

                    @Override
                    public void onFailure(Call<List<Route>> call, Throwable t) {
                        //textViewResult.setText(t.getMessage());
                        Toast.makeText(ContextStorage.getCtx(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }

                });
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(ContextStorage.getCtx(), "????????? ????????? ????????? ??????????????????", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRouteValues(List<Route> routes) {

        // if (routes??? ???????????? ????????? 0?????? ???????????? )
        for (Route route : routes) {
            if (route.getId() == 0) {
                String time = "";
                String path = "";
                String price = "";
                String walktime = "";
                String transfer = "";
                String distance = "";

                String expect_st = "";
                String expect_dt = "";

                time += route.getTime();
                path += route.getPath();
                price += route.getPrice();
                walktime += route.getWalkTime();
                transfer += route.getTransfer();
                distance += route.getDistance();
                //textViewResult.append(content);

                //1????????? ??????????????? ????????? ??????

                System.out.println("chek?????? : " + checkTimeResult);
                System.out.println("chek?????? : " + resultTimeResult);

                if (checkTimeResult == 1) {
                    expect_dt += convertDateFormatToKoreanString(predictDestinationTime(resultTimeResult, route.getTime()));
                    mResultItems.add(new ResultItem(time, path, price, walktime, transfer, distance, "", "", convertDateFormatToKoreanString(resultTimeResult), expect_dt));
                } else if (checkTimeResult == 2) {
                    expect_st += convertDateFormatToKoreanString(predictStartTime(resultTimeResult, route.getTime()));
                    mResultItems.add(new ResultItem(time, path, price, walktime, transfer, distance, "", "", expect_st, convertDateFormatToKoreanString(resultTimeResult)));
                }

            } else {
                String time = "";
                String path = "";
                String price = "";
                String transType = "";
                String interTime = "";

                String expect_st = "";
                String expect_dt = "";
                time += route.getTime();
                path += route.getPath();
                price += route.getPrice();
                transType += route.getTransType();
                interTime += route.getInterTime();
                //textViewResult.append(content);
                if (checkTimeResult == 1) {
                    expect_dt += convertDateFormatToKoreanString(predictDestinationTime(resultTimeResult, route.getTime()));
                    mResultItems.add(new ResultItem(time, path, price, "", "", "", transType, interTime, convertDateFormatToKoreanString(resultTimeResult), expect_dt));
                } else if (checkTimeResult == 2) {
                    expect_st += convertDateFormatToKoreanString(predictStartTime(resultTimeResult, route.getTime()));
                    mResultItems.add(new ResultItem(time, path, price, "", "", "", transType, interTime, expect_st, convertDateFormatToKoreanString(resultTimeResult)));
                }

            }
        }
    }

    private void getRouteValuesDummyData() {
        mResultItems = new ArrayList<>();
        String expect_st = "";
        String expect_dt = "";
//        content += "?????? : " + "1?????? 30???" + "\n";
//        content += "?????? : " + "????????? ??????????????????" + "\n";
//        content += "?????? : " + "1250???" + "\n";
//        content += "?????? ?????? : " + "40???" + "\n";
//        content += "?????? : " + "3???" + "\n";
//        content += "?????? : " + "130km" + "\n\n";
//        System.out.println("?????? content: " + content);

        if (checkTimeResult == 1) {
            expect_dt += convertDateFormatToKoreanString(predictDestinationTime(resultTimeResult, "1?????? 30???"));
            mResultItems.add(new ResultItem("1?????? 30???", "????????? ????????????????????? 09213\n9,2\n???????????????????????????", "1250???", "", "", "", "??????", "1??????", convertDateFormatToKoreanString(resultTimeResult), expect_dt));
        } else if (checkTimeResult == 2) {
            expect_st += convertDateFormatToKoreanString(predictStartTime(resultTimeResult, "1?????? 30???"));
            mResultItems.add(new ResultItem("1?????? 30???", "????????? ????????????????????? 09213\n9,2\n???????????????????????????", "1250???", "", "", "", "??????", "1??????", expect_st, convertDateFormatToKoreanString(resultTimeResult)));
        }

//        content = "";
//        content += "?????? : " + "1?????? 50???" + "\n";
//        content += "?????? : " + "????????? ????????????????????????????????????????????????????????????????????????" + "\n";
//        content += "?????? : " + "1350???" + "\n";
//        content += "?????? ?????? : " + "45???" + "\n";
//        content += "?????? : " + "2???" + "\n";
//        content += "?????? : " + "10km" + "\n\n";

        expect_st = "";
        expect_dt = "";
        if (checkTimeResult == 1) {
            expect_dt += convertDateFormatToKoreanString(predictDestinationTime(resultTimeResult, "1?????? 30???"));
            mResultItems.add(new ResultItem("1?????? 50???", "?????????(1??????) ????????????\n?????????(1??????) ????????????\n ????????? ????????? 09213\n9,2\n??????2??????????????????????????????", "1350???", "45???", "3???", "24.3km", "", "", convertDateFormatToKoreanString(resultTimeResult), expect_dt));
        } else if (checkTimeResult == 2) {
            expect_st += convertDateFormatToKoreanString(predictStartTime(resultTimeResult, "1?????? 30???"));
            mResultItems.add(new ResultItem("1?????? 50???", "?????????(1??????) ????????????\n?????????(1??????) ????????????\n ????????? ????????? 09213\n9,2\n??????2??????????????????????????????", "1350???", "45???", "3???", "24.3km", "", "", expect_st, convertDateFormatToKoreanString(resultTimeResult)));
        }


        //mRecyclerAdapter = new ResultRecyclerAdapter();
        //mRecyclerView.setAdapter(mRecyclerAdapter);
        //mRecyclerAdapter.setResultList(mResultItems);
    }

    // StartTime??? ?????????????????? ????????? ?????? ?????? ?????? ???????????? ??????
    private String predictDestinationTime(String startTime, String crawlingTime) {
        String result;
        crawlingTime = convertStringDateFormat(crawlingTime);

        DateFormat format = new SimpleDateFormat("HH:mm");
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        try {
            calendar1.setTime(format.parse(startTime));
            calendar2.setTime(format.parse(crawlingTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("predictDestinationTime ?????? ?????? : " + calendar1.getTime());
        System.out.println("predictDestinationTime ?????? ?????? : " + calendar2.getTime());

        calendar1.add(Calendar.HOUR_OF_DAY, calendar2.get(Calendar.HOUR_OF_DAY));
        calendar1.add(Calendar.MINUTE, calendar2.get(Calendar.MINUTE));

        result = format.format(calendar1.getTime());
        System.out.println("predictDestinationTime ?????? ?????? : " + result);
        return result;
    }

    // DestinationTime??? ????????? ????????? ???????????? ?????? ?????? ?????? ?????? ???????????? ??????
    private String predictStartTime(String destinationTime, String crawlingTime) {
        String result;
        crawlingTime = convertStringDateFormat(crawlingTime);

        DateFormat format = new SimpleDateFormat("HH:mm");
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        try {
            calendar1.setTime(format.parse(destinationTime));
            calendar2.setTime(format.parse(crawlingTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("predictStartTime ?????? ?????? : " + calendar1.getTime());
        System.out.println("predictStartTime ?????? ?????? : " + calendar2.getTime());

        calendar1.add(Calendar.HOUR_OF_DAY, -calendar2.get(Calendar.HOUR_OF_DAY));
        calendar1.add(Calendar.MINUTE, -calendar2.get(Calendar.MINUTE));

        result = format.format(calendar1.getTime());
        System.out.println("predictStartTime ?????? ?????? : " + result);
        return result;
    }

    private String convertStringDateFormat(String data) {
        if (!data.contains("??????")) {
            data = "0?????? " + data;
        }

        data = data.replace("?????? ", ":");
        data = data.replace("???", "");

        return data;
    }

    private String convertDateFormatToKoreanString(String data) {
        DateFormat df = new SimpleDateFormat("HH:mm");
        Date dateData = null;
        try {
            dateData = df.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("?????? data : " + dateData);

        DateFormat format = new SimpleDateFormat("a hh:mm", Locale.KOREAN);
        format.format(dateData);

        //dateData = format.format(data);
        System.out.println("format : " + format.format(dateData));
        return format.format(dateData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TTS ????????? ??????????????? ????????? ???????????? ??????????????? ????????????.
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setStt();
        startWithTD();
    }
}
