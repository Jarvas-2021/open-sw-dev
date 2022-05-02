package com.jarvas.mappyapp.models;

public class ResultItem {
    String time;
    String path;
    String price;

    String walkTime;
    String transfer;
    String distance;

    String transType;
    String interTime;

    String st;
    String dt;

    public ResultItem(String time, String path, String price, String walktime, String transfer, String distance, String transType, String interTime, String st, String dt) {
        this.time = time;
        this.path = path;
        this.price = price;
        this.walkTime = walktime;
        this.transfer = transfer;
        this.distance = distance;
        this.transType = transType;
        this.interTime = interTime;
        this.st = st;
        this.dt = dt;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getWalkTime() {
        return walkTime;
    }

    public void setWalkTime(String walkTime) {
        this.walkTime = walkTime;
    }

    public String getTransfer() {
        return transfer;
    }

    public void setTransfer(String transfer) {
        this.transfer = transfer;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getInterTime() {
        return interTime;
    }

    public void setInterTime(String interTime) {
        this.interTime = interTime;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }


}
