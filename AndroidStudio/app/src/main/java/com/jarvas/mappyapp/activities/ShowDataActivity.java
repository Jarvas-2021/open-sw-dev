package com.jarvas.mappyapp.activities;

import static android.net.wifi.p2p.WifiP2pManager.ERROR;
import static com.jarvas.mappyapp.Network.Client.client_msg;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
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
import com.jarvas.mappyapp.utils.BusProvider;
import com.jarvas.mappyapp.utils.Code;
import com.jarvas.mappyapp.utils.ContextStorage;
import com.jarvas.mappyapp.utils.StringResource;
import com.jarvas.mappyapp.utils.Util;
import com.naver.speech.clientapi.SpeechConfig;
import com.naver.speech.clientapi.SpeechRecognitionResult;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShowDataActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {
    private TextToSpeech tts;

    // Naver CSR Variable
    private static final String NAVER_TAG = ShowDataActivity.class.getSimpleName();
    private static final String CLIENT_ID = StringResource.getStringResource(ContextStorage.getCtx(),R.string.csr_key);
    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private AudioWriterPCM writer;
    private boolean isEpdTypeSelected;
    private SpeechConfig.EndPointDetectType currentEpdType;
    private TextDataAdapter mTextDataAdapter;
    Toast myToast;
    boolean check_end = false;
    public static boolean end_point_showdata;

    public ContextStorage contextStorage = new ContextStorage();

    String ai_msg = new String();
    String currentLocation="";
    Scenario scenario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);
        tts = new TextToSpeech(this, this);

//        // TTS??? ???????????? OnInitListener??? ????????? ??????.
//        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if (status != ERROR) {
//                    // ????????? ????????????.
//                    tts.setLanguage(Locale.KOREAN);
//                }
//            }
//        });
        //Intent resultIntent = getIntent();
        //currentLocation = resultIntent.getStringExtra("currentLocation");
        scenario = new Scenario();

        end_point_showdata = false;

//        initData();
        contextStorage.initialize_textdata();
        contextStorage.setmTextDataItems("???????????????. ????????? ???????????????????", 0);
        System.out.println("???????????????????????????????????????");
        //speakOut();
        System.out.println("???????????????????????????????????????????????????????????????");

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mTextDataAdapter = new TextDataAdapter(contextStorage.getmTextDataItems());
        /* initiate adapter */

        /* initiate recyclerview */
        mRecyclerView.setAdapter(mTextDataAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        System.out.println("mddmddddd??????????????????????????????????????????");

        ((ContextStorage) ContextStorage.getCtx().getApplicationContext()).setEnd_point_show_data(false);



        handler = new RecognitionHandler(ShowDataActivity.this);
        naverRecognizer = new NaverRecognizer(ShowDataActivity.this, handler, CLIENT_ID);

        rec_thread_showdata rec_thread_showdata = new rec_thread_showdata(naverRecognizer, NAVER_TAG, isEpdTypeSelected, getApplicationContext());
        rec_thread_showdata.start();

        mTextDataAdapter.setFriendList(contextStorage.getmTextDataItems());



    }

//    private void speak(String text) {
//        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if (status != ERROR){
//                    int result = tts.setLanguage(Locale.KOREA); // ?????? ??????
//                    if(result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA){
//                        Log.e("TTS", "This Language is not supported");
//                    }else{
//                        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
//                    }
//                }else{
//                    Log.e("TTS", "Initialization Failed!");
//                }
//            }
//        });
//    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void speakOut(){
        contextStorage.setCheckTTS(false);
        CharSequence text = "???????????????. ????????? ???????????????????";
        tts.setPitch((float)1); // ?????? ??? ?????? ??????
        tts.setSpeechRate((float)1); // ?????? ?????? ??????

        // ??? ?????? ????????????: ?????? ????????? ??? ?????????
        // ??? ?????? ????????????: 1. TextToSpeech.QUEUE_FLUSH - ???????????? ?????? ????????? ?????? ?????? TTS??? ?????? ??????
        //                 2. TextToSpeech.QUEUE_ADD - ???????????? ?????? ????????? ?????? ?????? ?????? TTS??? ?????? ??????
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id1");
        System.out.println("???????????????2");


        //tts ??????x
        while (tts.isSpeaking()) {
            System.out.println("?????? ???????????????");
        }
        System.out.println("??? ??????");
        contextStorage.setCheckTTS(true);
        System.out.println("??? ??????"+contextStorage.getCheckTTS());
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void speakOut2(String data){
        contextStorage.setCheckTTS(false);
        CharSequence text = data;
        tts.setPitch((float)1); // ?????? ??? ?????? ??????
        tts.setSpeechRate((float)1); // ?????? ?????? ??????

        // ??? ?????? ????????????: ?????? ????????? ??? ?????????
        // ??? ?????? ????????????: 1. TextToSpeech.QUEUE_FLUSH - ???????????? ?????? ????????? ?????? ?????? TTS??? ?????? ??????
        //                 2. TextToSpeech.QUEUE_ADD - ???????????? ?????? ????????? ?????? ?????? ?????? TTS??? ?????? ??????
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id2");
        //tts ??????x

//        while (tts.isSpeaking()) {
//            System.out.println("?????? ???????????????");
//        }
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("?????????????????????????????????speakout");
            }
        },7000);
        System.out.println("??? ??????2");
        contextStorage.setCheckTTS(true);
        System.out.println("??? ??????2"+contextStorage.getCheckTTS());
    }




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onInit(int status) { // OnInitListener??? ????????? TTS ?????????
        if(status == TextToSpeech.SUCCESS){
            int result = tts.setLanguage(Locale.KOREA); // TTS?????? ???????????? ??????

            if(result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA){
                Log.e("TTS", "This Language is not supported");
            }else{
                System.out.println("???????????????");
                speakOut2("???????????????. ????????? ???????????????????");
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("?????????????????????????????????speakout");
                    }
                },7000);
            }
        }else{
            Log.e("TTS", "Initialization Failed!");
        }
    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {

    }
//
//    private void initData() {
//        mTextDataItems = new ArrayList<>();
//        /* adapt data */
//        mTextDataItems.add(new TextDataItem("???????????????. ????????? ???????????????????", Code.ViewType.LEFT_CONTENT));
//        mTextDataItems.add(new TextDataItem("???????????????????????? ????????? ???????", Code.ViewType.RIGHT_CONTENT));
//        mTextDataItems.add(new TextDataItem("????????? ??????????????????. ?????? ????????????.", Code.ViewType.RIGHT_CONTENT));
//        mTextDataItems.add(new TextDataItem("??? 30??? ?????? ????????? ???????????????.", Code.ViewType.RIGHT_CONTENT));
//        mTextDataItems.add(new TextDataItem("?????????.", Code.ViewType.LEFT_CONTENT));
//        System.out.println("items:"+mTextDataItems.get(0).getViewType());
//        System.out.println(mTextDataItems.get(1).getViewType());
//        mTextDataAdapter.setFriendList(mTextDataItems);
//
//    }


    static class RecognitionHandler extends Handler {
        private WeakReference<ShowDataActivity> mActivity;

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
        // TTS ????????? ??????????????? ????????? ???????????? ??????????????? ????????????.
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        System.out.println("?????? : backpressed");
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
                Toast.makeText(this, "?????? ????????????", Toast.LENGTH_SHORT).show();
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
                contextStorage.setmTextDataItems(strBuf.toString(), 1);
                Log.d("Take MSG", client_msg);
                ai_msg = this.scenario.check_auto(client_msg);
//                if (check_all(ai_msg)) {
//                    //ai_msg = ai_msg + " ????????? ??????????????????????";
//                    check_end = true;
//                }
//                if (check_end) {
//                    if (client_msg.equals("???") | client_msg.equals("???")) {
//                        ((ContextStorage) ContextStorage.getCtx().getApplicationContext()).setEnd_point_show_data(true);
//                    }
//                    else {
//                        check_end = false;
//                    }
//                }
//                if (this.scenario.check_scene() == -1) {
//                    ((ContextStorage) ContextStorage.getCtx().getApplicationContext()).setEnd_point_show_data(true);
//                }
//                if (((ContextStorage) ContextStorage.getCtx().getApplicationContext()).isEnd_point_show_data()) {
//                    Intent intent = new Intent(getApplicationContext(), InputActivity.class);
//                    intent.putExtra("start_time_scene", this.scenario.start_time_scene);
//                    intent.putExtra("arrive_time_scene", this.scenario.arrive_time_scene);
//                    intent.putExtra("start_place_scene", this.scenario.start_place_scene);
//                    intent.putExtra("arrive_place_scene", this.scenario.arrive_place_scene);
//                    getApplicationContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                    finish();
//                }
                System.out.println();
                System.out.println("????????????????????????"+ai_msg);
                System.out.println("???????????????????????????"+contextStorage.getmTextDataItems());
                if(!Util.isStringEmpty(ai_msg)) {
                    contextStorage.setmTextDataItems(ai_msg, 0);
                }

                mTextDataAdapter.setFriendList(contextStorage.getmTextDataItems());
                System.out.println("?????????????????????????????????"+contextStorage.getmTextDataItems().get(contextStorage.getmTextDataItems().size()-1).getTextData());
                System.out.println("????????????"+mTextDataAdapter.getTextItem());
                System.out.println("????????????22"+ai_msg);
                speakOut2(ai_msg);
//                Handler mHandler = new Handler();
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                },4000);

                System.out.println("??????");
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
                    Toast.makeText(this, "?????? ????????????.", Toast.LENGTH_SHORT).show();
                } else if (currentEpdType == SpeechConfig.EndPointDetectType.MANUAL) {
                    Toast.makeText(this, "MANUAL epd type is selected.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public boolean check_all(String msg) {
        if (msg.contains("??????????????? ?????????????????????.")) {
            return true;
        }
        if (msg.contains("??????????????? ?????????????????????.")) {
            return true;
        }
        if (msg.contains("???????????? ?????????????????????.")) {
            return true;
        }
        if (msg.contains("???????????? ?????????????????????.")) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TTS ????????? ??????????????? ????????? ???????????? ??????????????? ????????????.
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        System.out.println("?????? : destroy");
        ((ContextStorage) ContextStorage.getCtx().getApplicationContext()).setEnd_point_show_data(true);
    }


}