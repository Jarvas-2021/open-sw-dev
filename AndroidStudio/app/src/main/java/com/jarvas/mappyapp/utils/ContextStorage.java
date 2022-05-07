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

    boolean end_point_main;
    boolean end_point_input;
    boolean end_point_show_data;

    public boolean isEnd_point_main() {
        return end_point_main;
    }

    public void setEnd_point_main(boolean end_point_main) {
        this.end_point_main = end_point_main;
    }

    public boolean isEnd_point_input() {
        return end_point_input;
    }

    public void setEnd_point_input(boolean end_point_input) {
        this.end_point_input = end_point_input;
    }

    public boolean isEnd_point_show_data() {
        return end_point_show_data;
    }

    public void setEnd_point_show_data(boolean end_point_show_data) {
        this.end_point_show_data = end_point_show_data;
    }
}
