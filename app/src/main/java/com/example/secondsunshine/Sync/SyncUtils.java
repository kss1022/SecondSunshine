package com.example.secondsunshine.Sync;

import android.content.Context;
import android.content.Intent;


import androidx.work.BackoffPolicy;
import androidx.work.Constraints;

import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.secondsunshine.Data.AppDataBase;
import com.example.secondsunshine.Data.AppExcutors;


import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class SyncUtils {
    private static boolean sInitialized;


    synchronized  static void schedualeWatherUpdate(@NotNull final Context context)
    {
        Constraints constraints  = new Constraints.Builder()
                    // The Worker needs Network connectivity
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

        OneTimeWorkRequest request =
                // Tell which work to execute
                new OneTimeWorkRequest.Builder(WeatherLisenableWorker.class)
                        // Sets the input data for the ListenableWorker
                        // If you want to delay the start of work by 60 seconds
                        .setInitialDelay(60, TimeUnit.SECONDS)
                        // Set a backoff criteria to be used when retry-ing
                        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 3000, TimeUnit.MILLISECONDS)
                        // Set additional constraints
                        .setConstraints(constraints)
                        .build();

        WorkManager.getInstance(context).enqueue( request);
    }


    synchronized public static void initialize(@NotNull final Context context) {
        if (sInitialized) return;

        sInitialized = true;


        schedualeWatherUpdate(context);

        AppExcutors.getInstance().distIO().execute(new Runnable() {
            @Override
            public void run() {
                AppDataBase dataBase = AppDataBase.getInstance(context);

                if ( !dataBase.weatherDao().isExist()) {
                    startImmediateSync(context);
                }
            }
        });
    }

    public static void startImmediateSync(@NotNull Context context) {
        Intent updateWeatherIntent = new Intent(context, WeatherIntentService.class);
        context.startService(updateWeatherIntent);
    }

}
