package com.jarvas.mappyapp.activities;

import static com.jarvas.mappyapp.activities.ServerThreadMock.mRecyclerAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jarvas.mappyapp.Network.Route;
import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.ResultRecyclerAdapter;
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
    private RecyclerView mRecyclerView;
    //private ResultRecyclerAdapter mRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mRecyclerView = findViewById(R.id.result_recyclerView);

        //mRecyclerAdapter = new ResultRecyclerAdapter();
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerAdapter.setResultList(mResultItems);

    }

}
