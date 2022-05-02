package com.jarvas.mappyapp.models.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.jarvas.mappyapp.models.Star;

@Database(entities = {Star.class}, version = 1, exportSchema = false)

public abstract class StarDatabase extends RoomDatabase {
    private static StarDatabase database;

    private static String DATABASE_NAME = "database";

    public synchronized static StarDatabase getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(), StarDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }

    public abstract StarDAO starDAO();
}

