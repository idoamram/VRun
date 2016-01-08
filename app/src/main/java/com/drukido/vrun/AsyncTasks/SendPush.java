package com.drukido.vrun.AsyncTasks;

import android.os.AsyncTask;

import com.drukido.vrun.entities.Group;
import com.drukido.vrun.entities.User;
import com.drukido.vrun.interfaces.OnAsyncTaskFinishedListener;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ido on 1/1/2016.
 */
public class SendPush extends AsyncTask<Void, Void, Boolean> {

    String message = null;
    User user;
    OnAsyncTaskFinishedListener listener;

    public SendPush(@NotNull String message, OnAsyncTaskFinishedListener listener) {
        this.message = message;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            user = (User) ParseUser.getCurrentUser().fetchIfNeeded();
            Group group = user.getGroup().fetchIfNeeded();

            ParsePush push = new ParsePush();
            push.setChannel(group.getName());

            JSONObject dataJson = new JSONObject();
//            dataJson.put("userId", user.getObjectId());
            dataJson.put("title", user.getName());
            dataJson.put("message", message);

            JSONObject pushJson = new JSONObject();
            pushJson.put("data", dataJson);
            pushJson.put("is_background", false);
//            push.setMessage(message);
            push.setData(pushJson);
            push.send();

            return true;

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result) {
            listener.onSuccess(result);
        } else {
            listener.onError("Push sending failed");
        }
    }
}
