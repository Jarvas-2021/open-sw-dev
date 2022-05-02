package com.jarvas.mappyapp.models;

public class TextDataItem {
    private String textData;
    private int viewType;

    public TextDataItem(String textData, int viewType) {
        this.textData = textData;
        this.viewType = viewType;
    }

    public String getTextData() {
        return textData;
    }

    public Integer getViewType() {
        return viewType;
    }

    public void setTextData(String textData) {
        this.textData = textData;
    }
}
