package me.pjq.rpicar

import android.app.Application
import android.content.Context

/**
 * Created by i329817(Jianqing.Peng@sap.com) on 18/11/2017.
 */

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        context = this
    }

    companion object {
        lateinit var context: Context
            internal set
    }
}
