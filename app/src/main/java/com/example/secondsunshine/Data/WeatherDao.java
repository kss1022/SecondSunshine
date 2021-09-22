package com.example.secondsunshine.Data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WeatherDao {

    @Query("SELECT * FROM weather ORDER BY time")
    LiveData<List<WeatherEntry>> loadAllWeather();

    @Insert
    void insertWeather(WeatherEntry weatherEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateWeather(WeatherEntry weatherEntry);

    @Delete
    void deleteWeather(WeatherEntry weatherEntry);


    @Query("DELETE FROM weather")
    void deleteAllWeather();


    @Query("SELECT * FROM weather WHERE id = :id")
    LiveData<WeatherEntry> loadWeatherById(int id);
}
