package me.pjq.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

/**
 * Pet
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-02-23T07:57:56.431Z")
@Entity
public class RpiWeatherItem {
    @JsonProperty("id")
    @Id
    @GeneratedValue
    private Long id = null;

    @JsonProperty("timestamp")
    private long timestamp;

    @JsonProperty("pm25")
    private float pm25;

    @JsonProperty("pm25_cf")
    private float pm25_cf ;

    @JsonProperty("pm10")
    private float pm10 ;

    @JsonProperty("pm10_cf")
    private float pm10_cf;

    @JsonProperty("temperature")
    private float temperature ;

    @JsonProperty("humidity")
    private float humidity ;

    @JsonProperty("raw_data")
    private String raw_data = null;

    @JsonProperty("location")
    private String location= null;

    @JsonProperty("date")
    private String date= null;

    @JsonProperty("alt")
    private float alt;

    @JsonProperty("lat")
    private float lat;

    public RpiWeatherItem id(Long id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     *
     * @return id
     **/
    @ApiModelProperty(required = true, value = "")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ApiModelProperty(required = false, value = "")
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Get pm25
     *
     * @return pm25
     **/
    @ApiModelProperty(required = true, value = "")
    public float getPm25() {
        return pm25;
    }

    public void setPm25(float pm25) {
        this.pm25 = pm25;
    }

    public RpiWeatherItem pm25(float pm25) {
        this.pm25 = pm25;
        return this;
    }

    /**
     * Get temperature
     *
     * @return temperature
     **/
    @ApiModelProperty(required = true, value = "")
    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getPm25_cf() {
        return pm25_cf;
    }

    public void setPm25_cf(float pm25_cf) {
        this.pm25_cf = pm25_cf;
    }

    public float getPm10() {
        return pm10;
    }

    public void setPm10(float pm10) {
        this.pm10 = pm10;
    }

    public float getPm10_cf() {
        return pm10_cf;
    }

    public void setPm10_cf(float pm10_cf) {
        this.pm10_cf = pm10_cf;
    }

    /**
     * Get humidity
     *
     * @return humidity
     **/
    @ApiModelProperty(value = "")
    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    /**
     * Get raw_data
     *
     * @return raw_data
     **/
    @ApiModelProperty(value = "")
    public String getRaw_data() {
        return raw_data;
    }

    public void setRaw_data(String raw_data) {
        this.raw_data = raw_data;
    }

    /**
     * Get location
     *
     * @return location
     **/
    @ApiModelProperty(value = "")
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @ApiModelProperty(value = "")
    public float getAlt() {
        return alt;
    }

    public void setAlt(float alt) {
        this.alt = alt;
    }

    @ApiModelProperty(value = "")
    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RpiWeatherItem pet = (RpiWeatherItem) o;
        return Objects.equals(this.id, pet.id) &&
                Objects.equals(this.pm25, pet.pm25) &&
                Objects.equals(this.humidity, pet.humidity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pm25, humidity);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Pet {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
        sb.append("    temperature: ").append(toIndentedString(temperature)).append("\n");
        sb.append("    pm25: ").append(toIndentedString(pm25)).append("\n");
        sb.append("    pm25_cf: ").append(toIndentedString(pm25_cf)).append("\n");
        sb.append("    humidity: ").append(toIndentedString(humidity)).append("\n");
        sb.append("    pm10: ").append(toIndentedString(pm10)).append("\n");
        sb.append("    pm10_cf: ").append(toIndentedString(pm10_cf)).append("\n");
        sb.append("    raw_data: ").append(toIndentedString(raw_data)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

