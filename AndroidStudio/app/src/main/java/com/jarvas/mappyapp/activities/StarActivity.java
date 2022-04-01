package com.jarvas.mappyapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.adapter.ResultRecyclerAdapter;
import com.jarvas.mappyapp.adapter.StarAdapter;
import com.jarvas.mappyapp.models.Star;
import com.jarvas.mappyapp.models.database.StarDatabase;

import java.util.List;

public class StarActivity extends AppCompatActivity {

    private List<Star> starList;
    private StarDatabase starDatabase = null;
    private Context mContext = null;
    private StarAdapter starAdapter;
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star);

        mRecyclerView = findViewById(R.id.star_result_recyclerView);
        mContext = getApplicationContext();
        starAdapter = new StarAdapter(starList);

        //DB 생성
        starDatabase = StarDatabase.getInstance(this);

        //thread 사용
        class InsertRunnable implements Runnable {
            @Override
            public void run() {
                try {
                    starList = StarDatabase.getInstance(mContext).starDAO().getStars();
                    starAdapter = new StarAdapter(starList);
                    starAdapter.notifyDataSetChanged();

                    mRecyclerView.setAdapter(starAdapter);
                    LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
                    mRecyclerView.setLayoutManager(mLinearLayoutManager);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        InsertRunnable insertRunnable = new InsertRunnable();
        Thread t = new Thread(insertRunnable);
        t.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StarDatabase.destroyInstance();
        starDatabase = null;
    }
}
