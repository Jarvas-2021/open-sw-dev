package com.jarvas.mappyapp.activities;

import static com.jarvas.mappyapp.Network.Client.client_msg;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.Scenario;
import com.jarvas.mappyapp.adapter.TextDataAdapter;
import com.jarvas.mappyapp.listener.NaverRecognizer;
import com.jarvas.mappyapp.listener.rec_thread_showdata;
import com.jarvas.mappyapp.models.TextDataItem;
import com.jarvas.mappyapp.utils.AudioWriterPCM;
import com.jarvas.mappyapp.utils.Code;
import com.jarvas.mappyapp.utils.ContextStorage;
import com.jarvas.mappyapp.utils.StringResource;
import com.naver.speech.clientapi.SpeechConfig;
import com.naver.speech.clientapi.SpeechRecognitionResult;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ShowDataActivity extends AppCompatActivity {

    // Naver CSR Variable
    private static final String NAVER_TAG = ShowDataActivity.class.getSimpleName();
    private static final String CLIENT_ID = StringResource.getStringResource(ContextStorage.getCtx(),R.string.csr_key);
    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private AudioWriterPCM writer;
    private boolean isEpdTypeSelected;
    private SpeechConfig.EndPointDetectType currentEpdType;
    private ArrayList<TextDataItem> mTextDataItems;
    private TextDataAdapter mTextDataAdapter;
    Toast myToast;
    boolean check_end = false;
    public static boolean end_point_showdata;

    Scenario scenario = new Scenario();
    String ai_msg = new String();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);

        end_point_showdata = false;

//        initData();
        mTextDataItems = new ArrayList<>();
        mTextDataItems.add(new TextDataItem("안녕하세요. 무엇을 도와드릴까요?", Code.ViewType.LEFT_CONTENT));

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mTextDataAdapter = new TextDataAdapter(mTextDataItems);
        mRecyclerView.setAdapter(mTextDataAdapter);
        /* initiate adapter */

        /* initiate recyclerview */
        mRecyclerView.setAdapter(mTextDataAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);

        rec_thread_showdata rec_thread_showdata = new rec_thread_showdata(naverRecognizer, NAVER_TAG, isEpdTypeSelected, getApplicationContext());
        rec_thread_showdata.start();

        mTextDataAdapter.setFriendList(mTextDataItems);

    }

    private void initData() {
        mTextDataItems = new ArrayList<>();
        /* adapt data */
        mTextDataItems.add(new TextDataItem("안녕하세요. 무엇을 도와드릴까요?", Code.ViewType.LEFT_CONTENT));
        mTextDataItems.add(new TextDataItem("인천대입구역까지 얼마나 걸려?", Code.ViewType.RIGHT_CONTENT));
        mTextDataItems.add(new TextDataItem("잠시만 기다려주세요. 탐색 중입니다.", Code.ViewType.RIGHT_CONTENT));
        mTextDataItems.add(new TextDataItem("약 30분 걸릴 것으로 예상됩니다.", Code.ViewType.RIGHT_CONTENT));
        mTextDataItems.add(new TextDataItem("알겠어.", Code.ViewType.LEFT_CONTENT));
        System.out.println("items:"+mTextDataItems.get(0).getViewType());
        System.out.println(mTextDataItems.get(1).getViewType());
        mTextDataAdapter.setFriendList(mTextDataItems);

    }

    static class RecognitionHandler extends Handler {
        private final WeakReference<ShowDataActivity> mActivity;

        RecognitionHandler(ShowDataActivity activity) {
            mActivity = new WeakReference<ShowDataActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ShowDataActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        // NOTE : initialize() must be called on start time.
        naverRecognizer.getSpeechRecognizer().initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }


    // Handle speech recognition Messages.
    private void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady:
                // Now an user can speak.
                writer = new AudioWriterPCM(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;

            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;

            case R.id.partialResult:
                // Extract obj property typed with String.
                break;

            case R.id.finalResult:
                // Extract obj property typed with String array.
                // The first element is recognition result for speech.
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();
                StringBuilder strBuf = new StringBuilder();
                for (String result : results) {
                    strBuf.append(result);
                    break;
                }
                System.out.println("showresults:"+results);
                System.out.println("showstrBuf"+strBuf);
                mTextDataItems.add(new TextDataItem(strBuf.toString(),Code.ViewType.RIGHT_CONTENT));
                Log.d("Take MSG", client_msg);
                ai_msg = this.scenario.check_auto(client_msg);
                if (check_all(ai_msg)) {
                    ai_msg = ai_msg + " 검색을 마치시겠습니까?";
                    check_end = true;
                }
                if (check_end) {
                    if (client_msg.equals("네") | client_msg.equals("예")) {
                        ((ContextStorage) ContextStorage.getCtx().getApplicationContext()).setEnd_point_show_data(true);
                    }
                    else {
                        check_end = false;
                    }
                }
                if (this.scenario.check_scene() == -1) {
                    ((ContextStorage) ContextStorage.getCtx().getApplicationContext()).setEnd_point_show_data(true);
                }
                if (((ContextStorage) ContextStorage.getCtx().getApplicationContext()).isEnd_point_show_data()) {
                    Intent intent = new Intent(getApplicationContext(), InputActivity.class);
                    intent.putExtra("start_time_scene", this.scenario.start_time_scene);
                    intent.putExtra("arrive_time_scene", this.scenario.arrive_time_scene);
                    intent.putExtra("start_place_scene", this.scenario.start_place_scene);
                    intent.putExtra("arrive_place_scene", this.scenario.arrive_place_scene);
                    finish();
                }
                System.out.println(mTextDataItems);
                mTextDataItems.add(new TextDataItem(ai_msg, Code.ViewType.LEFT_CONTENT));
                mTextDataAdapter.setFriendList(mTextDataItems);
                System.out.println("실행");
                break;

            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }
                break;

            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }
                break;

            case R.id.endPointDetectTypeSelected:
                isEpdTypeSelected = true;
                currentEpdType = (SpeechConfig.EndPointDetectType) msg.obj;
                if (currentEpdType == SpeechConfig.EndPointDetectType.AUTO) {
                    Toast.makeText(this, "지금 말하세요.", Toast.LENGTH_SHORT).show();
                } else if (currentEpdType == SpeechConfig.EndPointDetectType.MANUAL) {
                    Toast.makeText(this, "MANUAL epd type is selected.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public boolean check_all(String msg) {
        if (msg.contains("출발시간이 입력되었습니다.")) {
            return true;
        }
        if (msg.contains("도착시간이 입력되었습니다.")) {
            return true;
        }
        if (msg.contains("도착지가 입력되었습니다.")) {
            return true;
        }
        if (msg.contains("출발지가 입력되었습니다.")) {
            return true;
        }
        return false;
    }
}