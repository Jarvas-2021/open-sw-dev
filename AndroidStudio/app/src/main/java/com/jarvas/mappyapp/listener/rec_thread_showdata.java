package com.jarvas.mappyapp.listener;

import static com.jarvas.mappyapp.activities.ShowDataActivity.end_point_showdata;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.jarvas.mappyapp.utils.AudioWriterPCM;
import com.jarvas.mappyapp.utils.ContextStorage;
import com.naver.speech.clientapi.SpeechConfig;

public class rec_thread_showdata extends Thread {

    AudioWriterPCM writer;
    NaverRecognizer naverRecognizer;
    String NAVER_TAG;
    boolean isEpdTypeSelected;
    SpeechConfig.EndPointDetectType currentEpdType;
    Context context;
    ContextStorage contextStorage = new ContextStorage();

    public rec_thread_showdata(NaverRecognizer naverRecognizer, String NAVER_TAG, boolean isEpdTypeSelected, Context context) {
        this.naverRecognizer = naverRecognizer;
        this.NAVER_TAG = NAVER_TAG;
        this.isEpdTypeSelected = isEpdTypeSelected;
        this.context = context;

    }

    @Override
    public void run() {
        while (!((ContextStorage) ContextStorage.getCtx().getApplicationContext()).isEnd_point_show_data()) {
            while (!contextStorage.getCheckTTS()) {
                System.out.println("true될때까지 기다림");
            };
            System.out.println("showdata"+end_point_showdata);
            System.out.println("case 들어옴");
            try {
                sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            this.writer = new AudioWriterPCM(Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
            if (!this.naverRecognizer.getSpeechRecognizer().isRunning()) {
                System.out.println("tts 사용x일때만 시작하기");
                System.out.println("음성인식 실행됨");
                // Run SpeechRecongizer by calling recognize().
                currentEpdType = SpeechConfig.EndPointDetectType.HYBRID;
                isEpdTypeSelected = false;
                this.naverRecognizer.recognize();
            }
            if (!isEpdTypeSelected) {
                if (this.naverRecognizer.getSpeechRecognizer().isRunning()) {
                    this.naverRecognizer.getSpeechRecognizer().selectEPDTypeInHybrid(SpeechConfig.EndPointDetectType.AUTO);
                }
            } else {
                if (!this.naverRecognizer.getSpeechRecognizer().isRunning()) {
                    Log.e(this.NAVER_TAG, "Recognition is already finished.");
                } else {
                    this.naverRecognizer.getSpeechRecognizer().stop();
                }
            }

        }
    }
}