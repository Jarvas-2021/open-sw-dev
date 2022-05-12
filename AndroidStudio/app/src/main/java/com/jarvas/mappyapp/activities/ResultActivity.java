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

        // todo - Dummy Data & Server 변경
        // Call Server
        callServer(startAddressText, destinationAddressText);

        // Dummy Data
        //getRouteValuesDummyData();


//        // TTS를 생성하고 OnInitListener로 초기화 한다.
//        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if (status != ERROR) {
//                    // 언어를 선택한다.
//                    tts.setLanguage(Locale.KOREAN);
//                }
//            }
//        });
//        if (Build.VERSION.SDK_INT >= 23) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO}, PERMISSION);
//        }
        setStt();
        startWithTD();

        // todo - 추후 tts recyclerview 속 data 읽기로 바꾸기
        //tts.speak(textViewResult.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void speakOut() {
        contextStorage.setCheckTTS(false);
        CharSequence text = "";
        tts.setPitch((float) 1); // 음성 톤 높이 지정
        tts.setSpeechRate((float) 1); // 음성 속도 지정

        // 첫 번째 매개변수: 음성 출력을 할 텍스트
        // 두 번째 매개변수: 1. TextToSpeech.QUEUE_FLUSH - 진행중인 음성 출력을 끊고 이번 TTS의 음성 출력
        //                 2. TextToSpeech.QUEUE_ADD - 진행중인 음성 출력이 끝난 후에 이번 TTS의 음성 출력
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id1");
        System.out.println("실행되는중2");


        //tts 사용x
        while (tts.isSpeaking()) {
            System.out.println("아직 말하는중임");
        }
        System.out.println("말 끝남");
        contextStorage.setCheckTTS(true);
        System.out.println("말 끝남" + contextStorage.getCheckTTS());
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("으아아아앙들어왔쓰레드speakout result");
            }
        }, 7000);
        System.out.println("말 끝남2");
        contextStorage.setCheckTTS(true);
        System.out.println("말 끝남2" + contextStorage.getCheckTTS());
    
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void speakOut2(Integer data) {
        contextStorage.setCheckTTS(false);
        //CharSequence text = data;
        tts.setPitch((float) 1); // 음성 톤 높이 지정
        tts.setSpeechRate((float) 1); // 음성 속도 지정

        // 첫 번째 매개변수: 음성 출력을 할 텍스트
        // 두 번째 매개변수: 1. TextToSpeech.QUEUE_FLUSH - 진행중인 음성 출력을 끊고 이번 TTS의 음성 출력
        //                 2. TextToSpeech.QUEUE_ADD - 진행중인 음성 출력이 끝난 후에 이번 TTS의 음성 출력
        tts.speak(mResultItems.get(data).getPath().toString(), TextToSpeech.QUEUE_FLUSH, null, "id2");
        //tts 사용x

//        while (tts.isSpeaking()) {
//            System.out.println("아직 말하는중임");
//        }
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("으아아아앙들어왔쓰레드speakout");
            }
        }, 7000);
        System.out.println("말 끝남2");
        contextStorage.setCheckTTS(true);
        System.out.println("말 끝남2" + contextStorage.getCheckTTS());
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onInit(int status) { // OnInitListener를 통해서 TTS 초기화
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.KOREA); // TTS언어 한국어로 설정

            if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
                Log.e("TTS", "This Language is not supported");
            } else {
                System.out.println("실행되는중");
                speakOut();
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("으아아아앙들어왔쓰레드speakout");
                    }
                }, 7000);
            }
        } else {
            Log.e("TTS", "Initialization Failed!");
        }
    }

    public void startWithTD() {
        //어플이 실행되면 자동으로 1초뒤에 음성 인식 시작
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("자동 음성 인식 시작");
                //txtSystem.setText("어플 실행됨--자동 실행-----------"+"\r\n"+txtSystem.getText());
                sttBtn.performClick();
            }
        }, 5000);
    }


    public void setStt() {
        //음성인식
        System.out.println("startRecognizer");
        Log.i("Re", "start함수");
        sttIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        sttIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplicationContext().getPackageName());
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");//한국어 사용
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
            //txtSystem.setText("지금부터 말을 해주세요..........."+"\r\n"+txtSystem.getText());
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
            //txtSystem.setText("천천히 다시 말해 주세요..........."+"\r\n"+txtSystem.getText());
            System.out.println("onError" + i);
            mRecognizer.startListening(sttIntent);
        }

        public int check_input_start(ArrayList<String> msg) {
            if (msg.contains("일") || msg.contains("첫번째") || msg.contains("1") || msg.contains("위")) {
                return 0;
            }
            if (msg.contains("이") || msg.contains("두번째") || msg.contains("2")) {
                return 1;
            }
            if (msg.contains("삼") || msg.contains("세번째") || msg.contains("3")) {
                return 2;
            }
            if (msg.contains("사") || msg.contains("네번째") || msg.contains("4")) {
                return 3;
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
            switch (check_input_start(mResult)) {
                case 0:
                    num = 0;
                case 1:
                    num = 1;
                case 2:
                    num = 2;
                case 3:
                    num = 3;
            }
            System.out.println("num"+num);
            String[] rs = new String[mResult.size()];
            if (num!=-1) {
                speakOut2(num);
            }
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
                if (v.equals("매피")) return true;
                if (v.equals("맵피")) return true;
                if (v.equals("해피")) return true;
                if (v.equals("웨피")) return true;
                if (v.equals("웹피")) return true;

                if (v.equals("매피야")) return true;
                if (v.equals("맵피야")) return true;
                if (v.equals("해피야")) return true;
                if (v.equals("웨피야")) return true;
                if (v.equals("웹피야")) return true;

                if (v.equals("웨피아")) return true;
                if (v.equals("웨피아")) return true;
                if (v.equals("웨피아")) return true;
                if (v.equals("웨피아")) return true;
                if (v.equals("웹피아")) return true;

                if (v.equals("매피 야")) return true;
                if (v.equals("맵피 야")) return true;
                if (v.equals("해피 야")) return true;
                if (v.equals("웨피 야")) return true;
                if (v.equals("웹피 야")) return true;

                if (v.equals("웨피 아")) return true;
                if (v.equals("웨피 아")) return true;
                if (v.equals("웨피 아")) return true;
                if (v.equals("웨피 아")) return true;
                if (v.equals("웨피 아")) return true;

                if (v.equals("매퍄")) return true;
                if (v.equals("피야")) return true;

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
                Toast.makeText(ContextStorage.getCtx(), "서버에 값을 전달했습니다 : ", Toast.LENGTH_SHORT).show();

                Call<List<Route>> m2 = RetrofitServiceImplFactoryGetServer.serverCon2().getMlist();
                System.out.println("여기까지 됨");
                m2.enqueue(new Callback<List<Route>>() {
                    @Override
                    public void onResponse(Call<List<Route>> call, Response<List<Route>> response) {
                        Log.i("RESPONSE", "onResponse");
                        if (!response.isSuccessful()) {
                            Log.i("RESPONSE", "if문");
                            //textViewResult.setText("Code: " + response.code());
                            Toast.makeText(ContextStorage.getCtx(), "Code: " + response.code(), Toast.LENGTH_SHORT).show();
                            System.out.println(response.message());
                            return;
                        }

                        List<Route> routes = response.body();
                        Log.i("RESPONSE", routes.toString());
                        //todo-주석해제
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
                Toast.makeText(ContextStorage.getCtx(), "서버와 통신중 에러가 발생했습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRouteValues(List<Route> routes) {

        // if (routes의 앞에있는 부분이 0이면 시내버스 )
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

                //1일때는 출발시간이 들어올 때임

                System.out.println("chek하기 : " + checkTimeResult);
                System.out.println("chek하기 : " + resultTimeResult);

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
//        content += "시간 : " + "1시간 30분" + "\n";
//        content += "경로 : " + "경로는 어쩌구저쩌구" + "\n";
//        content += "요금 : " + "1250원" + "\n";
//        content += "도보 시간 : " + "40분" + "\n";
//        content += "환승 : " + "3번" + "\n";
//        content += "거리 : " + "130km" + "\n\n";
//        System.out.println("지금 content: " + content);

        if (checkTimeResult == 1) {
            expect_dt += convertDateFormatToKoreanString(predictDestinationTime(resultTimeResult, "1시간 30분"));
            mResultItems.add(new ResultItem("1시간 30분", "안양역 시외버스터미널 09213\n9,2\n어쩌구저쩌구터미널", "1250원", "", "", "", "시외", "1시간", convertDateFormatToKoreanString(resultTimeResult), expect_dt));
        } else if (checkTimeResult == 2) {
            expect_st += convertDateFormatToKoreanString(predictStartTime(resultTimeResult, "1시간 30분"));
            mResultItems.add(new ResultItem("1시간 30분", "안양역 시외버스터미널 09213\n9,2\n어쩌구저쩌구터미널", "1250원", "", "", "", "시외", "1시간", expect_st, convertDateFormatToKoreanString(resultTimeResult)));
        }

//        content = "";
//        content += "시간 : " + "1시간 50분" + "\n";
//        content += "경로 : " + "경로는 좌짜짜짜짜자자자자자잦쿠카쿠카쿠켘켘어쩌구저쩌구" + "\n";
//        content += "요금 : " + "1350원" + "\n";
//        content += "도보 시간 : " + "45분" + "\n";
//        content += "환승 : " + "2번" + "\n";
//        content += "거리 : " + "10km" + "\n\n";

        expect_st = "";
        expect_dt = "";
        if (checkTimeResult == 1) {
            expect_dt += convertDateFormatToKoreanString(predictDestinationTime(resultTimeResult, "1시간 30분"));
            mResultItems.add(new ResultItem("1시간 50분", "부평역(1호선) 일반급행\n구로역(1호선) 일반급행\n 안양역 정류장 09213\n9,2\n안양2동행정복지센터정류장", "1350원", "45분", "3회", "24.3km", "", "", convertDateFormatToKoreanString(resultTimeResult), expect_dt));
        } else if (checkTimeResult == 2) {
            expect_st += convertDateFormatToKoreanString(predictStartTime(resultTimeResult, "1시간 30분"));
            mResultItems.add(new ResultItem("1시간 50분", "부평역(1호선) 일반급행\n구로역(1호선) 일반급행\n 안양역 정류장 09213\n9,2\n안양2동행정복지센터정류장", "1350원", "45분", "3회", "24.3km", "", "", expect_st, convertDateFormatToKoreanString(resultTimeResult)));
        }


        //mRecyclerAdapter = new ResultRecyclerAdapter();
        //mRecyclerView.setAdapter(mRecyclerAdapter);
        //mRecyclerAdapter.setResultList(mResultItems);
    }

    // StartTime과 크롤링시간을 더해서 예상 도착 시간 알려주는 함수
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

        System.out.println("predictDestinationTime 시간 계산 : " + calendar1.getTime());
        System.out.println("predictDestinationTime 시간 계산 : " + calendar2.getTime());

        calendar1.add(Calendar.HOUR_OF_DAY, calendar2.get(Calendar.HOUR_OF_DAY));
        calendar1.add(Calendar.MINUTE, calendar2.get(Calendar.MINUTE));

        result = format.format(calendar1.getTime());
        System.out.println("predictDestinationTime 시간 계산 : " + result);
        return result;
    }

    // DestinationTime을 받아서 크롤링 시간에서 빼서 예상 출발 시간 알려주는 함수
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

        System.out.println("predictStartTime 시간 계산 : " + calendar1.getTime());
        System.out.println("predictStartTime 시간 계산 : " + calendar2.getTime());

        calendar1.add(Calendar.HOUR_OF_DAY, -calendar2.get(Calendar.HOUR_OF_DAY));
        calendar1.add(Calendar.MINUTE, -calendar2.get(Calendar.MINUTE));

        result = format.format(calendar1.getTime());
        System.out.println("predictStartTime 시간 계산 : " + result);
        return result;
    }

    private String convertStringDateFormat(String data) {
        if (!data.contains("시간")) {
            data = "0시간 " + data;
        }

        data = data.replace("시간 ", ":");
        data = data.replace("분", "");

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
        System.out.println("현재 data : " + dateData);

        DateFormat format = new SimpleDateFormat("a hh:mm", Locale.KOREAN);
        format.format(dateData);

        //dateData = format.format(data);
        System.out.println("format : " + format.format(dateData));
        return format.format(dateData);
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

    @Override
    public void onResume() {
        super.onResume();
        setStt();
        startWithTD();
    }
}
