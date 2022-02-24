package com.jarvas.mappyapp.activities;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.adapter.TextDataAdapter;
import com.jarvas.mappyapp.api.NaverRecognizer;
import com.jarvas.mappyapp.model.category_search.TextDataItem;
import com.jarvas.mappyapp.utils.AudioWriterPCM;
import com.naver.speech.clientapi.SpeechConfig;
import com.naver.speech.clientapi.SpeechRecognitionResult;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ShowDataActivity extends AppCompatActivity implements View.OnClickListener {

    // Naver CSR Variable
    private static final String NAVER_TAG = ShowDataActivity.class.getSimpleName();
    private static final String CLIENT_ID = "n9a2bacryq";
    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private AudioWriterPCM writer;
    private boolean isEpdTypeSelected;
    private SpeechConfig.EndPointDetectType currentEpdType;
    private FloatingActionButton floatingActionButton;
    private ArrayList<TextDataItem> mTextDataItems = new ArrayList<>();
    private TextDataAdapter mTextDataAdapter = new TextDataAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        /* initiate adapter */

        /* initiate recyclerview */
        mRecyclerView.setAdapter(mTextDataAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(this);
        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);

        /* adapt data */
        mTextDataItems.add(new TextDataItem("예제 데이터"));
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
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.floatingActionButton:
                System.out.println("case 들어옴");
                writer = new AudioWriterPCM(Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                if (!naverRecognizer.getSpeechRecognizer().isRunning()) {
                    System.out.println("음성인식 실행됨");
                    // Run SpeechRecongizer by calling recognize().
                    currentEpdType = SpeechConfig.EndPointDetectType.HYBRID;
                    isEpdTypeSelected = false;
                    naverRecognizer.recognize();
                }
                if (!isEpdTypeSelected) {
                    if (naverRecognizer.getSpeechRecognizer().isRunning()) {
                        naverRecognizer.getSpeechRecognizer().selectEPDTypeInHybrid(SpeechConfig.EndPointDetectType.AUTO);
                    }
                } else {
                    if (!naverRecognizer.getSpeechRecognizer().isRunning()) {
                        Log.e(NAVER_TAG, "Recognition is already finished.");
                    } else {
                        naverRecognizer.getSpeechRecognizer().stop();
                    }
                }
                break;
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
        floatingActionButton.setEnabled(true);
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
                    strBuf.append("\n");
                    System.out.println("RESULT : "+result);
                }
                // todo - 이부분 고치기
                System.out.println("strBuf"+strBuf);
                mTextDataItems.add(new TextDataItem(strBuf.toString()));
                System.out.println(mTextDataItems);
                mTextDataAdapter.setFriendList(mTextDataItems);
                break;

            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }
                floatingActionButton.setEnabled(true);
                break;

            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }
                floatingActionButton.setEnabled(true);
                break;

            case R.id.endPointDetectTypeSelected:
                isEpdTypeSelected = true;
                currentEpdType = (SpeechConfig.EndPointDetectType) msg.obj;
                if (currentEpdType == SpeechConfig.EndPointDetectType.AUTO) {
                    Toast.makeText(this, "AUTO epd type is selected.", Toast.LENGTH_SHORT).show();
                } else if (currentEpdType == SpeechConfig.EndPointDetectType.MANUAL) {
                    Toast.makeText(this, "MANUAL epd type is selected.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
