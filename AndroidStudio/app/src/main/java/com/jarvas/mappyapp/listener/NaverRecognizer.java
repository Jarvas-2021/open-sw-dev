package com.jarvas.mappyapp.listener;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.WorkerThread;

import com.jarvas.mappyapp.Network.Client;
import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.utils.ContextStorage;
import com.jarvas.mappyapp.utils.Util;
import com.naver.speech.clientapi.SpeechConfig;
import com.naver.speech.clientapi.SpeechConfig.EndPointDetectType;
import com.naver.speech.clientapi.SpeechConfig.LanguageType;
import com.naver.speech.clientapi.SpeechRecognitionException;
import com.naver.speech.clientapi.SpeechRecognitionListener;
import com.naver.speech.clientapi.SpeechRecognitionResult;
import com.naver.speech.clientapi.SpeechRecognizer;

public class NaverRecognizer implements SpeechRecognitionListener {

    ContextStorage contextStorage = new ContextStorage();
    private final static String TAG = NaverRecognizer.class.getSimpleName();

    private Handler mHandler;
    private SpeechRecognizer mRecognizer;

    public NaverRecognizer(Context context, Handler handler, String clientId) {
        this.mHandler = handler;

        try {
            mRecognizer = new SpeechRecognizer(context, clientId);
        } catch (SpeechRecognitionException e) {
            e.printStackTrace();
        }

        mRecognizer.setSpeechRecognitionListener(this);
    }

    public SpeechRecognizer getSpeechRecognizer() {
        return mRecognizer;
    }

    public void recognize() {
        try {
            mRecognizer.recognize(new SpeechConfig(
                    LanguageType.KOREAN,
                    EndPointDetectType.AUTO));
        } catch (SpeechRecognitionException e) {
            e.printStackTrace();
        }
    }

    @Override
    @WorkerThread
    public void onInactive() {
        Log.d(TAG, "Event occurred : Inactive");
        Message msg = Message.obtain(mHandler, R.id.clientInactive);
        msg.sendToTarget();
    }

    @Override
    @WorkerThread
    public void onReady() {
        Log.d(TAG, "Event occurred : Ready");
        Message msg = Message.obtain(mHandler, R.id.clientReady);
        msg.sendToTarget();
    }

    @Override
    @WorkerThread
    public void onRecord(short[] speech) {
        Log.d(TAG, "Event occurred : Record");
        Message msg = Message.obtain(mHandler, R.id.audioRecording, speech);
        msg.sendToTarget();
    }

    @Override
    @WorkerThread
    public void onPartialResult(String result) {
        Log.d(TAG, "Partial Result!! (" + result + ")");
        Message msg = Message.obtain(mHandler, R.id.partialResult, result);
        msg.sendToTarget();
    }

    @Override
    @WorkerThread
    public void onEndPointDetected() {
        Log.d(TAG, "Event occurred : EndPointDetected");
    }

    @Override
    @WorkerThread
    public void onResult(SpeechRecognitionResult result) {
        Log.d(TAG, "Final Result!! (" + result.getResults().get(0) + ")");
        Client sendobj = new Client("Thread", result.getResults().get(0));
        sendobj.client_msg = "";
        sendobj.start();
        try {
            sendobj.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Message msg = Message.obtain(mHandler, R.id.finalResult, result);
        msg.sendToTarget();
    }

    @Override
    @WorkerThread
    public void onError(int errorCode) {
        Log.d(TAG, "Error!! (" + Integer.toString(errorCode) + ")");
        Message msg = Message.obtain(mHandler, R.id.recognitionError, errorCode);
        msg.sendToTarget();
    }

    @Override
    @WorkerThread
    public void onEndPointDetectTypeSelected(EndPointDetectType epdType) {
        Log.d(TAG, "EndPointDetectType is selected!! (" + Integer.toString(epdType.toInteger()) + ")");
        Message msg = Message.obtain(mHandler, R.id.endPointDetectTypeSelected, epdType);
        msg.sendToTarget();
    }
}