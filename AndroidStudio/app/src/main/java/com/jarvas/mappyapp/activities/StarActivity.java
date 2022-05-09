package com.jarvas.mappyapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.adapter.ResultRecyclerAdapter;
import com.jarvas.mappyapp.adapter.StarAdapter;
import com.jarvas.mappyapp.models.Star;
import com.jarvas.mappyapp.models.database.StarDatabase;

import java.util.ArrayList;
import java.util.List;

public class StarActivity extends AppCompatActivity {

    private List<Star> starList;
    private StarDatabase starDatabase = null;
    private Context mContext = null;
    private StarAdapter starAdapter;
    private RecyclerView mRecyclerView;

    SpeechRecognizer mRecognizer;
    Intent sttIntent;
    Button sttBtn;
    EditText txtSystem;
    EditText txtInMsg;
    Context cThis;
    public static Boolean trigger = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star);

        mRecyclerView = findViewById(R.id.star_result_recyclerView);
        mContext = getApplicationContext();
        starAdapter = new StarAdapter(starList);

        //DB 생성
        starDatabase = StarDatabase.getInstance(this);

        //thread 사용
        class InsertRunnable implements Runnable {
            @Override
            public void run() {
                try {
                    starList = StarDatabase.getInstance(mContext).starDAO().getStars();
                    starAdapter = new StarAdapter(starList);
                    starAdapter.notifyDataSetChanged();

                    mRecyclerView.setAdapter(starAdapter);
                    LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
                    mRecyclerView.setLayoutManager(mLinearLayoutManager);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        InsertRunnable insertRunnable = new InsertRunnable();
        Thread t = new Thread(insertRunnable);
        t.start();

        txtInMsg = (EditText) findViewById(R.id.txtInMsg);
        txtSystem = (EditText) findViewById(R.id.txtSystem);
        sttBtn = (Button)findViewById(R.id.sttStart);
        cThis = this;

        setStt();
        startWithTD();

    }    public void startWithTD(){
        //어플이 실행되면 자동으로 1초뒤에 음성 인식 시작
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("자동 음성 인식 시작");
                txtSystem.setText("어플 실행됨--자동 실행-----------"+"\r\n"+txtSystem.getText());
                sttBtn.performClick();
            }
        },1000);
    }


    public void setStt() {
        //음성인식
        System.out.println("startRecognizer");
        Log.i("Re","start함수");
        sttIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        sttIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getApplicationContext().getPackageName());
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");//한국어 사용
        mRecognizer= SpeechRecognizer.createSpeechRecognizer(cThis);
        mRecognizer.setRecognitionListener(listener);
        System.out.println("startRecognizer");
    }


    public RecognitionListener listener=new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            System.out.println("onREADY");
            txtSystem.setText("onReadyForSpeech..........."+"\r\n"+txtSystem.getText());
        }

        @Override
        public void onBeginningOfSpeech() {
            System.out.println("onBEGINNING");
            txtSystem.setText("지금부터 말을 해주세요..........."+"\r\n"+txtSystem.getText());
        }

        @Override
        public void onRmsChanged(float v) {
            System.out.println("onRms");
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
            txtSystem.setText("onBufferReceived..........."+"\r\n"+txtSystem.getText());
        }

        @Override
        public void onEndOfSpeech() {
            txtSystem.setText("onEndOfSpeech..........."+"\r\n"+txtSystem.getText());
            System.out.println("onEndOfSpeech");
        }

        @Override
        public void onError(int i) {
            txtSystem.setText("천천히 다시 말해 주세요..........."+"\r\n"+txtSystem.getText());
            System.out.println("onError"+i);
            mRecognizer.startListening(sttIntent);
        }

        @Override
        public void onResults(Bundle results) {
            String key= "";
            trigger = false;
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult =results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            if (checkTriggerWord(mResult)) trigger = true;
            if (trigger == true) {
//                mRecognizer.destroy();

                Intent intent_show = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent_show);
            }
            mResult.toArray(rs);
            System.out.println("trigger "+trigger);
            //System.out.println(rs[0]+"\r\n"+txtInMsg.getText()+trigger+"stt result");
            txtInMsg.setText(rs[0]+"\r\n"+txtInMsg.getText()+trigger);
//            mRecognizer.startListening(sttIntent);

        }

        public Boolean checkTriggerWord(ArrayList<String> values){
            for (String v : values){
                if (v.equals("뒤로")) return true;
                if (v.equals("메인")) return true;
                if (v.equals("이전")) return true;
                if (v.equals("돌아")) return true;
                if (v.equals("지도")) return true;
            }
            return false;
        }

        @Override
        public void onPartialResults(Bundle bundle) {
            txtSystem.setText("onPartialResults..........."+"\r\n"+txtSystem.getText());
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
            txtSystem.setText("onEvent..........."+"\r\n"+txtSystem.getText());
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent_show = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent_show);
    }
}
