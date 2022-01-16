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
//import com.jarvas.mappyapp.api.RestApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

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

        Call<String> m = ResultActivity.RetrofitServiceImplFactory.serverPost().sendAddress(startAddressText,destinationAddressText);
        m.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast.makeText(getApplicationContext(), "서버에 값을 전달했습니다 : " , Toast.LENGTH_SHORT).show();
                Call<List<Route>> m2 = ResultActivity.RetrofitServiceImplFactory2.serverCon2().getMlist();
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
                        Log.i("RESPONSE","route");
                        Log.i("RESPONSE",routes.toString());
                        for (Route route : routes) {
                            System.out.println("루트 : "+route);
                            String content = "";
                            content += "Time: " + route.getTime() + "\n";
                            content += "WalkTime: " + route.getWalkTime() + "\n";
                            content += "Path: " + route.getPath() + "\n\n";
                            textViewResult.append(content);
                        }
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



        //RestApi restApi = retrofit.create(RestApi.class);











    }

    public interface ServerPost{
        @FormUrlEncoded
        @POST("/android")
        Call <String> sendAddress(@Field("startAddressText") String startAddressText, @Field("destinationAddressText") String destinationAddressText);
    }

    static class RetrofitServiceImplFactory{
        private static Retrofit getretrofit(){
            return new Retrofit.Builder()
                    .baseUrl("http://192.168.0.17:8082")
                    .addConverterFactory(ScalarsConverterFactory.create()).build();
        }

        public static ServerPost serverPost(){
            return getretrofit().create(ServerPost.class);
        }
    }



    static class RetrofitServiceImplFactory2{
        private static Retrofit getretrofit2(){
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            return new Retrofit.Builder()
                    .baseUrl("http://192.168.0.17:8082")
                    .addConverterFactory(GsonConverterFactory.create(gson)).build();
        }

        public static Server serverCon2() { return getretrofit2().create(Server.class);}
    }


    public interface Server {
        @GET("/android/api")
        Call<List<Route>> getMlist();
    }

}
