package me.pjq.rpicar;

import android.app.Application;
import android.content.Context;

/**
 * Created by i329817(Jianqing.Peng@sap.com) on 18/11/2017.
 */

public class BaseApplication extends Application {
    static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
    }

    public static Context getContext() {
        return context;
    }
}
