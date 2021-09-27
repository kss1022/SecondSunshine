package com.example.secondsunshine.Sync;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

public class WeatherIntentService extends IntentService {

    public WeatherIntentService() {
        super("WeatherIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        WeatherSyncTask.syncWeather(this);
    }
}
