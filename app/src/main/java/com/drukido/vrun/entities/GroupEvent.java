package com.drukido.vrun.entities;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("GroupEvent")
public class GroupEvent extends ParseObject {

    public static final String KEY_MADE_DISTANCE = "madeDistance";
    public static final String KEY_MADE_DURATION = "madeDuration";
    public static final String KEY_GROUP = "group";
    public static final String KEY_OWNER = "owner";
    public static final String KEY_TARGET_DISTANCE = "targetDistance";
    public static final String KEY_TARGET_DURATION = "targetDuration";
    public static final String KEY_EVENT_TIME = "eventTime";
    public static final String KEY_TITLE = "title";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_LOCATION_TITLE = "locationTitle";

    public GroupEvent() {
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(KEY_LOCATION);
    }

    public void setLocation(ParseGeoPoint location) {
        this.put(KEY_LOCATION,location);
    }

    public long getMadeDistance() {
        return getLong(KEY_MADE_DISTANCE);
    }

    public void setMadeDistance(long madeDistance) {
        this.put(KEY_MADE_DISTANCE, madeDistance);
    }

    public long getTargetDistance() {
        return getLong(KEY_TARGET_DISTANCE);
    }

    public void setTargetDistance(long targetDistance) {
        this.put(KEY_TARGET_DISTANCE, targetDistance);
    }

    public String getEventTitle() {
        return getString(KEY_TITLE);
    }

    public void setEventTitle(String title) {
        this.put(KEY_TITLE, title);
    }

    public String getMadeDuration() {
        return getString(KEY_MADE_DURATION);
    }

    public void setMadeDuration(String madeDuration) {
        this.put(KEY_MADE_DURATION, madeDuration);
    }

    public String getTargetDuration() {
        return getString(KEY_TARGET_DURATION);
    }

    public void setTargetDuration(String targetDuration) {
        this.put(KEY_TARGET_DURATION, targetDuration);
    }

    public Group getGroup() {
        return (Group) getParseObject(KEY_GROUP);
    }

    public void setGroup(Group group) {
        this.put(KEY_GROUP, group);
    }

    public ParseUser getOwner() {
        return getParseUser(KEY_OWNER);
    }

    public void setOwner(ParseUser owner) {
        this.put(KEY_OWNER, owner);
    }

    public Date getEventTime() {
        return getDate(KEY_EVENT_TIME);
    }

    public void setEventTime(Date eventTime) {
        this.put(KEY_EVENT_TIME, eventTime);
    }

    public void setLocationTitle(String locationTitle) {
        this.put(KEY_LOCATION_TITLE,locationTitle);
    }

    public String getLocationTitle() {
        return getString(KEY_LOCATION_TITLE);
    }

    public static ParseQuery<GroupEvent> getQuery(){
        return ParseQuery.getQuery(GroupEvent.class);
    }

    /*************** Static Methods ***************/

    public static void getComingEvent(String groupId, FindCallback<GroupEvent> findCallback){
        ParseQuery<GroupEvent> query = getQuery();
        query.whereEqualTo(GroupEvent.KEY_GROUP,
                Group.createWithoutData(Group.class, groupId));
        query.whereGreaterThan(GroupEvent.KEY_EVENT_TIME, new Date());
        query.orderByAscending(GroupEvent.KEY_EVENT_TIME);
        query.setLimit(1);
        query.findInBackground(findCallback);
    }

}
