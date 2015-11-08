package com.drukido.vrun.entities;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;

@ParseClassName("Run")
public class Run extends ParseObject {

    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_NOT_RESPOND = "notRespond";
    public static final String KEY_NOT_ATTENDING = "notAttending";
    public static final String KEY_ATTENDING = "attending";
    public static final String KEY_RUN_CREATOR = "runCreator";
    public static final String KEY_GROUP = "group";
    public static final String KEY_RUN_TIME = "runTime";

    public Run() {
    }

    public long getDistance() {
        return getLong(KEY_DISTANCE);
    }

    public void setDistance(long distance) {
        this.put(KEY_DISTANCE, distance);
    }

    public String getDuration() {
        return getString(KEY_DURATION);
    }

    public void setDuration(String duration) {
        this.put(KEY_DURATION, duration);
    }

    public ParseUser getCreator() {
        return getParseUser(KEY_RUN_CREATOR);
    }

    public void setCreator(ParseUser creator) {
        this.put(KEY_RUN_CREATOR, creator);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<ParseUser> getAttending() {
        return (ArrayList<ParseUser>) get(KEY_ATTENDING);
    }

    public void setAttending(ArrayList<ParseUser> attending) {
        this.put(KEY_ATTENDING, attending);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<ParseUser> getNotAttending() {
        return (ArrayList<ParseUser>) get(KEY_NOT_ATTENDING);
    }

    public void setNotAttending(ArrayList<ParseUser> notAttending) {
        this.put(KEY_NOT_ATTENDING, notAttending);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<ParseUser> getNotRespond() {
        return (ArrayList<ParseUser>) get(KEY_NOT_RESPOND);
    }

    public void setNotRespond(ArrayList<ParseUser> notRespond) {
        this.put(KEY_NOT_RESPOND, notRespond);
    }

    public Group getGroup() {
        return (Group) getParseObject(KEY_GROUP);
    }

    public void setGroup(Group group) {
        this.put(KEY_GROUP, group);
    }

    public Date getRunTime() {
        return getDate(KEY_RUN_TIME);
    }

    public void setRunTime(Date runTime) {
        this.put(KEY_RUN_TIME, runTime);
    }
}
