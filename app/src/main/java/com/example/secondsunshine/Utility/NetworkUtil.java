package com.example.secondsunshine.Utility;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
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

    public final static int DAY_NUMBER = 14;

    public final static String OPENWEATHERMAP_BASE_URL = "https://api.openweathermap.org/data/2.5/forecast";
    final static String PARAM_LOCATION = "q";
    final static String PARAM_MODE = "mode";
    final static String PARAM_UNITS = "units";
    final static String PARAM_DAY = "cnt";
    final static String PARAM_KEY = "appid";


    // String url -> Uri -> URL
    public static URL buildURL(String location, String units, int num_day) {
        Uri builtUri = Uri.parse(OPENWEATHERMAP_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_LOCATION, location)
                .appendQueryParameter(PARAM_MODE, "json")
                .appendQueryParameter(PARAM_UNITS, units)
                .appendQueryParameter(PARAM_DAY, Integer.toString(num_day))
                .appendQueryParameter(PARAM_KEY, "cc5e1980ddf504a11985dc39a47850bf")
                .build();

        URL builtUrl = null;

        try {
            builtUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        Log.d(LOG_TAG, "build Url : " + builtUri);
        return builtUrl;
    }


    //URL -> json 데이터 가져오기
    public static String getDataFromURL(URL url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.connect();

        InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());

        StringBuffer stringBuffer = new StringBuffer();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        for (int ch; (ch = inputStream.read()) != -1; ) {
            stringBuffer.append((char) ch);
        }

        String data = stringBuffer.toString();

        Log.d(LOG_TAG, "getDataFromUL : " + data);

        return data;
    }


    final static String OWM_LIST = "list";
    final static String OWM_DT = "dt";
    final static String OWM_MAIN = "main";
    final static String OWM_TEMP_MAX = "temp_max";
    final static String OWM_TEMP_MIN = "temp_min";
    final static String OWM_HUMIDIRT = "humidity";
    final static String OWM_PRESSURE = "pressure";
    final static String OWM_WEATHER = "weather";
    final static String OWM_ID = "id";
    final static String OWM_WEATHER_MAIN = "main";
    final static String OWM_DESCRIPTION = "description";
    final static String OWM_DT_TXT = "dt_txt";

    //json 데이터에서 원하는 정보를 뺴온다.
    public static String[] getDataFromJson(String JsonData, int dataNum) {
        String weatherData[] = new String[dataNum];


        String utcTime;

        double high;
        double low;
        int humidity;
        double pressure;

        int weatherId;
        String mainWather;
        String description;

        String dateText;

        try {
            JSONObject jsonObject = new JSONObject(JsonData);


            JSONArray jsonArray = jsonObject.getJSONArray(OWM_LIST);

            int jsonLength = jsonArray.length();

            JSONObject dayForcast = null;


            for (int i = 0; i < jsonLength; i++) {
                dayForcast = jsonArray.getJSONObject(i);

                utcTime = TimeUtil.convertUtcToLocal(dayForcast.getString(OWM_DT));

                JSONObject mainObject = dayForcast.getJSONObject(OWM_MAIN);

                high = mainObject.getDouble(OWM_TEMP_MAX);
                low = mainObject.getDouble(OWM_TEMP_MIN);
                humidity = mainObject.getInt(OWM_HUMIDIRT);
                pressure = mainObject.getDouble(OWM_PRESSURE);


                JSONObject weatherArray = dayForcast.getJSONArray(OWM_WEATHER).getJSONObject(0);

                weatherId = weatherArray.getInt(OWM_ID);
                mainWather = weatherArray.getString(OWM_WEATHER_MAIN);
                description = weatherArray.getString(OWM_DESCRIPTION);

                dateText = dayForcast.getString(OWM_DT_TXT);


                String dayInformation = dateText + " - " + description;

                Log.d(LOG_TAG, "get data from json " + dayInformation);
                weatherData[i] = dayInformation;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return weatherData;
    }
}
