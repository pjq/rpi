package me.pjq.rpicar;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by i329817(Jianqing.Peng@sap.com) on 30/07/2017.
 */

public interface CarControllerApi {
    @POST("/api/car/controller")
    Observable<CarAction> sendCommand(@Body CarAction command);
}
