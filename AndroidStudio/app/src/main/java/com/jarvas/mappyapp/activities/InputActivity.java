package com.jarvas.mappyapp.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.adapter.LocationAdapter;
import com.jarvas.mappyapp.listener.EventListener;
import com.jarvas.mappyapp.models.category_search.Document;
import com.jarvas.mappyapp.utils.BusProvider;
import com.jarvas.mappyapp.utils.IntentKey;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class InputActivity extends Activity {
    RecyclerView recyclerView1;
    RecyclerView recyclerView2;
    RecyclerView recyclerView3;
    EditText searchEdit1;
    EditText searchEdit2;
    EditText searchEdit3;
    Button okButton;
    Bus bus = BusProvider.getInstance();
    //private ActivityResultLauncher<Intent> resultLauncher;

    String startAddressText;
    String destinationAddressText;
    String wayPointAddressText;
    String mAddressText;
    String mPlaceNameText;

    String startTimeText;
    String destinationTimeText;

    String searchAddressText;

    Integer checkTime=0;
    String resultTime="";

    Map<String, String> map = new HashMap<String, String>();
    Set<String> set = map.keySet();

    Bus bus2 = BusProvider.getInstance();

    EventListener eventListener = new EventListener();

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_input);
        bus2.register(this);
        initView();
        // todo - 서버 확인 후 callbackActivity 함수 제거
        //callbackActivity();
        getProcessIntentAndKey();

        Calendar calendar = Calendar.getInstance();
        String current_hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String current_min = String.valueOf(calendar.get(Calendar.MINUTE));

        Button stButton = findViewById(R.id.startTimeButton);
        Button dtButton = findViewById(R.id.destinationTimeButton);

        stButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //출발시간 TimePickerDialog 띄우기
                showStartTime();
                stButton.setBackgroundResource(R.drawable.icon_active_time);
                dtButton.setBackgroundResource(R.drawable.icon_inactive_time);
            }
        });


        dtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //출발시간 TimePickerDialog 띄우기
                showDestinationTime();
                dtButton.setBackgroundResource(R.drawable.icon_active_time);
                stButton.setBackgroundResource(R.drawable.icon_inactive_time);
            }
        });

    }

    void showStartTime() {
        Calendar calendar = Calendar.getInstance();
        String current_hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String current_min = String.valueOf(calendar.get(Calendar.MINUTE));
        checkTime = 1;

        TimePickerDialog.OnTimeSetListener mTimeSetListener =
                new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //tv.setText(hourOfDay+"시"+minute+"분");
                        startTimeText = hourOfDay + ":" + minute;
                        Toast.makeText(getApplicationContext(),
                                startTimeText, Toast.LENGTH_SHORT)
                                .show();

                    }
                };
        TimePickerDialog oDialog = new TimePickerDialog(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog,
                mTimeSetListener, Integer.parseInt(current_hour), Integer.parseInt(current_min), false);
        oDialog.show();

    }

    void showDestinationTime() {
        Calendar calendar = Calendar.getInstance();
        String current_hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String current_min = String.valueOf(calendar.get(Calendar.MINUTE));
        checkTime = 2;

        TimePickerDialog.OnTimeSetListener mTimeSetListener =
                new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //tv.setText(hourOfDay+"시"+minute+"분");
                        destinationTimeText = hourOfDay + ":" + minute;
                        Toast.makeText(getApplicationContext(),
                                destinationTimeText, Toast.LENGTH_SHORT)
                                .show();
                    }
                };
        TimePickerDialog oDialog = new TimePickerDialog(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog,
                mTimeSetListener, Integer.parseInt(current_hour), Integer.parseInt(current_min), false);
        oDialog.show();
    }

    //확인 버튼 클릭
//    public void mOnClose(View v) {
//        //데이터 전달하기
//        Intent intent = new Intent();
//        intent.putExtra("startingTime", startTimeText);
//        intent.putExtra("destinationTime", destinationTimeText);
//        setResult(RESULT_OK, intent);
//        //액티비티(팝업) 닫기
//        finish();
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }

//    public void callbackActivity() {
//        //액티비티 콜백 함수
//        resultLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        if (result.getResultCode() == RESULT_OK) {
//                            System.out.println("callbackActivity 함수 실행");
//                            Intent intent = result.getData();
//                            int CallType = intent.getIntExtra("CallType", 0);
//                            if (CallType == 0) {
//                                //실행될 코드
//                            } else if (CallType == 1) {
//                                //실행될 코드
//                            } else if (CallType == 2) {
//                                //실행될 코드
//                            }
//                        }
//                    }
//                });
//    }

    private void initView() {
        //바인딩
        searchEdit1 = findViewById(R.id.editText);  //출발지
        searchEdit2 = findViewById(R.id.editText3); //도착지
        recyclerView1 = findViewById(R.id.recyclerview1);
        recyclerView2 = findViewById(R.id.recyclerview2);
        okButton = findViewById(R.id.okButton);

        ArrayList<Document> documentArrayList = new ArrayList<>(); //지역명 검색 결과 리스트
        LocationAdapter locationAdapter = new LocationAdapter(documentArrayList, getApplicationContext(), searchEdit1, recyclerView1);
        LocationAdapter locationAdapter2 = new LocationAdapter(documentArrayList, getApplicationContext(), searchEdit2, recyclerView2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView1.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL)); //아래구분선 세팅
        recyclerView1.setLayoutManager(layoutManager);
        recyclerView1.setAdapter(locationAdapter);

        recyclerView2.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL)); //아래구분선 세팅
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView2.setAdapter(locationAdapter2);


        // 검색 텍스처 Listener
        eventListener.addTextChangedListenerEvent(searchEdit1, recyclerView1, documentArrayList, locationAdapter);
        eventListener.addTextChangedListenerEvent(searchEdit2, recyclerView2, documentArrayList, locationAdapter2);
        //eventListener.addTextChangedListenerEvent(searchEdit3,recyclerView3,documentArrayList,locationAdapter3);

        // setOnFocusChangeListener
        eventListener.setOnFocusChangeListenerEvent(searchEdit1, recyclerView1);
        eventListener.setOnFocusChangeListenerEvent(searchEdit2, recyclerView2);
        //eventListener.setOnFocusChangeListenerEvent(searchEdit3,recyclerView3);

        // setOnClickListener
        eventListener.setOnClickListenerEvent(searchEdit1);
        eventListener.setOnClickListenerEvent(searchEdit2);
        //eventListener.setOnClickListenerEvent(searchEdit3);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("BUTTON", "okButton click");
                getAddressText();
                System.out.println("StartAddress: " + startAddressText + "DestAddress: " + destinationAddressText);

                System.out.println();

                if(checkTime==1){
                    resultTime=startTimeText;
                }else if (checkTime==2){
                    resultTime=destinationTimeText;
                }else if (checkTime==0) {
                    resultTime=calculateCurrentTime();
                }
                System.out.println("resultTime : " +resultTime);

                if (startAddressText==null) {
                    Toast.makeText(InputActivity.this, "출발지를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (destinationAddressText==null) {
                    Toast.makeText(InputActivity.this, "도착지를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    putIntentAndStartActivity();
                }

                //ServerThread serverThread = new ServerThread(startAddressText,destinationAddressText,resultTime,checkTime);
                //serverThread.run();
                //Thread.State state = serverThread.getState();
                //ServerThreadMock serverThreadMock = new ServerThreadMock(startAddressText, destinationAddressText,resultTime,checkTime);
                //serverThreadMock.run();
                //Thread.State state = serverThreadMock.getState();

            }
        });
    }

    public String calculateCurrentTime() {
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        Date now = new Date();
        String currentTime = sf.format(now);
        return currentTime;
    }

    public void getAddressText() {
        for (String str : set) {
            //출발지
            if (Objects.equals(map.get(str), searchEdit1.getText().toString())) {
                startAddressText = str;
            }
            //도착지
            if (Objects.equals(map.get(str), searchEdit2.getText().toString())) {
                destinationAddressText = str;
            }
        }
    }

    public void getProcessIntentAndKey() {
        //Intent 받아오기
        Intent processIntent = getIntent();
        Bundle b = processIntent.getExtras();

        //Key 값 받기
        if (b != null) {
            Iterator<String> iter = b.keySet().iterator();
            String key = "";
            while (iter.hasNext()) {
                key = iter.next();
            }
            switch (key) {
                case IntentKey.PLACE_SEARCH_SET_STARTING:
                    processIntent(processIntent, IntentKey.PLACE_SEARCH_SET_STARTING, searchEdit1, startAddressText, recyclerView1);
                    break;
                case IntentKey.PLACE_SEARCH_SET_DESTINATION:
                    processIntent(processIntent, IntentKey.PLACE_SEARCH_SET_DESTINATION, searchEdit2, destinationAddressText, recyclerView2);
                    break;
            }
        }
    }

    public void startResultActivity() {
        Intent intent = new Intent(InputActivity.this, ResultActivity.class);
        //intent.putExtra("startAddressText", startAddressText);
        //intent.putExtra("destinationAddressText", destinationAddressText);
        startActivity(intent);
    }

    public void putIntentAndStartActivity() {
        Intent intent = new Intent(InputActivity.this, ResultActivity.class);
        intent.putExtra("startAddressText", startAddressText);
        intent.putExtra("destinationAddressText", destinationAddressText);
        intent.putExtra("resultTime",resultTime);
        intent.putExtra("checkTIme",checkTime);
        startActivity(intent);
    }

    private void processIntent(Intent intent, String key, EditText searchEdit, String addressText, RecyclerView recyclerView) {
        if (intent != null) {
            Document document = intent.getParcelableExtra(key);
            if (document != null) {
                getDocumentValues(document, searchEdit, addressText, recyclerView);
            }
        }
    }

    private void getDocumentValues(Document document, EditText searchEdit, String addressText, RecyclerView recyclerView) {
        searchEdit.setText(document.getPlaceName());
        addressText = document.getAddressName();
        recyclerView.setVisibility(View.GONE);
    }

    public void mOnPopupClick(View v, Integer i) {
        //데이터 담아서 팝업(액티비티) 호출
        Intent intent = new Intent(getApplicationContext(), TimePopupActivity.class);
        intent.putExtra("CallType", i);
        //resultLauncher.launch(intent);
        getApplicationContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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

    //검색예시 클릭시 이벤트 오토버스
    @Subscribe
    public void search(Document document) {
        //public항상 붙여줘야함
        Log.i("OTTO", "ottobus event input activity");
        Toast.makeText(getApplicationContext(), document.getPlaceName() + " 검색", Toast.LENGTH_SHORT).show();
        System.out.println("search 이벤트 오토버스 실행 input");
        mAddressText = document.getAddressName();
        mPlaceNameText = document.getPlaceName();
        map.put(mAddressText, mPlaceNameText);
    }
}
