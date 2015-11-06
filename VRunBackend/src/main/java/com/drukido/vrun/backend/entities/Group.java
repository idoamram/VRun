package com.drukido.vrun.backend.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.Date;

@Entity
public class Group {

    /**
     * Unique identifier of this Entity in the database.
     */
    @Id
    private Long key;

    private long targetDistance;

    private String targetDuration;

    private Date targetTime;

    /**
     * The id of the user that establish the group.
     */
    private long founderId;

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public long getTargetDistance() {
        return targetDistance;
    }

    public void setTargetDistance(long targetDistance) {
        this.targetDistance = targetDistance;
    }

    public String getTargetDuration() {
        return targetDuration;
    }

    public void setTargetDuration(String targetDuration) {
        this.targetDuration = targetDuration;
    }

    public Date getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(Date targetTime) {
        this.targetTime = targetTime;
    }

    public long getFounderId() {
        return founderId;
    }

    public void setFounderId(long founderId) {
        this.founderId = founderId;
    }
}
