package me.pjq.rpicar.models

/**
 * Created by i329817(Jianqing.Peng@sap.com) on 10/09/2017.
 */

class WeatherItem {

    /**
     * id : 15
     * alt : 0
     * lat : 0
     * timestamp : 1504980523833
     * date : Sep 10, 2017 2:08:43 AM
     * location : home
     * pm25 : 36
     * pm25_cf : 41
     * pm10 : 41
     * pm10_cf : 41
     * temperature : 25.9
     * humidity : 69.8
     * raw_data :
     */

    var id: Int = 0
    var alt: Int = 0
    var lat: Int = 0
    var timestamp: Long = 0
    var date: String? = null
    var location: String? = null
    var pm25: Int = 0
        get() = (field * 2).toInt()
    var pm25_cf: Int = 0
    var pm10: Int = 0
    var pm10_cf: Int = 0
    var temperature: Double = 0.toDouble()
    var humidity: Double = 0.toDouble()
    var raw_data: String? = null
}
