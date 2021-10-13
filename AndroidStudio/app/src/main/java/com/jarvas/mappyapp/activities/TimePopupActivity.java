package com.jarvas.mappyapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jarvas.mappyapp.R;

import java.util.Calendar;

public class TimePopupActivity extends Activity {

    int h=0, mi=0;
    private TextView textview_st;
    private TextView textview_dt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_popup);

        textview_st = findViewById(R.id.textView_st);
        textview_dt = findViewById(R.id.textView_dt);

        Button stButton = findViewById(R.id.stButton);
        stButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //출발시간 TimePickerDialog 띄우기
                showTime(textview_st);
            }
        });

        Button dtButton = findViewById(R.id.dtButton);
        dtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //도착시간 TimePickerDialog 띄우기
                showTime(textview_dt);
            }
        });

    }


    void showTime(TextView tv) {
        TimePickerDialog.OnTimeSetListener mTimeSetListener =
                new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        tv.setText(hourOfDay+"시"+minute+"분");
                        Toast.makeText(getApplicationContext(),
                                hourOfDay + ":" + minute, Toast.LENGTH_SHORT)
                                .show();
                    }
                };
        TimePickerDialog oDialog = new TimePickerDialog(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog,
                mTimeSetListener, 0, 0, false);
        oDialog.show();
    }

    //확인 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()== MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

}