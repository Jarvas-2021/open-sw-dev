package com.jarvas.mappyapp.thread;

import com.jarvas.mappyapp.activities.MainActivity;

public class RecognizerThread extends Thread{
    MainActivity m = new MainActivity();
    public RecognizerThread() {

    }
    public void run() {
        //m.startRecognizer();
    }
}
