package com.example.secondsunshine.Data;



import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExcutors {
    private  static Object LOCK = new Object();
    private  static AppExcutors sInstance;
    private  final Executor diskIO;
    private  final Executor mainThread;
    private  final Executor newworkIO;

    public AppExcutors(Executor diskIO, Executor newworkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.newworkIO = newworkIO;
        this.mainThread = mainThread;
    }

    public  static  AppExcutors getInstance()
    {
        if(sInstance == null)
        {
            synchronized (LOCK)
            {
                sInstance = new AppExcutors(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3),
                        new MainThreadExcutor());
            }
        }

        return sInstance;
    }


    public Executor distIO() {
        return diskIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    public Executor newworkIO() {
        return newworkIO;
    }

    private  static class MainThreadExcutor implements Executor
    {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());


        @Override
        public void execute(Runnable command) {

        }
    }

}
