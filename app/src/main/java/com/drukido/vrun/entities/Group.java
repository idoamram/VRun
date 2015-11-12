package com.drukido.vrun.entities;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Group")
public class Group extends ParseObject {

    public static final String KEY_FOUNDER = "founder";
    public static final String KEY_TARGET_DATE = "targetDate";
    public static final String KEY_TARGET_DURATION = "targetDuration";
    public static final String KEY_TARGET_DISTANCE = "targetDistance";
    public static final String KEY_BEST_DISTANCE = "bestDistance";
    public static final String KEY_NAME = "name";

    public Group() {
    }

    public long getBestDistance() {
        return getLong(KEY_BEST_DISTANCE);
    }

    public void setBestDistance(long bestDistance) {
        this.put(KEY_BEST_DISTANCE, bestDistance);
    }

    public ParseUser getFounder() {
        return getParseUser(KEY_FOUNDER);
    }

    public void setFounder(ParseUser founder) {
        this.put(KEY_FOUNDER, founder);
    }

    public Date getTargetDate() {
        return (Date) get(KEY_TARGET_DATE);
    }

    public void setTargetDate(Date targetDate) {
        this.put(KEY_TARGET_DATE, targetDate);
    }

    public String getTargetDuration() {
        return getString(KEY_TARGET_DURATION);
    }

    public void setTargetDuration(String targetDuration) {
        this.put(KEY_TARGET_DURATION, targetDuration);
    }

    public long getTargetDistance() {
        return getLong(KEY_TARGET_DISTANCE);
    }

    public void setTargetDistance(long targetDistance) {
        this.put(KEY_TARGET_DISTANCE, targetDistance);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        this.put(KEY_NAME, name);
    }
}
