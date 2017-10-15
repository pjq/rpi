package me.pjq.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

/**
 * Pet
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-09-11T07:57:56.431Z")
public class ServerStatus {
    @JsonProperty("name")
    private String name = null;

    @JsonProperty("weather")
    RpiWeatherItem weather;

    public RpiWeatherItem getWeather() {
        return weather;
    }

    public void setWeather(RpiWeatherItem weather) {
        this.weather = weather;
    }
}

