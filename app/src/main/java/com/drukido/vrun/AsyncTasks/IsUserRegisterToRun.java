package com.drukido.vrun.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.drukido.vrun.Constants;
import com.drukido.vrun.entities.Run;
import com.drukido.vrun.interfaces.OnAsyncTaskFinishedListener;
import com.parse.ParseException;
import com.parse.ParseUser;

public class IsUserRegisterToRun extends AsyncTask<Void, Void, Integer> {

//    public static final int RESULT_NOT_REGISTERED = 0;
    public static final int RESULT_ATTENDING = 1;
    public static final int RESULT_NOT_ATTENDING = 2;

    OnAsyncTaskFinishedListener mListener;
    Run mRun;
    ParseUser mUser;
    boolean mIsSucceeded = false;

    public IsUserRegisterToRun(Run run, ParseUser user, OnAsyncTaskFinishedListener listener) {
        this.mListener = listener;
        this.mRun = run;
        this.mUser = user;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        try {
            mRun.fetch();
            mUser.fetch();

            if (mRun.getAttending() != null) {
                for(ParseUser currUser:mRun.getAttending()) {
                    if(currUser.getObjectId().equals(mUser.getObjectId())) {
                        mIsSucceeded = true;
                        return RESULT_ATTENDING;
                    }
                }
            }

            if (mRun.getNotAttending() != null) {
                for(ParseUser currUser:mRun.getNotAttending()) {
                    if(currUser.getObjectId().equals(mUser.getObjectId())) {
                        mIsSucceeded = true;
                        return RESULT_NOT_ATTENDING;
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            if (e.getMessage() != null) {
                Log.e(Constants.LOG_TAG, e.getMessage());
                mListener.onError(e.getMessage());
            } else {
                mListener.onError("");
            }
        }

        mIsSucceeded = true;
        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if(result != null && (mIsSucceeded)) {
            mListener.onSuccess(result);
        }
    }
}
