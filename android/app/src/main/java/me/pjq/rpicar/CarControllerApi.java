package me.pjq.rpicar;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by i329817(Jianqing.Peng@sap.com) on 30/07/2017.
 */

public interface CarControllerApi {
    @POST("/api/car/controller")
    Observable<CarAction> sendCommand(@Body CarAction command);

    @GET("/api/weathers")
    Observable<List<WeatherItem>> getWeatherItems(@Query("page") int page, @Query("size") int size);

    @GET("/api/car/sensorstatus")
    Observable<SensorStatus> getSensorStatus();
}
