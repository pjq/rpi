package me.pjq.rpicar

import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by i329817(Jianqing.Peng@sap.com) on 30/07/2017.
 */

class CarControllerApiService private constructor() {


    lateinit var api: CarControllerApi
        internal set
    internal lateinit var mOkHttpClient: OkHttpClient

    object Config {
        var HOST = "http://192.168.31.180"

        private val isSshRediret: Boolean
            get() = HOST.contains("pjq")

        fun HOST(): String {
            return HOST
        }

        fun API_URL(): String {
            var port = ":8080"
            if (isSshRediret) {
                port = ":18080"
            }

            return HOST() + port
        }

        fun STREAM_URL(): String {
            var port = ":8092"
            if (isSshRediret) {
                port = ":18092"
            }

            return HOST() + port
        }

        fun CAPTURE_VIDEO_URL(): String {
            var port = ""
            if (isSshRediret) {
                port = ":18091"
            }

            return HOST() + port + "/motion"
        }
    }

    init {
        init()
    }

    fun init() {
        val builder = OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)//设置超时时间
                .readTimeout(10, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS)//设置写入超时时间
        //        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        //        Cache cache = new Cache(App.getContext().getCacheDir(), cacheSize);
        //        builder.cache(cache);
        //        builder.addInterceptor(interceptor);
        mOkHttpClient = builder.build()

        val builder2 = Retrofit.Builder()
        builder2.client(mOkHttpClient)
        builder2.addConverterFactory(GsonConverterFactory.create())
        builder2.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        val retrofit = builder2.baseUrl(Config.API_URL()).build()
        api = retrofit.create(CarControllerApi::class.java)
    }

    fun getmOkHttpClient(): OkHttpClient {
        return mOkHttpClient
    }

    companion object {

        private var INSTANCE: CarControllerApiService? = null

        val instance: CarControllerApiService
            get() {
                if (null == INSTANCE) {
                    INSTANCE = CarControllerApiService()
                }

                return INSTANCE!!
            }
    }
}
