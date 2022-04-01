package com.jarvas.mappyapp.activities;

import com.jarvas.mappyapp.models.ResultItem;
import com.jarvas.mappyapp.adapter.ResultRecyclerAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ServerThreadMock extends Thread {
    private String startAddressText;
    private String destinationAddressText;
    private String resultTime;
    private Integer checkTime;
    private ArrayList<ResultItem> mResultItems;
    static public ResultRecyclerAdapter mRecyclerAdapter;

    public ServerThreadMock(String startAddressText, String destinationAddressText, String resultTime, Integer checkTime) {
        this.startAddressText = startAddressText;
        this.destinationAddressText = destinationAddressText;
        this.resultTime = resultTime;
        this.checkTime = checkTime;
    }

    public void run() {
        mRecyclerAdapter = new ResultRecyclerAdapter();
        getRouteValues();
    }

    private void getRouteValues() {
        mResultItems = new ArrayList<>();
        String content = "";
        content += "시간 : " + "1시간 30분" + "\n";
        content += "경로 : " + "경로는 어쩌구저쩌구" + "\n";
        content += "요금 : " + "1250원" + "\n";
        content += "도보 시간 : " + "40분" + "\n";
        content += "환승 : " + "3번" + "\n";
        content += "거리 : " + "130km" + "\n\n";
        System.out.println("지금 content: " + content);

        if (checkTime == 1) {
            content += "예상 도착 시간 : " + convertDateFormatToKoreanString(predictDestinationTime(resultTime, "1시간 30분"));
        } else if (checkTime == 2) {
            content += "예상 출발 시간 : " + convertDateFormatToKoreanString(predictStartTime(resultTime, "1시간 30분"));
        }

        mResultItems.add(new ResultItem(content));

        content = "";
        content += "시간 : " + "1시간 50분" + "\n";
        content += "경로 : " + "경로는 좌짜짜짜짜자자자자자잦쿠카쿠카쿠켘켘어쩌구저쩌구" + "\n";
        content += "요금 : " + "1350원" + "\n";
        content += "도보 시간 : " + "45분" + "\n";
        content += "환승 : " + "2번" + "\n";
        content += "거리 : " + "10km" + "\n\n";

        mResultItems.add(new ResultItem(content));
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

}

