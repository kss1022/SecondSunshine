package com.example.secondsunshine.Sync;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.secondsunshine.Data.AppDataBase;
import com.example.secondsunshine.Data.PreferenceUtils;
import com.example.secondsunshine.Data.WeatherEntry;
import com.example.secondsunshine.R;
import com.example.secondsunshine.Utility.NetworkUtil;
import com.example.secondsunshine.Utility.NotificationUtils;


import java.io.IOException;
import java.net.URL;

public class WeatherSyncTask {

    public static void syncWeather(Context context) {

        String location = PreferenceUtils.getLocation(context);
        String temperture = PreferenceUtils.getTempertureFormat(context);
        int day  = PreferenceUtils.getDay(context);

        URL weatehrUrl = NetworkUtil.buildURL(location, temperture, day);

        try {
            String jsonData = NetworkUtil.getDataFromURL(weatehrUrl);
            WeatherEntry[] weatherData = new WeatherEntry[day];
            weatherData = NetworkUtil.getWeateherDataFromJson(jsonData, day);

            AppDataBase dataBase = AppDataBase.getInstance(context);
            dataBase.weatherDao().deleteAllWeather();

            for (int i = 0; i < day; i++) {
                dataBase.weatherDao().insertWeather(weatherData[i]);
            }


            //Notification 설정에 따라 알람을 한다.
            if (PreferenceUtils.getNotification(context)) {
                NotificationUtils.setTodayWeatherData(weatherData[0]);
                NotificationUtils.NotificationWeather(context);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
