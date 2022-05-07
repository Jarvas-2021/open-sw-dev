package com.jarvas.mappyapp.listener;

import static com.jarvas.mappyapp.activities.MainActivity.end_point_main;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.jarvas.mappyapp.utils.AudioWriterPCM;
import com.naver.speech.clientapi.SpeechConfig;

public class rec_thread_main extends Thread {

    AudioWriterPCM writer;
    NaverRecognizer naverRecognizer;
    String NAVER_TAG;
    boolean isEpdTypeSelected;
    SpeechConfig.EndPointDetectType currentEpdType;
    Context context;

    public rec_thread_main(NaverRecognizer naverRecognizer, String NAVER_TAG, boolean isEpdTypeSelected, Context context) {
        this.naverRecognizer = naverRecognizer;
        this.NAVER_TAG = NAVER_TAG;
        this.isEpdTypeSelected = isEpdTypeSelected;
        this.context = context;

    }

    @Override
    public void run() {

        while (!end_point_main) {
            System.out.println("end_point_main"+end_point_main);
            System.out.println("case 들어옴");
            this.writer = new AudioWriterPCM(Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
            if (!this.naverRecognizer.getSpeechRecognizer().isRunning()) {
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
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}