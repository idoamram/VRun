package com.drukido.vrun.entities;

import android.content.Context;

import com.drukido.vrun.database.DBConstants;
import com.drukido.vrun.database.DBObject;
import com.drukido.vrun.database.annotations.Column;
import com.drukido.vrun.database.annotations.ColumnGetter;
import com.drukido.vrun.database.annotations.ColumnSetter;
import com.drukido.vrun.database.annotations.EntityArraySetter;
import com.drukido.vrun.database.annotations.ForeignKeyEntityArray;
import com.drukido.vrun.database.annotations.PrimaryKey;
import com.drukido.vrun.database.annotations.TableName;

import java.util.ArrayList;
import java.util.Date;

@TableName(name = DBConstants.T_USER)
public class User extends DBObject{

    // Public Constructor
    public User(Context context) {
        super(context);
    }

    /*************************************************************************/
    /** Members **/
    @PrimaryKey(columnName = DBConstants.COL_ID)
    private long id;

    @Column(name = DBConstants.COL_USER_NAME)
    private String userName;

    @Column(name = DBConstants.COL_PASSWORD)
    private String password;

    @Column(name = DBConstants.COL_EMAIL)
    private String email;

    @Column(name = DBConstants.COL_PHONE_NUMBER)
    private String phoneNumber;

    @Column(name = DBConstants.COL_FIRST_NAME)
    private String firstName;

    @Column(name = DBConstants.COL_LAST_NAME)
    private String lastName;

    @Column(name = DBConstants.COL_JOIN_TIME)
    private Date   joinTime;

    @ForeignKeyEntityArray(fkColumnName = DBConstants.COL_RUNS, entityClass = Run.class)
    private ArrayList<Run> runs;
    /*************************************************************************/

    /*************************************************************************/
    /** Setters **/
    @ColumnSetter(columnName = DBConstants.COL_ID, type = TYPE_LONG)
    public void setId(long id) {
        this.id = id;
    }

    @ColumnSetter(columnName = DBConstants.COL_USER_NAME, type = TYPE_STRING)
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @ColumnSetter(columnName = DBConstants.COL_PASSWORD, type = TYPE_STRING)
    public void setPassword(String password) {
        this.password = password;
    }

    @ColumnSetter(columnName = DBConstants.COL_EMAIL, type = TYPE_STRING)
    public void setEmail(String email) {
        this.email = email;
    }

    @ColumnSetter(columnName = DBConstants.COL_PHONE_NUMBER, type = TYPE_STRING)
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @ColumnSetter(columnName = DBConstants.COL_FIRST_NAME, type = TYPE_STRING)
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @ColumnSetter(columnName = DBConstants.COL_LAST_NAME, type = TYPE_STRING)
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @ColumnSetter(columnName = DBConstants.COL_JOIN_TIME, type = TYPE_DATE)
    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }

    @EntityArraySetter(entityClass = Run.class, fkColumnName = DBConstants.COL_RUNS)
    public void setRuns(ArrayList<Run> runs) {
        this.runs = runs;
    }
    /*************************************************************************/

    /*************************************************************************/
    /** Getters **/
    @ColumnGetter(columnName = DBConstants.COL_ID)
    public long getId() {
        return id;
    }

    @ColumnGetter(columnName = DBConstants.COL_USER_NAME)
    public String getUserName() {
        return userName;
    }

    @ColumnGetter(columnName = DBConstants.COL_PASSWORD)
    public String getPassword() {
        return password;
    }

    @ColumnGetter(columnName = DBConstants.COL_EMAIL)
    public String getEmail() {
        return email;
    }

    @ColumnGetter(columnName = DBConstants.COL_PHONE_NUMBER)
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @ColumnGetter(columnName = DBConstants.COL_FIRST_NAME)
    public String getFirstName() {
        return firstName;
    }

    @ColumnGetter(columnName = DBConstants.COL_LAST_NAME)
    public String getLastName() {
        return lastName;
    }

    @ColumnGetter(columnName = DBConstants.COL_JOIN_TIME)
    public Date getJoinTime() {
        return joinTime;
    }

    @ColumnGetter(columnName = DBConstants.COL_RUNS)
    public ArrayList<Run> getRuns() {
        return runs;
    }
    /*************************************************************************/

    @Override
    public String toString() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else return "User id: " + id;
    }
}
