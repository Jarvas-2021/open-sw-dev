package com.jarvas.mappyapp.crawling_server_api.getServer;

import com.jarvas.mappyapp.models.Route;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CrawlingGetServer {
    @GET("/android/api")
    Call<List<Route>> getMlist();
}
