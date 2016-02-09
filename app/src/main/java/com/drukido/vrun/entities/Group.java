package com.drukido.vrun.entities;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

import com.drukido.vrun.R;
import com.drukido.vrun.interfaces.OnAsyncTaskFinishedListener;
import com.drukido.vrun.ui.fragments.GroupFragment;
import com.drukido.vrun.utils.PhotosManager;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;
import java.util.List;

@ParseClassName("Group")
public class Group extends ParseObject {

    public static final String KEY_FOUNDER = "founder";
    public static final String KEY_TARGET_DATE = "targetDate";
    public static final String KEY_TARGET_DURATION = "targetDuration";
    public static final String KEY_TARGET_DISTANCE = "targetDistance";
    public static final String KEY_BEST_DISTANCE = "bestDistance";
    public static final String KEY_NAME = "name";
    public static final String KEY_GROUP_PHOTO = "groupPhoto";

    public Group() {
    }

    public long getBestDistance() {
        return getLong(KEY_BEST_DISTANCE);
    }

    public void setBestDistance(long bestDistance) {
        this.put(KEY_BEST_DISTANCE, bestDistance);
    }

    public ParseUser getFounder() {
        return getParseUser(KEY_FOUNDER);
    }

    public void setFounder(ParseUser founder) {
        this.put(KEY_FOUNDER, founder);
    }

    public Date getTargetDate() {
        return (Date) get(KEY_TARGET_DATE);
    }

    public void setTargetDate(Date targetDate) {
        this.put(KEY_TARGET_DATE, targetDate);
    }

    public String getTargetDuration() {
        return getString(KEY_TARGET_DURATION);
    }

    public void setTargetDuration(String targetDuration) {
        this.put(KEY_TARGET_DURATION, targetDuration);
    }

    public long getTargetDistance() {
        return getLong(KEY_TARGET_DISTANCE);
    }

    public void setTargetDistance(long targetDistance) {
        this.put(KEY_TARGET_DISTANCE, targetDistance);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        this.put(KEY_NAME, name);
    }

    public ParseFile getGroupPhoto() {
        return getParseFile(KEY_GROUP_PHOTO);
    }

    public void setGroupPhoto(ParseFile groupPhoto) {
        this.put(KEY_GROUP_PHOTO, groupPhoto);
    }

    /****************** Methods *********************/

    public List<User> getAllAppleUsers() throws ParseException {
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.whereEqualTo(User.KEY_GROUP, Group.this);
        query.whereEqualTo(User.KEY_IS_IPHONE_USER, true);
        query.orderByAscending(User.KEY_NAME);
        return query.find();
    }

    public void getPicassoGroupPhoto(ImageView imageView, Context context, Callback callback) {
        try {
            Picasso picasso = Picasso.with(context);
            picasso.setLoggingEnabled(true);
            picasso.load(getGroupPhoto().getUrl())
                    .resize(imageView.getWidth(), imageView.getHeight())
                    .centerCrop().into(imageView, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveGroupPhoto(String photoFilePath, SaveCallback saveCallback) {
        setGroupPhoto(new ParseFile(new File(photoFilePath)));
        saveInBackground(saveCallback);
    }

    public static void setGroupPhotoToImageView(final Context context, Group group, final ImageView imageView){
        group.fetchIfNeededInBackground(new GetCallback<Group>() {
            @Override
            public void done(final Group fetchedGroup, ParseException e) {
                if (e == null) {
                    fetchedGroup.getGroupPhoto().getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            if (e == null) {
                                if (data.length > 0) {
                                    imageView.setImageBitmap(BitmapFactory
                                            .decodeByteArray(data, 0, data.length));
                                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

//                                    // Save photo Locally
//                                    PhotosManager.savePhotoLocallyInBackground(context, data,
//                                            fetchedGroup.getObjectId(),
//                                            PhotosManager.TYPE_GROUP_PHOTO,
//                                            new OnAsyncTaskFinishedListener() {
//                                                @Override
//                                                public void onSuccess(Object result) {
//
//                                                }
//
//                                                @Override
//                                                public void onError(String errorMessage) {
//                                                    Toast.makeText(context,
//                                                            "Failed to save group photo locally",
//                                                            Toast.LENGTH_LONG).show();
//                                                }
//                                            });
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    public static void saveCurrentUserGroupPhoto(final byte[] groupPhotoData,
                                                 final OnAsyncTaskFinishedListener listener) {
        new AsyncTask<Void,Void,Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    User currUser = (User) ParseUser.getCurrentUser();
                    currUser.fetch();
                    Group group = currUser.getGroup();
                    group.fetch();

                    String fileName = currUser.getObjectId();
                    ParseFile groupPhoto = new ParseFile(fileName, groupPhotoData, "jpg");
                    group.setGroupPhoto(groupPhoto);
                    group.save();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if(result){
                    listener.onSuccess(result);
                } else {
                    listener.onError("Error");
                }
            }
        }.execute();
    }
}
