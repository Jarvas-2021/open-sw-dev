package com.jarvas.mappyapp.activities;

import com.jarvas.mappyapp.ResultItem;
import com.jarvas.mappyapp.ResultRecyclerAdapter;

import java.util.ArrayList;

public class ServerThreadMock extends Thread {
    private String startAddressText;
    private String destinationAddressText;
    private ArrayList<ResultItem> mResultItems;
    static public ResultRecyclerAdapter mRecyclerAdapter;

    public ServerThreadMock(String startAddressText, String destinationAddressText) {
        this.startAddressText = startAddressText;
        this.destinationAddressText = destinationAddressText;
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
        mResultItems.add(new ResultItem(content));

        content += "시간 : " + "1시간 50분" + "\n";
        content += "경로 : " + "경로는 좌짜짜짜짜자자자자자잦쿠카쿠카쿠켘켘어쩌구저쩌구" + "\n";
        content += "요금 : " + "1350원" + "\n";
        content += "도보 시간 : " + "45분" + "\n";
        content += "환승 : " + "2번" + "\n";
        content += "거리 : " + "10km" + "\n\n";

        mResultItems.add(new ResultItem(content));
        mRecyclerAdapter.setResultList(mResultItems);

    }
}

