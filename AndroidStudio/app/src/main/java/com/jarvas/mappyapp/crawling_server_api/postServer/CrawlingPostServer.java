package com.jarvas.mappyapp.crawling_server_api.postServer;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface CrawlingPostServer {
    @FormUrlEncoded
    @POST("/android")
    Call<String> sendAddress(@Field("startAddressText") String startAddressText, @Field("destinationAddressText") String destinationAddressText);
}
