package me.pjq.rpicar;

import io.realm.RealmObject;

/**
 * Created by i329817(Jianqing.Peng@sap.com) on 05/08/2017.
 */

public class Settings extends RealmObject {
    String host;
    int duration;
    int speed;

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
}
