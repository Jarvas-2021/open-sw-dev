package com.jarvas.mappyapp.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Star")
public class Star {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "content")
    public String content;
}
