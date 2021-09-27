package com.example.secondsunshine.Sync;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.example.secondsunshine.Data.AppDataBase;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class SyncUtils {

    private static final int SYNC_INTERVAL_MINUTES = 1;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.MINUTES.
            toSeconds(SYNC_INTERVAL_MINUTES);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS;


    private static final String SUNSHINE_SYNC_TAG = "sunshine-sync";

    private static boolean sInitialized;


    public static void schedualeWatherUpdate(@NotNull final Context context)
    {
        Driver driver =  new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job myJob = dispatcher.newJobBuilder()
                .setService(WeatherFirebaseJobService.class)
                .setTag(SUNSHINE_SYNC_TAG)
                .setConstraints(
                        Constraint.ON_ANY_NETWORK
                )
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(10,
                        15))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(myJob);
    }


    synchronized public static void initialize(@NotNull final Context context) {
        if (sInitialized) return;

        sInitialized = true;


        schedualeWatherUpdate(context);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                AppDataBase dataBase = AppDataBase.getInstance(context);

                if ( !dataBase.weatherDao().isExist()) {
                    startImmediateSync(context);
                }

                return null;
            }
        }.execute();

    }

    public static void startImmediateSync(@NotNull Context context) {
        Intent updateWeatherIntent = new Intent(context, WeatherIntentService.class);
        context.startService(updateWeatherIntent);
    }

}
