package com.jarvas.mappyapp.models.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.jarvas.mappyapp.models.Star;

@Database(entities = {Star.class}, version = 1)
public abstract class StarDatabase extends RoomDatabase {
    private static StarDatabase INSTANCE = null;
    public abstract StarDAO starDAO();

    public static StarDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), StarDatabase.class, "star.db").build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
