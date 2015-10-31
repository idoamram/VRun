package com.drukido.vrun.entities;

import android.content.Context;
import com.drukido.vrun.database.DBConstants;
import com.drukido.vrun.database.DBObject;
import com.drukido.vrun.database.annotations.Column;
import com.drukido.vrun.database.annotations.ColumnGetter;
import com.drukido.vrun.database.annotations.ColumnSetter;
import com.drukido.vrun.database.annotations.EntityArraySetter;
import com.drukido.vrun.database.annotations.EntitySetter;
import com.drukido.vrun.database.annotations.ForeignKeyEntity;
import com.drukido.vrun.database.annotations.ForeignKeyEntityArray;
import com.drukido.vrun.database.annotations.PrimaryKeySetter;
import com.drukido.vrun.database.annotations.TableName;
import com.drukido.vrun.database.annotations.TrackColumn;
import com.drukido.vrun.database.annotations.TrackColumnSetter;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.Date;

@TableName(name = DBConstants.T_RUN)
public class Run extends DBObject{

    // Public Constructor
    public Run(Context context) {
        super(context);
    }

    /*************************************************************************/
    /** Members **/
    @Column(name = DBConstants.COL_ID)
    private long id;

    @Column(name = DBConstants.COL_TIME)
    private Date time;

    @Column(name = DBConstants.COL_DISTANCE)
    private long distance;

    @Column(name = DBConstants.COL_DURATION)
    private long durationInSeconds;

    @ForeignKeyEntityArray(fkColumnName = DBConstants.COL_ATTENDING, entityClass = User.class)
    private ArrayList<User> attending;

    @ForeignKeyEntityArray(fkColumnName = DBConstants.COL_NOT_ATTENDING, entityClass = User.class)
    private ArrayList<User> notAttending;

    @TrackColumn(columnName = DBConstants.COL_TRACK, locationUnitClass = LatLng.class)
    private ArrayList<LatLng> track;

    @ForeignKeyEntity(fkColumnName = DBConstants.COL_RUN_CREATOR)
    private User runCreator;

    @ForeignKeyEntity(fkColumnName = DBConstants.COL_TRACK_CREATOR)
    private User trackCreator;
    /*************************************************************************/

    /*************************************************************************/
    /** Setters **/
    @PrimaryKeySetter
    @ColumnSetter(columnName = DBConstants.COL_ID, type = TYPE_LONG)
    public void setId(long id) {
        this.id = id;
    }

    @ColumnSetter(columnName = DBConstants.COL_TIME, type = TYPE_DATE)
    public void setTime(Date time) {
        this.time = time;
    }

    @ColumnSetter(columnName = DBConstants.COL_DISTANCE, type = TYPE_LONG)
    public void setDistance(long distance) {
        this.distance = distance;
    }

    @ColumnSetter(columnName = DBConstants.COL_DURATION, type = TYPE_LONG)
    public void setDurationInSeconds(long durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    @EntityArraySetter(fkColumnName = DBConstants.COL_ATTENDING, entityClass = User.class)
    public void setAttending(ArrayList<User> attending) {
        this.attending = attending;
    }

    @EntityArraySetter(fkColumnName = DBConstants.COL_NOT_ATTENDING, entityClass = User.class)
    public void setNotAttending(ArrayList<User> notAttending) {
        this.notAttending = notAttending;
    }

    @EntitySetter(fkColumnName = DBConstants.COL_TRACK_CREATOR, entityClass = User.class)
    public void setTrackCreator(User trackCreator) {
        this.trackCreator = trackCreator;
    }

    @EntitySetter(fkColumnName = DBConstants.COL_RUN_CREATOR, entityClass = User.class)
    public void setRunCreator(User runCreator) {
        this.runCreator = runCreator;
    }

    @TrackColumnSetter(columnName = DBConstants.COL_TRACK, locationUnitClass = LatLng.class)
    public void setTrack(ArrayList<LatLng> track) {
        this.track = track;
    }
    /*************************************************************************/

    /*************************************************************************/
    /** Getters **/
    @ColumnGetter(columnName = DBConstants.COL_ID)
    public long getId() {
        return id;
    }

    @ColumnGetter(columnName = DBConstants.COL_TIME)
    public Date getTime() {
        return time;
    }

    @ColumnGetter(columnName = DBConstants.COL_DISTANCE)
    public long getDistance() {
        return distance;
    }

    @ColumnGetter(columnName = DBConstants.COL_DURATION)
    public long getDurationInSeconds() {
        return durationInSeconds;
    }

    @ColumnGetter(columnName = DBConstants.COL_ATTENDING)
    public ArrayList<User> getAttending() {
        return attending;
    }

    @ColumnGetter(columnName = DBConstants.COL_NOT_ATTENDING)
    public ArrayList<User> getNotAttending() {
        return notAttending;
    }

    @ColumnGetter(columnName = DBConstants.COL_TRACK)
    public ArrayList<LatLng> getTrack() {
        return track;
    }

    @ColumnGetter(columnName = DBConstants.COL_RUN_CREATOR)
    public User getRunCreator() {
        return runCreator;
    }

    @ColumnGetter(columnName = DBConstants.COL_TRACK_CREATOR)
    public User getTrackCreator() {
        return trackCreator;
    }
    /*************************************************************************/

    @Override
    public String toString() {
        return "Run id: " + id;
    }
}
