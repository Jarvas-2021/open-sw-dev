package com.jarvas.mappyapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.utils.ContextStorage;

public class SettingActivity extends AppCompatActivity {
    Button ossButton;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ossButton = findViewById(R.id.btn_opensource_license);
        ossButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                OssLicensesMenuActivity.setActivityTitle("오픈소스 라이센스");
                startActivity(new Intent(SettingActivity.this,OssLicensesMenuActivity.class));
            }
        });


    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ContextStorage.getCtx(), MainActivity.class);
        ContextStorage.getCtx().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

    }
}
