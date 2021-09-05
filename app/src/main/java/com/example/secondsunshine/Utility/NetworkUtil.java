package com.example.secondsunshine.Utility;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;

public class NetworkUtil {
    final static String LOG_TAG = NetworkUtil.class.getSimpleName();

//    https://api.openweathermap.org/data/2.5/forecast?q=Seoul&mode=json&units=metric&cnt=14&appid=cc5e1980ddf504a11985dc39a47850bf
    public final static String OPENWEATHERMAP_BASE_URL =  "https://api.openweathermap.org/data/2.5/forecast";
    final static String PARAM_LOCATION = "q";
    final static String PARAM_MODE = "mode";
    final static String PARAM_UNITS = "units";
    final static String PARAM_DAY = "cnt";
    final static String PARAM_KEY = "appid";


    // String url -> Uri -> URL
    public static URL buildURL(String url)
    {
        Uri builtUri = Uri.parse(url).buildUpon()
                .appendQueryParameter(PARAM_LOCATION, "Seoul")
                .appendQueryParameter(PARAM_MODE, "json")
                .appendQueryParameter(PARAM_UNITS, "metric")
                .appendQueryParameter(PARAM_DAY, "14")
                .appendQueryParameter(PARAM_KEY, "cc5e1980ddf504a11985dc39a47850bf")
                .build();

        URL builtUrl = null;

        try {
            builtUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        Log.d(LOG_TAG, "build Url : " + builtUri );
        return builtUrl;
    }


    //URL -> json 데이터 가져오기
    public static String getDataFromURL(URL url) throws  IOException
    {
        HttpURLConnection  httpURLConnection = (HttpURLConnection)url.openConnection();
        httpURLConnection.connect();

        InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());

        StringBuffer stringBuffer = new StringBuffer();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        for(int ch; (ch = inputStream.read()) != -1;)
        {
            stringBuffer.append((char)ch);
        }

        String  data = stringBuffer.toString();

        Log.d(LOG_TAG ,"getDataFromUL : " + data);

        return data;
    }





}
