package com.drukido.vrun.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocalDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "vrun_local_db";
    private static final int DATABASE_VERSION = 1;

    public LocalDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(DBConstants.CREATE_T_USER);
            db.execSQL(DBConstants.CREATE_T_RUN);
            Log.i(DATABASE_NAME, "database created");
        }
        catch (SQLException e){
            Log.e(DATABASE_NAME, e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(LocalDBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        // TODO: Execute DROP TABLE commands
        onCreate(db);
    }
}
