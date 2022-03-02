package com.jarvas.mappyapp.crawling_server_api.getServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.activities.ResultActivity;
import com.jarvas.mappyapp.utils.ContextStorage;
import com.jarvas.mappyapp.utils.StringResource;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitServiceImplFactoryGetServer {
    final static private String ServerUrl = StringResource.getStringResource(ContextStorage.getCtx(), R.string.ServerUrl);
    private static Retrofit getretrofit2(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        return new Retrofit.Builder()
                .baseUrl(ServerUrl)
                .addConverterFactory(GsonConverterFactory.create(gson)).build();
    }

    public static CrawlingGetServer serverCon2() { return getretrofit2().create(CrawlingGetServer.class);}
}
