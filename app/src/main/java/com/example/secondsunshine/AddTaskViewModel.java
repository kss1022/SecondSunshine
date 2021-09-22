package com.example.secondsunshine;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.secondsunshine.Data.AppDataBase;
import com.example.secondsunshine.Data.WeatherEntry;

public class AddTaskViewModel extends ViewModel {

    private LiveData<WeatherEntry> weather;

    public AddTaskViewModel (AppDataBase dataBase,int taskId)
    {
        weather = dataBase.weatherDao().loadWeatherById(taskId);
    }


    public LiveData<WeatherEntry> getWeather() {
        return weather;
    }
}
