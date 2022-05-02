package com.jarvas.mappyapp.models.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.jarvas.mappyapp.models.Star;

import java.util.List;


@Dao
public interface StarDAO {
    @Query("SELECT * FROM Star")
    List<Star> getStars();

    @Insert
    void insertStar(Star star);

    @Delete
    void deleteStar(Star star);
}
