package com.jarvas.mappyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView; //바텀 네비게이션 뷰
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNAV);

        //첫화면
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame,new Fragment_map()).commit();

        // 바텀 네비게이션 뷰 안 아이템 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
                switch(menuItem.getItemId()){
                    case R.id.map:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment_map()).commit();
                        break;
                    case R.id.stars:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment_stars()).commit();
                        break;
                    case R.id.setting:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment_setting()).commit();
                        break;
                }
                return true;
            }
        });




        //SearchView searchView = findViewById(R.id.search_view);
        //searchView.bringToFront();

    }

}