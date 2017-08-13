package me.pjq.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Pet
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-07-29T07:57:56.431Z")
@Entity
public class CarAction {
    @JsonProperty("id")
    @Id
    @GeneratedValue
    private Long id = null;

    @JsonProperty("timestamp")
    private long timestamp;

    @JsonProperty("action")
    private String action;

    @JsonProperty("duration")
    private long duration;

    @JsonProperty("speed")
    private int speed;

    public CarAction id(Long id) {
        this.id = id;
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CarAction carAction = (CarAction) o;

        if (timestamp != carAction.timestamp) return false;
        if (Float.compare(carAction.duration, duration) != 0) return false;
        if (id != null ? !id.equals(carAction.id) : carAction.id != null) return false;
        if (action != null ? !action.equals(carAction.action) : carAction.action != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (action != null ? action.hashCode() : 0);
        result = 31 * result + (duration != +0.0f ? Float.floatToIntBits(duration) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CarAction{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", action=" + action +
                ", duration=" + duration +
                '}';
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

    public enum Action {
        UP("up"),
        DOWN("down"),
        LEFT("left"),
        RIGHT("right"),
        STOP("stop"),
        AUTO_DRIVE("auto_drive"),
        SPEED("speed"),
        UNKNOWN("unknown");
        String action;

        Action(String action) {
            this.action = action;
        }

        public static Action toAction(String action) {
            Action act = null;
            if (UP.action.equalsIgnoreCase(action)) {
                act = UP;
            } else  if (DOWN.action.equalsIgnoreCase(action)) {
                act = DOWN;
            } else  if (LEFT.action.equalsIgnoreCase(action)) {
                act = LEFT;
            } else  if (RIGHT.action.equalsIgnoreCase(action)) {
                act = RIGHT;
            } else  if (STOP.action.equalsIgnoreCase(action)) {
                act = STOP;
            } else  if (AUTO_DRIVE.action.equalsIgnoreCase(action)) {
                act = AUTO_DRIVE;
            } else  if (SPEED.action.equalsIgnoreCase(action)) {
                act = SPEED;
            } else {
                act = UNKNOWN;
            }

            return act;
        }

        public boolean isUp() {
            return UP.action.equalsIgnoreCase(action);
        }

        public boolean isDown() {
            return DOWN.action.equalsIgnoreCase(action);
        }

        public boolean isLeft() {
            return LEFT.action.equalsIgnoreCase(action);
        }

        public boolean isRight() {
            return RIGHT.action.equalsIgnoreCase(action);
        }

        public boolean isStop() {
            return STOP.action.equalsIgnoreCase(action);
        }

        public boolean isAutoDrive() {
            return AUTO_DRIVE.action.equalsIgnoreCase(action);
        }

        public boolean isSpeed() {
            return SPEED.action.equalsIgnoreCase(action);
        }
    }
}

