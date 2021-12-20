package com.jarvas.mappyapp.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.adapter.LocationAdapter;
import com.jarvas.mappyapp.api.ApiClient;
import com.jarvas.mappyapp.api.ApiInterface;
import com.jarvas.mappyapp.api.Config;
import com.jarvas.mappyapp.model.category_search.CategoryResult;
import com.jarvas.mappyapp.model.category_search.Document;
import com.jarvas.mappyapp.utils.BusProvider;
import com.jarvas.mappyapp.utils.IntentKey;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;

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
    Bus bus = BusProvider.getInstance();
    private ActivityResultLauncher<Intent> resultLauncher;

    String startAddressText;
    String destinationAddressText;
    String WayPointAddressText;

    String searchAddressText;

    Bus bus2 = BusProvider.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        bus2.register(this);
        initView();
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
        //MainActivity mainActivity = new MainActivity();
        //searchAddressText = mainActivity.mSearchAddress;
        //System.out.println("searchaddress"+searchAddressText);
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

        // editText1(출발지) 검색 텍스처이벤트
        searchEdit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 입력하기 전에
                recyclerView1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() >= 1) {
                    // if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                    documentArrayList.clear();
                    locationAdapter.clear();
                    locationAdapter.notifyDataSetChanged();
                    ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<CategoryResult> call = apiInterface.getSearchLocation(Config.restapi_key, charSequence.toString(), 15);
                    call.enqueue(new Callback<CategoryResult>() {
                        @Override
                        public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                for (Document document : response.body().getDocuments()) {
                                    locationAdapter.addItem(document);
                                }
                                locationAdapter.notifyDataSetChanged();

                            } else {
                                Log.e("test", response.message());
                            }
                        }
                        @Override
                        public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {

                        }
                    });
                    //}
                    //mLastClickTime = SystemClock.elapsedRealtime();
                } else {
                    if (charSequence.length() <= 0) {
                        recyclerView1.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 입력이 끝났을 때
            }
        });

        searchEdit1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                } else {
                    recyclerView1.setVisibility(View.GONE);
                }
            }
        });
        searchEdit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "검색리스트에서 장소를 선택해주세요", Toast.LENGTH_SHORT).show();
            }
        });

        // editText2(도착지) 검색 텍스처이벤트
        searchEdit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 입력하기 전에
                recyclerView2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() >= 1) {
                    // if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                    documentArrayList.clear();
                    locationAdapter2.clear();
                    locationAdapter2.notifyDataSetChanged();
                    ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<CategoryResult> call = apiInterface.getSearchLocation(Config.restapi_key, charSequence.toString(), 15);
                    call.enqueue(new Callback<CategoryResult>() {
                        @Override
                        public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                for (Document document : response.body().getDocuments()) {
                                    locationAdapter2.addItem(document);
                                }
                                locationAdapter2.notifyDataSetChanged();

                            } else {
                                Log.e("test", response.message());
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {

                        }
                    });
                    //}
                    //mLastClickTime = SystemClock.elapsedRealtime();
                } else {
                    if (charSequence.length() <= 0) {
                        recyclerView2.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 입력이 끝났을 때
            }
        });

        searchEdit2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                } else {
                    recyclerView2.setVisibility(View.GONE);
                }
            }
        });
        searchEdit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "검색리스트에서 장소를 선택해주세요", Toast.LENGTH_SHORT).show();
            }
        });

        // editText3(경유지) 검색 텍스처이벤트
        searchEdit3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 입력하기 전에
                recyclerView3.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() >= 1) {
                    // if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                    documentArrayList.clear();
                    locationAdapter3.clear();
                    locationAdapter3.notifyDataSetChanged();
                    ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<CategoryResult> call = apiInterface.getSearchLocation(Config.restapi_key, charSequence.toString(), 15);
                    call.enqueue(new Callback<CategoryResult>() {
                        @Override
                        public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                for (Document document : response.body().getDocuments()) {
                                    locationAdapter3.addItem(document);
                                }
                                locationAdapter3.notifyDataSetChanged();
                            } else {
                                Log.e("test", response.message());
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {

                        }
                    });
                    //}
                    //mLastClickTime = SystemClock.elapsedRealtime();
                } else {
                    if (charSequence.length() <= 0) {
                        recyclerView3.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 입력이 끝났을 때
            }
        });

        searchEdit3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                } else {
                    recyclerView3.setVisibility(View.GONE);
                }
            }
        });
        searchEdit3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "검색리스트에서 장소를 선택해주세요", Toast.LENGTH_SHORT).show();
            }
        });

    }
    //검색예시 클릭시 이벤트 오토버스
    @Subscribe
    public void search(Document document) {
        //public항상 붙여줘야함
        Toast.makeText(getApplicationContext(), document.getPlaceName() + " 검색", Toast.LENGTH_SHORT).show();
        System.out.println("input search 이벤트 오토버스 실행");
        //mSearchAddress = document.getAddressName();
        searchAddressText = document.getAddressName();
        System.out.println("searchAddressText:"+searchAddressText);
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
                searchEdit1.setText(document.getPlaceName());
                startAddressText = document.getAddressName();
                System.out.println("process Intent"+startAddressText);

            }
        }
    }

    private void processIntentDestination(Intent intent) {
        if (intent != null) {
            Document document = intent.getParcelableExtra(IntentKey.PLACE_SEARCH_SET_DESTINATION);
            if (document != null) {
                searchEdit2.setText(document.getPlaceName());
                destinationAddressText = document.getAddressName();
            }
        }
    }
    private void processIntentWayPoint(Intent intent) {
        if (intent != null) {
            Document document = intent.getParcelableExtra(IntentKey.PLACE_SEARCH_SET_WAYPOINT);
            if (document != null) {
                searchEdit3.setText(document.getPlaceName());
                WayPointAddressText = document.getAddressName();
            }
        }
    }
}
