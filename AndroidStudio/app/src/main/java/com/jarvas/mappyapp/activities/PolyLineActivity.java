package com.jarvas.mappyapp.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.jarvas.mappyapp.R;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.util.List;

public class PolyLineActivity extends AppCompatActivity {
    MapPOIItem startMarker = new MapPOIItem();
    MapPOIItem destinationMarker = new MapPOIItem();
    MapView mapView;
    MapPolyline mapPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polyline);
        mapView = new MapView(this);
        mapPolyline = new MapPolyline();
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view_polyline);
        mapViewContainer.addView(mapView);

        String resultSAT = "";
        String resultDAT = "";

        // resultActivity -> MainActivity
        Intent resultIntent = getIntent();
        resultSAT = resultIntent.getStringExtra("startAddressText");
        resultDAT = resultIntent.getStringExtra("destinationAddressText");

        setMarkers(resultSAT,resultDAT);


    }
    public void setMarkers(String resultSAT, String resultDAT) {
        List<Address> startList = null;
        List<Address> destinationList = null;
        Geocoder g = new Geocoder(this);
        try {
            startList = g.getFromLocationName(resultSAT,1);
            destinationList = g.getFromLocationName(resultDAT,1);
            MapPoint startPoint = MapPoint.mapPointWithGeoCoord(startList.get(0).getLatitude(),startList.get(0).getLongitude());
            MapPoint destinationPoint = MapPoint.mapPointWithGeoCoord(destinationList.get(0).getLatitude(),destinationList.get(0).getLongitude());

            startMarker.setItemName("출발지");
            startMarker.setMapPoint(startPoint);
            startMarker.setTag(10001);
            startMarker.setMarkerType(MapPOIItem.MarkerType.YellowPin);
            startMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
            mapView.addPOIItem(startMarker);
            System.out.println("테스트 출발지 됨");

            destinationMarker.setItemName("도착지");
            destinationMarker.setMapPoint(destinationPoint);
            destinationMarker.setTag(10002);
            destinationMarker.setMarkerType(MapPOIItem.MarkerType.YellowPin);
            destinationMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
            mapView.addPOIItem(destinationMarker);

            System.out.println("테스트 도착지 됨");

            mapPolyline.addPoint(startPoint);
            mapPolyline.addPoint(destinationPoint);
            mapView.addPolyline(mapPolyline);
            System.out.println("테스트 poly됨");

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test","입출력 오류 - 주소 변환시 에러 발생");
        }

    }
    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
