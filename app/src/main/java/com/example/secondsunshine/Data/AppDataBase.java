package com.example.secondsunshine.Data;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities =  {WeatherEntry.class}, version = 1, exportSchema = false)
@TypeConverters(DataConverter.class)
public abstract class AppDataBase extends RoomDatabase {

    private  static final String LOG_TAG  = AppDataBase.class.getSimpleName();
    private  static final String DATABASE_NAME = "weather_forcast";

    private  static final Object LOCK = new Object();
    private  static AppDataBase sInstance;


    public static AppDataBase getInstance(Context context)
    {
        if(sInstance == null)
        {
            synchronized (LOCK)
            {
                Log.d(LOG_TAG, "Create new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDataBase.class, AppDataBase.DATABASE_NAME)

//                       TODO Test용으로 메인스레드에서 실행되게 설정해준다. 나중에 제거해야함함
                       .allowMainThreadQueries()
                        .build();
            }
        }

        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract  WeatherDao weatherDao();
}
