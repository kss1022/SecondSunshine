package com.example.secondsunshine;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.secondsunshine.Data.AppDataBase;
import com.example.secondsunshine.Data.WeatherEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private  static final String LOG_TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<WeatherEntry>> weathers;

    public MainViewModel(@NonNull Application application) {
        super(application);

        AppDataBase dataBase = AppDataBase.getInstance(this.getApplication());
        Log.d(LOG_TAG, " Actively retrieving weathers from DataBase");
        weathers = dataBase.weatherDao().loadAllWeather();
    }

    public LiveData<List<WeatherEntry>> getWeathers() {
        return weathers;
    }
}
