package com.jarvas.mappyapp.activities;

import android.util.Log;
import android.widget.Toast;

import com.jarvas.mappyapp.models.Route;
import com.jarvas.mappyapp.models.ResultItem;
import com.jarvas.mappyapp.adapter.ResultRecyclerAdapter;
import com.jarvas.mappyapp.crawling_server_api.getServer.RetrofitServiceImplFactoryGetServer;
import com.jarvas.mappyapp.crawling_server_api.postServer.RetrofitServiceImplFactoryPostServer;
import com.jarvas.mappyapp.utils.ContextStorage;

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

public class ServerThread extends Thread {
    private String startAddressText;
    private String destinationAddressText;
    private String resultTime;
    private Integer checkTime;
    private ArrayList<ResultItem> mResultItems;
    private ResultRecyclerAdapter mRecyclerAdapter;

    public ServerThread(String startAddressText, String destinationAddressText, String resultTime, Integer checkTime) {
        this.startAddressText = startAddressText;
        this.destinationAddressText = destinationAddressText;
        this.resultTime = resultTime;
        this.checkTime = checkTime;
    }
    public void run() {
        //Intent secondIntent = getIntent();
        //String startAddressText = secondIntent.getStringExtra("startAddressText");
        //String destinationAddressText = secondIntent.getStringExtra("destinationAddressText");

        mRecyclerAdapter = new ResultRecyclerAdapter();

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
                        getRouteValues(routes);
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
        mResultItems = new ArrayList<>();
        // if (routes의 앞에있는 부분이 0이면 시내버스 )
        for (Route route : routes) {
            if (route.getId() == 0) {
                String content = "";
                content += "시간 : " + route.getTime() + "\n";
                content += "경로 : " + route.getPath() + "\n";
                content += "요금 : " + route.getPrice() + "\n";
                content += "도보 시간 : " + route.getWalkTime() + "\n";
                content += "환승 : " + route.getTransfer() + "\n";
                content += "거리 : " + route.getDistance() + "\n\n";
                //textViewResult.append(content);
                if(checkTime==1){
                    content += "예상 도착 시간 : " + convertDateFormatToKoreanString(predictDestinationTime(resultTime,route.getTime()));
                } else if (checkTime==2) {
                    content += "예상 출발 시간 : " + convertDateFormatToKoreanString(predictStartTime(resultTime,route.getTime()));
                }
                //mResultItems.add(new ResultItem(content));
            }
            else {
                String content = "";
                content += "시간 : " + route.getTime() + "\n";
                content += "경로 : " + route.getPath() + "\n";
                content += "요금 : " + route.getPrice() + "\n";
                content += "교통수단 : " + route.getTransType() + "\n";
                content += "교통수단에 따른 시간 : " + route.getInterTime() + "\n";
                //textViewResult.append(content);
                if(checkTime==1){
                    content += "예상 도착 시간 : " + convertDateFormatToKoreanString(predictDestinationTime(resultTime,route.getTime()));
                } else if (checkTime==2) {
                    content += "예상 출발 시간 : " + convertDateFormatToKoreanString(predictStartTime(resultTime,route.getTime()));
                }
                //mResultItems.add(new ResultItem(content));
            }
            mRecyclerAdapter.setResultList(mResultItems);
        }

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
}
