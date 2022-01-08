package com.crawl.demo.domain;

public class Route {
    private String time;
    private String walkTime;
    private String path;

    public Route() {}

    public Route(String time, String walkTime, String path) {
        this.time = time;
        this.walkTime = walkTime;
        this.path = path;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWalkTime() {
        return walkTime;
    }

    public void setWalkTime(String walkTime) {
        this.walkTime = walkTime;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
