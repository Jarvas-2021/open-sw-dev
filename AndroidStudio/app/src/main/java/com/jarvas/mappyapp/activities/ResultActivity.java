package com.jarvas.mappyapp.activities;

import static android.net.wifi.p2p.WifiP2pManager.ERROR;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jarvas.mappyapp.Network.Route;
import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.utils.ContextStorage;
import com.jarvas.mappyapp.utils.StringResource;
//import com.jarvas.mappyapp.api.RestApi;

import java.util.List;
import java.util.Locale;

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
    private TextToSpeech tts;

    final static private String ServerUrl = StringResource.getStringResource(ContextStorage.getCtx(), R.string.ServerUrl);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        textViewResult = findViewById(R.id.text_view_result);
        Intent secondIntent = getIntent();
        String startAddressText = secondIntent.getStringExtra("startAddressText");
        String destinationAddressText = secondIntent.getStringExtra("destinationAddressText");

        // TTS를 생성하고 OnInitListener로 초기화 한다.
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

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

                        //todo - if문으로 구분해서 시내버스일때 / 시외버스일때 구분
                        List<Route> routes = response.body();
                        Log.i("RESPONSE","route");
                        Log.i("RESPONSE",routes.toString());
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
                        tts.speak(textViewResult.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

    public interface ServerPost{
        @FormUrlEncoded
        @POST("/android")
        Call <String> sendAddress(@Field("startAddressText") String startAddressText, @Field("destinationAddressText") String destinationAddressText);
    }

    static class RetrofitServiceImplFactory{
        private static Retrofit getretrofit(){
            return new Retrofit.Builder()
                    .baseUrl(ServerUrl)
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
                    .baseUrl(ServerUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson)).build();
        }

        public static Server serverCon2() { return getretrofit2().create(Server.class);}
    }


    public interface Server {
        @GET("/android/api")
        Call<List<Route>> getMlist();
    }

}
