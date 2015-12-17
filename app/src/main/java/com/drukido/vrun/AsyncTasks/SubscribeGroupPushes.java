package com.drukido.vrun.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.drukido.vrun.Constants;
import com.drukido.vrun.entities.Group;
import com.drukido.vrun.entities.User;
import com.drukido.vrun.interfaces.OnAsyncTaskFinishedListener;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class SubscribeGroupPushes extends AsyncTask<Void, Void, Boolean> {

    Context mContext;
    Group userGroup;
    OnAsyncTaskFinishedListener listener;

    public SubscribeGroupPushes(Context context, OnAsyncTaskFinishedListener listener) {
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        try {
            User user = (User)ParseUser.getCurrentUser();
            user.fetch();
            userGroup = user.getGroup();
            userGroup.fetch();

            return true;

        } catch (ParseException e) {
            Toast.makeText(mContext, "Failed to subscribe...", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    protected void onPostExecute(final Boolean result) {
        super.onPostExecute(result);

        if(result){
            ParsePush.subscribeInBackground(userGroup.getName(), new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        listener.onSuccess(result);
                    } else {
                        listener.onError(String.valueOf(result));
                    }
                }
            });
        } else {
            listener.onError(String.valueOf(result));
        }
    }
}
