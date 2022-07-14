package me.pjq.rpicar;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import me.pjq.rpicar.models.Config;
import me.pjq.rpicar.utils.Log;

public enum Monitor {
    instance;
    private static final String TAG = "Monitor";
    final ExecutorService executorService = new ThreadPoolExecutor(2,
            4, 600, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(100), new ThreadPoolExecutor.CallerRunsPolicy());
    long lastCommandTime;
    boolean relayOn = false;
    SimpleClient4IOT home4IOT;

    private Monitor() {
        lastCommandTime = System.currentTimeMillis();

        init();
    }

    public void init() {
        Log.log(TAG, "init");
        new Thread(new Runnable() {
            @Override
            public void run() {
                home4IOT = new SimpleClient4IOT();
//                home4IOT.setListener(listener);
            }
        }).start();
    }

    public SimpleClient4IOT getHome4IOT() {
        return home4IOT;
    }

    public void onCommand() {
        lastCommandTime = System.currentTimeMillis();
    }

}
