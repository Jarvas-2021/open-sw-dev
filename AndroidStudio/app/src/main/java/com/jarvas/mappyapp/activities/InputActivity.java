package com.jarvas.mappyapp.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.adapter.LocationAdapter;
import com.jarvas.mappyapp.api.ApiClient;
import com.jarvas.mappyapp.api.ApiInterface;
import com.jarvas.mappyapp.listener.EventListener;
import com.jarvas.mappyapp.model.category_search.CategoryResult;
import com.jarvas.mappyapp.model.category_search.Document;
import com.jarvas.mappyapp.utils.BusProvider;
import com.jarvas.mappyapp.utils.ContextStorage;
import com.jarvas.mappyapp.utils.IntentKey;
import com.jarvas.mappyapp.utils.StringResource;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InputActivity extends AppCompatActivity {
    RecyclerView recyclerView1;
    RecyclerView recyclerView2;
    RecyclerView recyclerView3;
    EditText searchEdit1;
    EditText searchEdit2;
    EditText searchEdit3;
    Button okButton;
    Bus bus = BusProvider.getInstance();
    private ActivityResultLauncher<Intent> resultLauncher;

    String startAddressText;
    String destinationAddressText;
    String wayPointAddressText;
    String mAddressText;
    String mPlaceNameText;

    String searchAddressText;

    Map<String, String> map = new HashMap<String, String>();
    Set<String> set = map.keySet();

    Bus bus2 = BusProvider.getInstance();

    EventListener eventListener = new EventListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        bus2.register(this);
        initView();
        callbackActivity();
        getProcessIntentAndKey();
    }

    public void callbackActivity() {
        //액티비티 콜백 함수
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent intent = result.getData();
                            int CallType = intent.getIntExtra("CallType", 0);
                            if (CallType == 0) {
                                //실행될 코드
                            } else if (CallType == 1) {
                                //실행될 코드
                            } else if (CallType == 2) {
                                //실행될 코드
                            }
                        }
                    }
                });
    }

    public void getProcessIntentAndKey() {
        //Intent 받아오기
        Intent processIntent = getIntent();
        Bundle b = processIntent.getExtras();

        //Key 값 받기
        if (b!=null) {
            Iterator<String> iter = b.keySet().iterator();
            String key = "";
            while (iter.hasNext()) {
                key = iter.next();
            }
            if (key.equals(IntentKey.PLACE_SEARCH_SET_STARTING)) {
                processIntentStarting(processIntent);
            } else if (key.equals(IntentKey.PLACE_SEARCH_SET_DESTINATION)) {
                processIntentDestination(processIntent);
            } else if (key.equals(IntentKey.PLACE_SEARCH_SET_WAYPOINT)) {
                processIntentWayPoint(processIntent);
            }
        }
    }

    public void mOnPopupClick(View v) {
        //데이터 담아서 팝업(액티비티) 호출
        Intent intent = new Intent(getApplicationContext(), TimePopupActivity.class);
        intent.putExtra("CallType", 1);
        resultLauncher.launch(intent);
    }

    private void initView() {
        //바인딩
        searchEdit1 = findViewById(R.id.editText);  //출발지
        searchEdit2 = findViewById(R.id.editText3); //도착지
        searchEdit3 = findViewById(R.id.editText5); //경유지
        recyclerView1 = findViewById(R.id.recyclerview1);
        recyclerView2 = findViewById(R.id.recyclerview2);
        recyclerView3 = findViewById(R.id.recyclerview3);
        okButton = findViewById(R.id.okButton);

        ArrayList<Document> documentArrayList = new ArrayList<>(); //지역명 검색 결과 리스트
        LocationAdapter locationAdapter = new LocationAdapter(documentArrayList, getApplicationContext(), searchEdit1, recyclerView1);
        LocationAdapter locationAdapter2 = new LocationAdapter(documentArrayList, getApplicationContext(), searchEdit2, recyclerView2);
        LocationAdapter locationAdapter3 = new LocationAdapter(documentArrayList, getApplicationContext(), searchEdit3, recyclerView3);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false); //레이아웃매니저
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false); //레이아웃매니저
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false); //레이아웃매니저

        recyclerView1.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL)); //아래구분선 세팅
        recyclerView1.setLayoutManager(layoutManager);
        recyclerView1.setAdapter(locationAdapter);

        recyclerView2.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL)); //아래구분선 세팅
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView2.setAdapter(locationAdapter2);

        recyclerView3.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL)); //아래구분선 세팅
        recyclerView3.setLayoutManager(layoutManager3);
        recyclerView3.setAdapter(locationAdapter3);

        // 검색 텍스처 Listener
        eventListener.addTextChangedListenerEvent(searchEdit1, recyclerView1, documentArrayList, locationAdapter);
        eventListener.addTextChangedListenerEvent(searchEdit2,recyclerView2,documentArrayList,locationAdapter2);
        eventListener.addTextChangedListenerEvent(searchEdit3,recyclerView3,documentArrayList,locationAdapter3);

        // setOnFocusChangeListener
        eventListener.setOnFocusChangeListenerEvent(searchEdit1,recyclerView1);
        eventListener.setOnFocusChangeListenerEvent(searchEdit2,recyclerView2);
        eventListener.setOnFocusChangeListenerEvent(searchEdit3,recyclerView3);

        // setOnClickListener
        eventListener.setOnClickListenerEvent(searchEdit1);
        eventListener.setOnClickListenerEvent(searchEdit2);
        eventListener.setOnClickListenerEvent(searchEdit3);

        okButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.i("BUTTON","okButton click");
                getAddressText();
                System.out.println("StartAddress: "+startAddressText+"DestAddress: "+destinationAddressText+"WayAddress: "+ wayPointAddressText);
                putIntentAndStartActivity();
            }
        });
    }

    public void getAddressText() {
        for(String str:set){
            //출발지
            if (Objects.equals(map.get(str),searchEdit1.getText().toString())){
                startAddressText = str;
            }
            //도착지
            if (Objects.equals(map.get(str),searchEdit2.getText().toString())){
                destinationAddressText = str;
            }
            //경유지
            if (Objects.equals(map.get(str),searchEdit3.getText().toString())){
                wayPointAddressText = str;
            }
        }
    }

    public void putIntentAndStartActivity() {
        Intent intent = new Intent(InputActivity.this, ResultActivity.class);
        intent.putExtra("startAddressText",startAddressText);
        intent.putExtra("destinationAddressText",destinationAddressText);
        startActivity(intent);
    }


    //검색예시 클릭시 이벤트 오토버스
    @Subscribe
    public void search(Document document) {
        //public항상 붙여줘야함
        Log.i("OTTO","ottobus event input activity");
        Toast.makeText(getApplicationContext(), document.getPlaceName() + " 검색", Toast.LENGTH_SHORT).show();
        System.out.println("search 이벤트 오토버스 실행 input");
        mAddressText = document.getAddressName();
        mPlaceNameText = document.getPlaceName();
        map.put(mAddressText, mPlaceNameText);
    }


    @Override
    public void finish() {
        super.finish();
        bus2.unregister(this); //이액티비티 떠나면 정류소 해제해줌
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private void processIntentStarting(Intent intent) {
        if (intent != null) {
            Document document = intent.getParcelableExtra(IntentKey.PLACE_SEARCH_SET_STARTING);
            if (document != null) {
                getDocumentValues(document,searchEdit1, startAddressText, recyclerView1);
            }
        }
    }

    private void processIntentDestination(Intent intent) {
        if (intent != null) {
            Document document = intent.getParcelableExtra(IntentKey.PLACE_SEARCH_SET_DESTINATION);
            if (document != null) {
                getDocumentValues(document,searchEdit2, destinationAddressText, recyclerView2);
            }
        }
    }
    private void processIntentWayPoint(Intent intent) {
        if (intent != null) {
            Document document = intent.getParcelableExtra(IntentKey.PLACE_SEARCH_SET_WAYPOINT);
            if (document != null) {
                getDocumentValues(document,searchEdit3, wayPointAddressText, recyclerView3);
            }
        }
    }

    private void getDocumentValues(Document document, EditText searchEdit, String addressText, RecyclerView recyclerView) {
        searchEdit.setText(document.getPlaceName());
        addressText = document.getAddressName();
        recyclerView.setVisibility(View.GONE);
    }

}
