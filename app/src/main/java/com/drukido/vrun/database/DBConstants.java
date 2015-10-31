package com.drukido.vrun.database;

/**
 * Created by Ido on 10/31/2015.
 */
public class DBConstants {
    /** Tables **/
    public static final String T_USER = "user";
    public static final String T_RUN  = "run";

    /** Columns **/
    public static final String COL_ID               = "id";
    public static final String COL_USER_NAME        = "userName";
    public static final String COL_PASSWORD         = "password";
    public static final String COL_EMAIL            = "email";
    public static final String COL_PHONE_NUMBER     = "phoneNumber";
    public static final String COL_FIRST_NAME       = "firstName";
    public static final String COL_LAST_NAME        = "lastName";
    public static final String COL_JOIN_TIME        = "join_time";
    public static final String COL_RUNS             = "runs";
    public static final String COL_TIME             = "time";
    public static final String COL_DISTANCE         = "distance";
    public static final String COL_DURATION         = "duration";
    public static final String COL_ATTENDING        = "attending";
    public static final String COL_NOT_ATTENDING    = "notAttending";
    public static final String COL_TRACK            = "track";
    public static final String COL_RUN_CREATOR      = "run_creator";
    public static final String COL_TRACK_CREATOR    = "track_creator";

    /** Create table commands **/
    public static final String CREATE_T_USER = "CREATE TABLE " + T_USER + " (" +
            COL_ID              + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_USER_NAME       + " TEXT UNIQUE, " +
            COL_PASSWORD        + " TEXT, " +
            COL_EMAIL           + " TEXT UNIQUE, " +
            COL_PHONE_NUMBER    + " TEXT UNIQUE, " +
            COL_FIRST_NAME      + " TEXT, " +
            COL_LAST_NAME       + " TEXT, " +
            COL_JOIN_TIME       + " TEXT, " +
            COL_RUNS            + " TEXT" + ");";

    public static final String CREATE_T_RUN = "CREATE TABLE " + T_RUN + " (" +
            COL_ID              + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_TIME            + " TEXT, " +
            COL_DISTANCE        + " INTEGER, " +
            COL_DURATION        + " INTEGER, " +
            COL_ATTENDING       + " TEXT, " +
            COL_NOT_ATTENDING   + " TEXT, " +
            COL_TRACK           + " TEXT, " +
            COL_RUN_CREATOR     + " TEXT, " +
            COL_TRACK_CREATOR   + " TEXT, " +
            "FOREIGN KEY(" + COL_RUN_CREATOR + ") REFERENCES " +
            T_USER + "(" + COL_ID + "), " +
            "FOREIGN KEY(" + COL_TRACK_CREATOR + ") REFERENCES " +
            T_USER + "(" + COL_ID + ")" + ");";
}
