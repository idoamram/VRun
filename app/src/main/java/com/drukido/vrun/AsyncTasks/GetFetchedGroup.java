package com.drukido.vrun.AsyncTasks;

import android.os.AsyncTask;

import com.drukido.vrun.entities.Group;
import com.drukido.vrun.entities.User;
import com.drukido.vrun.interfaces.OnAsyncTaskFinishedListener;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Created by Ido on 12/26/2015.
 */
public class GetFetchedGroup extends AsyncTask<Void,Void,Group>{

    OnAsyncTaskFinishedListener listener;

    public GetFetchedGroup(OnAsyncTaskFinishedListener listener){
        this.listener = listener;
    }

    @Override
    protected Group doInBackground(Void... voids) {
        try {
            User user = (User) ParseUser.getCurrentUser();
            user.fetchIfNeeded();
            Group group = user.getGroup();
            group.fetchIfNeeded();
            return group;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Group group) {
        super.onPostExecute(group);
        if(group != null) {
            listener.onSuccess(group);
        } else {
            listener.onError("");
        }
    }
}
