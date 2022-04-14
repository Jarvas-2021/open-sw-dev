package com.jarvas.mappyapp.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Star")
public class Star {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "time")
    public String time;

    @ColumnInfo(name = "fee")
    public String fee;

    @ColumnInfo(name = "walktime")
    public String walktime;

    @ColumnInfo(name = "transfer")
    public String transfer;

    @ColumnInfo(name = "distance")
    public String distance;

    @ColumnInfo(name = "transport")
    public String transport;

    @ColumnInfo(name = "transporttime")
    public String transporttime;

    @ColumnInfo(name = "path")
    public String path;
}
