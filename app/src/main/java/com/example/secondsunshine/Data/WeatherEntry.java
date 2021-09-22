package com.example.secondsunshine.Data;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "weather")
public class WeatherEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private long time;
    private String description;
    private int weather_id;
    private double max_temp;
    private double min_temp;
    private int humidity;
    private double pressure;
    //    TypeConver Test
    private Date date;

    @Ignore
    public WeatherEntry(long time, String description, int weather_id, double max_temp, double min_temp, int humidity, double pressure, Date date) {
        this.time = time;
        this.description = description;
        this.weather_id = weather_id;
        this.max_temp = max_temp;
        this.min_temp = min_temp;
        this.humidity = humidity;
        this.pressure = pressure;
        this.date = date;
    }


    public WeatherEntry(int id, long time, String description, int weather_id, double max_temp, double min_temp, int humidity, double pressure, Date date) {
        this.id = id;
        this.time = time;
        this.description = description;
        this.weather_id = weather_id;
        this.max_temp = max_temp;
        this.min_temp = min_temp;
        this.humidity = humidity;
        this.pressure = pressure;
        this.date = date;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWeather_id() {
        return weather_id;
    }

    public void setWeather_id(int weather_id) {
        this.weather_id = weather_id;
    }

    public double getMax_temp() {
        return max_temp;
    }

    public void setMax_temp(double max_temp) {
        this.max_temp = max_temp;
    }

    public double getMin_temp() {
        return min_temp;
    }

    public void setMin_temp(double min_temp) {
        this.min_temp = min_temp;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}





