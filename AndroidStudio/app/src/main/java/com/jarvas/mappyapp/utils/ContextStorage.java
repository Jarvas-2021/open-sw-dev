package com.jarvas.mappyapp.utils;

import android.app.Application;
import android.content.Context;

import com.jarvas.mappyapp.models.TextDataItem;

import java.util.ArrayList;

public class ContextStorage extends Application {
    public static Context ctx_storage;
    private ArrayList<TextDataItem> mTextDataItems;
    public static boolean checkTTS;


    @Override
    public void onCreate() {
        ctx_storage = this;
        super.onCreate();
    }

    public static Context getCtx(){
        return ctx_storage;
    }

    boolean end_point_show_data;

    public boolean isEnd_point_show_data() {
        return end_point_show_data;
    }

    public void setEnd_point_show_data(boolean end_point_show_data) {
        this.end_point_show_data = end_point_show_data;
    }

    public ArrayList<TextDataItem> getmTextDataItems() {
        return this.mTextDataItems;
    }

    public void initialize_textdata() {
        mTextDataItems = new ArrayList<>();
    }

    public void setmTextDataItems(String str, int i) {
        if (i == 1) {
            this.mTextDataItems.add(new TextDataItem(str, Code.ViewType.RIGHT_CONTENT));
        }
        else {
            this.mTextDataItems.add(new TextDataItem(str, Code.ViewType.LEFT_CONTENT));
        }
    }

    public void setCheckTTS(boolean checkTTS) {
        this.checkTTS = checkTTS;
    }

    public boolean getCheckTTS() {
        return checkTTS;
    }
}
