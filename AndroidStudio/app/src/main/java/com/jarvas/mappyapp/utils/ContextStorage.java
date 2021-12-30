package com.jarvas.mappyapp.utils;

import android.app.Application;
import android.content.Context;

public class ContextStorage extends Application {
    public static Context ctx_storage;
    @Override
    public void onCreate() {
        ctx_storage = this;
        super.onCreate();
    }

    public static Context getCtx(){
        return ctx_storage;
    }
}
