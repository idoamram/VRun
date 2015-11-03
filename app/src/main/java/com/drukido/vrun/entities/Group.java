package com.drukido.vrun.entities;

import android.content.Context;
import com.drukido.vrun.database.DBConstants;
import com.drukido.vrun.database.DBObject;
import com.drukido.vrun.database.annotations.Column;
import com.drukido.vrun.database.annotations.ColumnGetter;
import com.drukido.vrun.database.annotations.ColumnSetter;
import com.drukido.vrun.database.annotations.EntitySetter;
import com.drukido.vrun.database.annotations.ForeignKeyEntity;
import com.drukido.vrun.database.annotations.PrimaryKey;
import com.drukido.vrun.database.annotations.PrimaryKeySetter;
import com.drukido.vrun.database.annotations.TableName;
import com.drukido.vrun.utils.Duration;
import java.util.Date;

@TableName(name = DBConstants.T_GROUP)
public class Group extends DBObject{

    // Public constructor
    public Group(Context context) {
        super(context);
    }

    /****************************************************/
    /** Members **/
    @PrimaryKey(columnName = DBConstants.COL_ID)
    private long id;

    @Column(name = DBConstants.COL_TARGET_DISTANCE)
    private long targetDistance;

    @Column(name = DBConstants.COL_TARGET_DURATION)
    private Duration targetDuration;

    @Column(name = DBConstants.COL_TARGET_TIME)
    private Date targetTime;

    @ForeignKeyEntity(fkColumnName = DBConstants.COL_FOUNDER)
    private User founder;
    /****************************************************/

    /****************************************************/
    /** Setters **/
    @PrimaryKeySetter
    @ColumnSetter(columnName = DBConstants.COL_ID, type = TYPE_LONG)
    public void setId(long id) {
        this.id = id;
    }

    @ColumnSetter(columnName = DBConstants.COL_TARGET_DURATION, type = TYPE_LONG)
    public void setTargetDistance(long targetDistance) {
        this.targetDistance = targetDistance;
    }

    @ColumnSetter(columnName = DBConstants.COL_TARGET_DURATION, type = TYPE_DURATION)
    public void setTargetDuration(Duration targetDuration) {
        this.targetDuration = targetDuration;
    }

    @ColumnSetter(columnName = DBConstants.COL_TARGET_TIME, type = TYPE_DATE)
    public void setTargetTime(Date targetTime) {
        this.targetTime = targetTime;
    }

    @EntitySetter(fkColumnName = DBConstants.COL_FOUNDER, entityClass = User.class)
    public void setFounder(User founder) {
        this.founder = founder;
    }
    /****************************************************/

    /****************************************************/
    /** Getters **/
    @ColumnGetter(columnName = DBConstants.COL_ID)
    public long getId() {
        return id;
    }

    @ColumnGetter(columnName = DBConstants.COL_TARGET_DISTANCE)
    public long getTargetDistance() {
        return targetDistance;
    }

    @ColumnGetter(columnName = DBConstants.COL_TARGET_DURATION)
    public Duration getTargetDuration() {
        return targetDuration;
    }

    @ColumnGetter(columnName = DBConstants.COL_TARGET_TIME)
    public Date getTargetTime() {
        return targetTime;
    }

    @ColumnGetter(columnName = DBConstants.COL_FOUNDER)
    public User getFounder() {
        return founder;
    }
    /****************************************************/
}
