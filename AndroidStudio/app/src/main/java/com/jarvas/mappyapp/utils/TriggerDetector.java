package com.jarvas.mappyapp.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.tts.TextToSpeech;
import android.speech.SpeechRecognizer;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class TriggerDetector {
    Context cMain;
    public final SpeechRecognizer mRecognizer = SpeechRecognizer.createSpeechRecognizer(cMain);

    Intent sttIntent;
    TextToSpeech tts;
    Button sttBtn;
    EditText txtSystem;
    EditText txtInMsg;
    final int PERMISSION = 1;
    private Boolean trigger = false;

    public TriggerDetector(){

    }
    public TriggerDetector(Context contextMain){
        cMain = contextMain;
    }

    public RecognitionListener listener = new RecognitionListener() {
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
            mResult.toArray(rs);
            System.out.println("trigger "+trigger);
            //System.out.println(rs[0]+"\r\n"+txtInMsg.getText()+trigger+"stt result");
            txtInMsg.setText(rs[0]+"\r\n"+txtInMsg.getText()+trigger);
            mRecognizer.startListening(sttIntent);
        }

        @Override
        public void onPartialResults(Bundle bundle) {
            txtSystem.setText("onPartialResults..........."+"\r\n"+txtSystem.getText());
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
            txtSystem.setText("onEvent..........."+"\r\n"+txtSystem.getText());
        }

        public Boolean checkTriggerWord(ArrayList<String> values){
            for (String v : values){
                if (v.equals("매피")) return true;
                if (v.equals("맵피")) return true;
                if (v.equals("해피")) return true;
                if (v.equals("웨피")) return true;
                if (v.equals("웹피")) return true;

                if (v.equals("매피야")) return true;
                if (v.equals("맵피야")) return true;
                if (v.equals("해피야")) return true;
                if (v.equals("웨피야")) return true;
                if (v.equals("웹피야")) return true;

                if (v.equals("웨피아")) return true;
                if (v.equals("웨피아")) return true;
                if (v.equals("웨피아")) return true;
                if (v.equals("웨피아")) return true;
                if (v.equals("웹피아")) return true;

                if (v.equals("매피 야")) return true;
                if (v.equals("맵피 야")) return true;
                if (v.equals("해피 야")) return true;
                if (v.equals("웨피 야")) return true;
                if (v.equals("웹피 야")) return true;

                if (v.equals("웨피 아")) return true;
                if (v.equals("웨피 아")) return true;
                if (v.equals("웨피 아")) return true;
                if (v.equals("웨피 아")) return true;
                if (v.equals("웨피 아")) return true;
            }
            return false;
        }
    };
}
