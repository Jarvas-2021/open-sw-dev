package com.jarvas.mappyapp.crawling_server_api.postServer;

import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.utils.ContextStorage;
import com.jarvas.mappyapp.utils.StringResource;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitServiceImplFactoryPostServer {
    final static private String ServerUrl = StringResource.getStringResource(ContextStorage.getCtx(), R.string.ServerUrl);
    private static Retrofit getretrofit(){
        return new Retrofit.Builder()
                .baseUrl(ServerUrl)
                .addConverterFactory(ScalarsConverterFactory.create()).build();
    }

    public static CrawlingPostServer serverPost(){
        return getretrofit().create(CrawlingPostServer.class);
    }
}
