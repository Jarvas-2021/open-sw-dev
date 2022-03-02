package com.jarvas.mappyapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jarvas.mappyapp.Network.Route;
import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.crawling_server_api.getServer.RetrofitServiceImplFactoryGetServer;
import com.jarvas.mappyapp.crawling_server_api.postServer.RetrofitServiceImplFactoryPostServer;
import com.jarvas.mappyapp.utils.ContextStorage;
import com.jarvas.mappyapp.utils.StringResource;
//import com.jarvas.mappyapp.api.RestApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class ResultActivity extends AppCompatActivity {
    private TextView textViewResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        textViewResult = findViewById(R.id.text_view_result);
        Intent secondIntent = getIntent();
        String startAddressText = secondIntent.getStringExtra("startAddressText");
        String destinationAddressText = secondIntent.getStringExtra("destinationAddressText");

        Call<String> m = RetrofitServiceImplFactoryPostServer.serverPost().sendAddress(startAddressText,destinationAddressText);
        m.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast.makeText(getApplicationContext(), "서버에 값을 전달했습니다 : " , Toast.LENGTH_SHORT).show();
                Call<List<Route>> m2 = RetrofitServiceImplFactoryGetServer.serverCon2().getMlist();
                System.out.println("여기까지 됨");
                m2.enqueue(new Callback<List<Route>>(){
                    @Override
                    public void onResponse(Call<List<Route>> call, Response<List<Route>> response) {
                        Log.i("RESPONSE","onResponse");
                        if (!response.isSuccessful()) {
                            Log.i("RESPONSE","if문");
                            textViewResult.setText("Code: " + response.code());
                            System.out.println(response.message());
                            return;
                        }

                        List<Route> routes = response.body();
                        Log.i("RESPONSE",routes.toString());
                        getRouteValues(routes);
                    }

                    @Override
                    public void onFailure(Call<List<Route>> call, Throwable t) {
                        textViewResult.setText(t.getMessage());
                        t.printStackTrace();
                    }

                });
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "서버와 통신중 에러가 발생했습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRouteValues(List<Route> routes) {
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
                textViewResult.append(content);
            }
            else {
                String content = "";
                content += "시간 : " + route.getTime() + "\n";
                content += "경로 : " + route.getPath() + "\n";
                content += "요금 : " + route.getPrice() + "\n";
                content += "교통수단 : " + route.getTransType() + "\n";
                content += "교통수단에 따른 시간 : " + route.getInterTime() + "\n";
                textViewResult.append(content);
            }
        }
    }
}
