package com.example.secondsunshine.Sync;

import android.content.Context;

import com.example.secondsunshine.Data.AppDataBase;
import com.example.secondsunshine.Data.WeatherEntry;
import com.example.secondsunshine.Utility.NetworkUtil;


import java.io.IOException;
import java.net.URL;

public class WeatherSyncTask {

    public static void syncWeather(Context context ) {
        URL weatehrUrl = NetworkUtil.buildURL("Seoul", "metric", NetworkUtil.DAY_NUMBER);

        try {
            String jsonData = NetworkUtil.getDataFromURL(weatehrUrl);
            WeatherEntry[]  weatherData= new WeatherEntry[NetworkUtil.DAY_NUMBER];
            weatherData = NetworkUtil.getWeateherDataFromJson(jsonData, NetworkUtil.DAY_NUMBER);

            AppDataBase dataBase = AppDataBase.getInstance(context);
            dataBase.weatherDao().deleteAllWeather();


            for(int i  = 0 ; i < NetworkUtil.DAY_NUMBER ; i++)
            {
                dataBase.weatherDao().insertWeather(weatherData[i]);
            }


            NotificationUtils.NotificationWeather(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
