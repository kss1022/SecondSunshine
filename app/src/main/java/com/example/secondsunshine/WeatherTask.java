package com.example.secondsunshine;

import android.os.AsyncTask;
import android.util.Log;

import com.example.secondsunshine.Utility.NetworkUtil;
import com.example.secondsunshine.Utility.TimeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class WeatherTask extends AsyncTask<URL, Void, String> {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(URL... urls) {
        URL searchURl = urls[0];

        String returnURL = null;
        try {
            returnURL = NetworkUtil.getDataFromURL(searchURl);
        } catch (IOException e) {
            e.printStackTrace();
        }


        getDataFromJson(returnURL);

        return returnURL;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
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
    public String[] getDataFromJson(String JsonData) {
        String weatherData[] = new String[14];


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


                String dayInformation = dateText + description ;

                Log.d("XXXX", "get data from json "  + dayInformation);
                weatherData[i] = dayInformation;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return weatherData;
    }


}