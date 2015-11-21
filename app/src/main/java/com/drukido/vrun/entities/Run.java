package com.drukido.vrun.entities;

import android.util.Log;

import com.drukido.vrun.Constants;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public static final String KEY_TARGET_DISTANCE = "targetDistance";
    public static final String KEY_TARGET_DURATION = "targetDuration";
    public static final String KEY_IS_MEASURED = "isMeasured";
    public static final String KEY_TITLE = "title";

    public Run() {
    }

    public Boolean getIsMeasured() {
        return getBoolean(KEY_IS_MEASURED);
    }

    public void setIsMeasured(Boolean isMeasured) {
        this.put(KEY_IS_MEASURED, isMeasured);
    }

    public long getDistance() {
        return getLong(KEY_DISTANCE);
    }

    public void setDistance(long distance) {
        this.put(KEY_DISTANCE, distance);
    }

    public long getTargetDistance() {
        return getLong(KEY_TARGET_DISTANCE);
    }

    public void setTargetDistance(long targetDistance) {
        this.put(KEY_TARGET_DISTANCE, targetDistance);
    }

    public String getRunTitle() {
        return getString(KEY_TITLE);
    }

    public void setRunTitle(String title) {
        this.put(KEY_TITLE, title);
    }

    public String getDuration() {
        return getString(KEY_DURATION);
    }

    public void setDuration(String duration) {
        this.put(KEY_DURATION, duration);
    }

    public String getTargetDuration() {
        return getString(KEY_TARGET_DURATION);
    }

    public void setTargetDuration(String targetDuration) {
        this.put(KEY_TARGET_DURATION, targetDuration);
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

    public static ParseQuery<Run> getQuery(){
        return ParseQuery.getQuery(Run.class);
    }

    /*************** Static Methods ***************/

    public static void getLastRunInBackground(String groupId, FindCallback<Run> findCallback){
        ParseQuery<Run> query = getQuery();
        query.whereEqualTo(Run.KEY_GROUP,
                Group.createWithoutData(Group.class, groupId));
        query.whereLessThan(Run.KEY_RUN_TIME, new Date());
        query.whereGreaterThan(Run.KEY_DISTANCE, 0);
        query.orderByDescending(Run.KEY_RUN_TIME);
        query.setLimit(1);
        query.findInBackground(findCallback);
    }

    public static void getBestRunInBackground(String groupId, FindCallback<Run> findCallback){
        ParseQuery<Run> query = getQuery();
        query.whereEqualTo(Run.KEY_GROUP,
                Group.createWithoutData(Group.class, groupId));
        query.whereGreaterThan(Run.KEY_DISTANCE, 0);
        query.orderByDescending(Run.KEY_DISTANCE);
        query.setLimit(1);
        query.findInBackground(findCallback);
    }

    public static void getAllComingRuns(String groupId, FindCallback<Run> findCallback){
        ParseQuery<Run> query = getQuery();
        query.whereEqualTo(Run.KEY_GROUP,
                Group.createWithoutData(Group.class, groupId));
        query.whereGreaterThanOrEqualTo(Run.KEY_RUN_TIME, new Date());
        query.findInBackground(findCallback);
    }

    public static void getAllPastRuns(String groupId, FindCallback<Run> findCallback) {
        ParseQuery<Run> query = getQuery();
        query.whereEqualTo(Run.KEY_GROUP,
                Group.createWithoutData(Group.class, groupId));
        query.whereLessThanOrEqualTo(Run.KEY_RUN_TIME, new Date());
        query.findInBackground(findCallback);
    }

}
