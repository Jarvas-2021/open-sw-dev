package com.jarvas.mappyapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.models.category_search.Document;
import com.jarvas.mappyapp.utils.IntentKey;

public class PlaceDetailActivity extends AppCompatActivity {
    TextView placeNameText;
    TextView addressText;
    TextView categoryText;
    TextView urlText;
    TextView phoneText;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        placeNameText = findViewById(R.id.placedetail_tv_name);
        addressText = findViewById(R.id.placedetail_tv_address);
        categoryText = findViewById(R.id.placedetail_tv_category);
        urlText = findViewById(R.id.placedetail_tv_url);
        phoneText = findViewById(R.id.placedetail_tv_phone);
        processIntent();
    }

    private void processIntent(){
        Intent processIntent = getIntent();
        Document document = processIntent.getParcelableExtra(IntentKey.PLACE_SEARCH_DETAIL_EXTRA);
        placeNameText.setText(document.getPlaceName());
        addressText.setText(document.getAddressName());
        categoryText.setText(document.getCategoryName());
        urlText.setText(document.getPlaceUrl());
        phoneText.setText(document.getPhone());
    }
}
