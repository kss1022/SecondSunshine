package com.example.secondsunshine.Sync;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;

public class WeatherLisenableWorker extends ListenableWorker {

    private AsyncTask mBackgroundTask;


    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    public WeatherLisenableWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Context context = getApplicationContext();
                WeatherSyncTask.syncWeather(context);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
            }
        };

        mBackgroundTask.execute();

        return (ListenableFuture<Result>) Result.success();
    }

    @Override
    public void onStopped() {
        super.onStopped();
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
    }
}
