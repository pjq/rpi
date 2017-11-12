package me.pjq.rpicar

import io.reactivex.Observable
import me.pjq.rpicar.models.CarAction
import me.pjq.rpicar.models.SensorStatus
import me.pjq.rpicar.models.WeatherItem
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by i329817(Jianqing.Peng@sap.com) on 30/07/2017.
 */

interface CarControllerApi {

    @GET("/api/car/sensorstatus")
    fun getSensorStatus(): Observable<SensorStatus>

    @POST("/api/car/controller")
    fun sendCommand(@Body command: CarAction): Observable<CarAction>

    @GET("/api/weathers")
    fun getWeatherItems(@Query("page") page: Int, @Query("size") size: Int): Observable<List<WeatherItem>>
}
