package com.drukido.vrun.utils;

/**
 * Created by Ido on 11/1/2015.
 */
public class Duration {
    private long hours;
    private long minutes;
    private long seconds;

    public long getHours() {
        return hours;
    }

    public void setHours(long hours) {
        this.hours = hours;
    }

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    @Override
    public String toString() {
        return hours + "," + minutes + "," + seconds;
    }

    public static Duration fromString(String durationString) {
        String args[] = durationString.split(",");

        Duration duration = new Duration();
        duration.setHours(Long.valueOf(args[0]));
        duration.setMinutes(Long.valueOf(args[1]));
        duration.setSeconds(Long.valueOf(args[2]));

        return duration;
    }
}
