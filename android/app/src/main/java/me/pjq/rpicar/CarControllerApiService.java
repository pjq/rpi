package me.pjq.rpicar;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by i329817(Jianqing.Peng@sap.com) on 30/07/2017.
 */

public class CarControllerApiService {

    public static class Config {
        public static String HOST = "http://192.168.31.180";

        public static String HOST() {
            return HOST;
        }

        public static String API_URL() {
            String port = ":8080";
            if (isSshRediret()) {
               port = ":18080";
            }

            return HOST() + port;
        }

        public static String STREAM_URL() {
            String port = ":8092";
            if (isSshRediret()) {
                port = ":18092";
            }

            return HOST() + port;
        }

        public static String CAPTURE_VIDEO_URL() {
            String port = "";
            if (isSshRediret()) {
                port = ":18091";
            }

            return HOST() + port+ "/motion";
        }

        private static boolean isSshRediret() {
            return HOST.contains("pjq");
        }
    }


    CarControllerApi api;
    OkHttpClient mOkHttpClient;

    private static CarControllerApiService INSTANCE;

    public static CarControllerApiService getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new CarControllerApiService();
        }

        return INSTANCE;
    }

    private CarControllerApiService() {
        init();
    }

    public void init() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)//设置超时时间
                .readTimeout(10, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS);//设置写入超时时间
//        int cacheSize = 10 * 1024 * 1024; // 10 MiB
//        Cache cache = new Cache(App.getContext().getCacheDir(), cacheSize);
//        builder.cache(cache);
//        builder.addInterceptor(interceptor);
        mOkHttpClient = builder.build();

        Retrofit.Builder builder2 = new Retrofit.Builder();
        builder2.client(mOkHttpClient);
        builder2.addConverterFactory(GsonConverterFactory.create());
        builder2.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        Retrofit retrofit = builder2.baseUrl(Config.API_URL()).build();
        api = retrofit.create(CarControllerApi.class);
    }

    public CarControllerApi getApi() {
        return api;
    }

    public OkHttpClient getmOkHttpClient() {
        return mOkHttpClient;
    }
}
