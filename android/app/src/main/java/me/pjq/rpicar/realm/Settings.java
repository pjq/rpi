package me.pjq.rpicar.realm;

import io.realm.RealmObject;

/**
 * Created by i329817(Jianqing.Peng@sap.com) on 05/08/2017.
 */

public class Settings extends RealmObject {
    public String host;
    public int duration;
    public int speed;
    public int name;
    public String weatherJson;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getWeatherJson() {
        return weatherJson;
    }

    public void setWeatherJson(String weatherJson) {
        this.weatherJson = weatherJson;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "host='" + host + '\'' +
                ", duration=" + duration +
                ", speed=" + speed +
                ", name=" + name +
                '}';
    }
}
