package me.pjq.rpicar.models;

/**
 * Created by i329817(Jianqing.Peng@sap.com) on 10/09/2017.
 */

public class WeatherItem {

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

    private int id;
    private int alt;
    private int lat;
    private long timestamp;
    private String date;
    private String location;
    private int pm25;
    private int pm25_cf;
    private int pm10;
    private int pm10_cf;
    private double temperature;
    private double humidity;
    private String raw_data;

    public void setId(int id) {
        this.id = id;
    }

    public void setAlt(int alt) {
        this.alt = alt;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPm25(int pm25) {
        this.pm25 = pm25;
    }

    public void setPm25_cf(int pm25_cf) {
        this.pm25_cf = pm25_cf;
    }

    public void setPm10(int pm10) {
        this.pm10 = pm10;
    }

    public void setPm10_cf(int pm10_cf) {
        this.pm10_cf = pm10_cf;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public void setRaw_data(String raw_data) {
        this.raw_data = raw_data;
    }

    public int getId() {
        return id;
    }

    public int getAlt() {
        return alt;
    }

    public int getLat() {
        return lat;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public int getPm25() {
        return (int)(pm25 * 2);
    }

    public int getPm25_cf() {
        return pm25_cf;
    }

    public int getPm10() {
        return pm10;
    }

    public int getPm10_cf() {
        return pm10_cf;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public String getRaw_data() {
        return raw_data;
    }
}
