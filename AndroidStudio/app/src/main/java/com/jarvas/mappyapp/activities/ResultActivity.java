package com.jarvas.mappyapp.activities;

import static android.net.wifi.p2p.WifiP2pManager.ERROR;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jarvas.mappyapp.dialog.ProgressDialog;
import com.jarvas.mappyapp.models.Route;
import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.models.ResultItem;
import com.jarvas.mappyapp.adapter.ResultRecyclerAdapter;
import com.jarvas.mappyapp.crawling_server_api.getServer.RetrofitServiceImplFactoryGetServer;
import com.jarvas.mappyapp.crawling_server_api.postServer.RetrofitServiceImplFactoryPostServer;
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

public class ResultActivity extends AppCompatActivity {
    //private TextView textViewResult;
    private TextToSpeech tts;

    final static private String ServerUrl = StringResource.getStringResource(ContextStorage.getCtx(), R.string.ServerUrl);

    private RecyclerView mRecyclerView;
    private ArrayList<ResultItem> mResultItems;
    private ResultRecyclerAdapter mRecyclerAdapter;

    private String resultTimeResult;
    private Integer checkTimeResult;

    private com.jarvas.mappyapp.dialog.ProgressDialog customProgressDialog;

    //private ResultRecyclerAdapter mRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        //textViewResult = findViewById(R.id.text_view_result);
        checkTimeResult = 0;
        Intent secondIntent = getIntent();
        String startAddressText = secondIntent.getStringExtra("startAddressText");
        String destinationAddressText = secondIntent.getStringExtra("destinationAddressText");
        String resultTime = secondIntent.getStringExtra("resultTime");
        Integer checkTime = secondIntent.getExtras().getInt("checkTime");

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
        //callServer(startAddressText,destinationAddressText);

        // Dummy Data
        getRouteValuesDummyData();


        // TTS를 생성하고 OnInitListener로 초기화 한다.
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
        
        // todo - 추후 tts recyclerview 속 data 읽기로 바꾸기
        //tts.speak(textViewResult.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
    }

    private void callServer(String startAddressText, String destinationAddressText) {
        customProgressDialog.show();
        Call<String> m = RetrofitServiceImplFactoryPostServer.serverPost().sendAddress(startAddressText,destinationAddressText);
        m.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast.makeText(ContextStorage.getCtx(), "서버에 값을 전달했습니다 : " , Toast.LENGTH_SHORT).show();

                Call<List<Route>> m2 = RetrofitServiceImplFactoryGetServer.serverCon2().getMlist();
                System.out.println("여기까지 됨");
                m2.enqueue(new Callback<List<Route>>(){
                    @Override
                    public void onResponse(Call<List<Route>> call, Response<List<Route>> response) {
                        Log.i("RESPONSE","onResponse");
                        if (!response.isSuccessful()) {
                            Log.i("RESPONSE","if문");
                            //textViewResult.setText("Code: " + response.code());
                            Toast.makeText(ContextStorage.getCtx(), "Code: "+response.code(), Toast.LENGTH_SHORT).show();
                            System.out.println(response.message());
                            return;
                        }

                        List<Route> routes = response.body();
                        Log.i("RESPONSE",routes.toString());
                        //todo-주석해제
                        //getRouteValues(routes);
                        customProgressDialog.dismiss();
                        mRecyclerAdapter = new ResultRecyclerAdapter();
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

                String expect_st="";
                String expect_dt="";

                time += route.getTime() + "\n";
                path += route.getPath() + "\n";
                price += route.getPrice() + "\n";
                walktime += route.getWalkTime() + "\n";
                transfer += route.getTransfer() + "\n";
                distance += route.getDistance() + "\n\n";
                //textViewResult.append(content);

                //1일때는 출발시간이 들어올 때임
                if(checkTimeResult==1){
                    expect_dt += convertDateFormatToKoreanString(predictDestinationTime(resultTimeResult,route.getTime()));
                    mResultItems.add(new ResultItem(time,path,price,walktime,transfer,distance,"","",convertDateFormatToKoreanString(resultTimeResult),expect_dt));
                } else if (checkTimeResult==2) {
                    expect_st += convertDateFormatToKoreanString(predictStartTime(resultTimeResult,route.getTime()));
                    mResultItems.add(new ResultItem(time,path,price,walktime,transfer,distance,"","",expect_st,convertDateFormatToKoreanString(resultTimeResult)));
                }

            }
            else {
                String time = "";
                String path = "";
                String price = "";
                String transType = "";
                String interTime = "";

                String expect_st="";
                String expect_dt="";
                time += "시간 : " + route.getTime() + "\n";
                path += "경로 : " + route.getPath() + "\n";
                price += "요금 : " + route.getPrice() + "\n";
                transType += "교통수단 : " + route.getTransType() + "\n";
                interTime += "교통수단에 따른 시간 : " + route.getInterTime() + "\n";
                //textViewResult.append(content);
                if(checkTimeResult==1){
                    expect_dt += "예상 도착 시간 : " + convertDateFormatToKoreanString(predictDestinationTime(resultTimeResult,route.getTime()));
                    mResultItems.add(new ResultItem(time,path,price,"","","",transType,interTime,convertDateFormatToKoreanString(resultTimeResult),expect_dt));
                } else if (checkTimeResult==2) {
                    expect_st += "예상 출발 시간 : " + convertDateFormatToKoreanString(predictStartTime(resultTimeResult,route.getTime()));
                    mResultItems.add(new ResultItem(time,path,price,"","","",transType,interTime,expect_st,convertDateFormatToKoreanString(resultTimeResult)));
                }

            }
        }
    }

    private void getRouteValuesDummyData() {
        mResultItems = new ArrayList<>();
        String expect_st="";
        String expect_dt="";
//        content += "시간 : " + "1시간 30분" + "\n";
//        content += "경로 : " + "경로는 어쩌구저쩌구" + "\n";
//        content += "요금 : " + "1250원" + "\n";
//        content += "도보 시간 : " + "40분" + "\n";
//        content += "환승 : " + "3번" + "\n";
//        content += "거리 : " + "130km" + "\n\n";
//        System.out.println("지금 content: " + content);

        if (checkTimeResult == 1) {
            expect_dt +=convertDateFormatToKoreanString(predictDestinationTime(resultTimeResult, "1시간 30분"));
            mResultItems.add(new ResultItem("1시간 30분","안양역 시외버스터미널 09213\n9,2\n어쩌구저쩌구터미널","1250원","","","","시외","1시간",convertDateFormatToKoreanString(resultTimeResult),expect_dt));
        } else if (checkTimeResult == 2) {
            expect_st +=convertDateFormatToKoreanString(predictStartTime(resultTimeResult, "1시간 30분"));
            mResultItems.add(new ResultItem("1시간 30분","안양역 시외버스터미널 09213\n9,2\n어쩌구저쩌구터미널","1250원","","","","시외","1시간",expect_st,convertDateFormatToKoreanString(resultTimeResult)));
        }

//        content = "";
//        content += "시간 : " + "1시간 50분" + "\n";
//        content += "경로 : " + "경로는 좌짜짜짜짜자자자자자잦쿠카쿠카쿠켘켘어쩌구저쩌구" + "\n";
//        content += "요금 : " + "1350원" + "\n";
//        content += "도보 시간 : " + "45분" + "\n";
//        content += "환승 : " + "2번" + "\n";
//        content += "거리 : " + "10km" + "\n\n";

        expect_st="";
        expect_dt="";
        if (checkTimeResult == 1) {
            expect_dt += convertDateFormatToKoreanString(predictDestinationTime(resultTimeResult, "1시간 30분"));
            mResultItems.add(new ResultItem("1시간 50분","부평역(1호선) 일반급행\n구로역(1호선) 일반급행\n 안양역 정류장 09213\n9,2\n안양2동행정복지센터정류장","1350원","45분","3회","24.3km","","",convertDateFormatToKoreanString(resultTimeResult),expect_dt));
        } else if (checkTimeResult == 2) {
            expect_st += convertDateFormatToKoreanString(predictStartTime(resultTimeResult, "1시간 30분"));
            mResultItems.add(new ResultItem("1시간 50분","부평역(1호선) 일반급행\n구로역(1호선) 일반급행\n 안양역 정류장 09213\n9,2\n안양2동행정복지센터정류장","1350원","45분","3회","24.3km","","",expect_st,convertDateFormatToKoreanString(resultTimeResult)));
        }



        mRecyclerAdapter = new ResultRecyclerAdapter();
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerAdapter.setResultList(mResultItems);
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

        System.out.println("predictDestinationTime 시간 계산 : "+calendar1.getTime());
        System.out.println("predictDestinationTime 시간 계산 : "+calendar2.getTime());

        calendar1.add(Calendar.HOUR_OF_DAY,calendar2.get(Calendar.HOUR_OF_DAY));
        calendar1.add(Calendar.MINUTE,calendar2.get(Calendar.MINUTE));

        result = format.format(calendar1.getTime());
        System.out.println("predictDestinationTime 시간 계산 : "+result);
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

        System.out.println("predictStartTime 시간 계산 : "+calendar1.getTime());
        System.out.println("predictStartTime 시간 계산 : "+calendar2.getTime());

        calendar1.add(Calendar.HOUR_OF_DAY,-calendar2.get(Calendar.HOUR_OF_DAY));
        calendar1.add(Calendar.MINUTE,-calendar2.get(Calendar.MINUTE));

        result = format.format(calendar1.getTime());
        System.out.println("predictStartTime 시간 계산 : "+result);
        return result;
    }

    private String convertStringDateFormat(String data) {
        data = data.replace("시간 ",":");
        data = data.replace("분","");
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
}
