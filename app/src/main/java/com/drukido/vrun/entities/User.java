package com.drukido.vrun.entities;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.drukido.vrun.R;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("_User")
public class User extends ParseUser{

    public static final String KEY_PHONE_NUMBER = "phoneNumber";
    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_NAME = "name";
    public static final String KEY_GROUP = "group";
    public static final String KEY_PROFILE_PHOTO = "profilePicture";
    public static final String KEY_IS_IPHONE_USER = "isIphoneUser";

    public Boolean getIsIphoneUser() {
        return getBoolean(KEY_IS_IPHONE_USER);
    }

    public void setIsIphoneUser(Boolean isIphoneUser) {
        this.put(KEY_IS_IPHONE_USER, isIphoneUser);
    }

    public ParseFile getProfilePhoto() {
        return getParseFile(KEY_PROFILE_PHOTO);
    }

    public void setProfilePhoto(ParseFile profilePhoto) {
        this.put(KEY_PROFILE_PHOTO, profilePhoto);
    }

    public Group getGroup() {
        return (Group) getParseObject(KEY_GROUP);
    }

    public void setGroup(Group group) {
        this.put(KEY_GROUP, group);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        this.put(KEY_NAME, name);
    }

    public String getLastName() {
        return getString(KEY_LAST_NAME);
    }

    public void setLastName(String lastName) {
        this.put(KEY_LAST_NAME, lastName);
    }

    public String getFirstName() {
        return getString(KEY_FIRST_NAME);
    }

    public void setFirstName(String firstName) {
        this.put(KEY_FIRST_NAME, firstName);
    }

    public String getPhoneNumber() {
        return getString(KEY_PHONE_NUMBER);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.put(KEY_PHONE_NUMBER, phoneNumber);
    }

    /********************* Methods *******************/
    public void getPicassoProfilePhoto(ImageView imageView, Context context) {
        if (getProfilePhoto() != null) {
            Picasso.with(context).load(getProfilePhoto().getUrl())
                    .placeholder(R.drawable.user_temp)
                    .error(R.drawable.user_temp).into(imageView);
        } else {
            imageView.setPadding(10,10,10,10);
        }
    }

    public void getPicassoProfilePhotoWithCallBack(ImageView imageView, Context context,
                                                   Callback callback) {
        if (getProfilePhoto() != null) {
            Picasso.with(context).load(getProfilePhoto().getUrl())
                    .placeholder(R.drawable.user_temp)
                    .error(R.drawable.user_temp).into(imageView, callback);
        } else {
            imageView.setPadding(10,10,10,10);
        }
    }

    public void saveProfilePhoto(String photoFilePath, SaveCallback saveCallback) {
        setProfilePhoto(new ParseFile(new File(photoFilePath)));
        saveInBackground(saveCallback);
    }

    public void getAllUsers(boolean isOnlyIphone, FindCallback<User> callback) {
        try {
            ParseQuery<User> query = ParseQuery.getQuery(User.class);
            query.whereEqualTo(KEY_GROUP, getGroup());

            if(isOnlyIphone) {
                query.whereEqualTo(KEY_IS_IPHONE_USER, true);
            }

            query.orderByAscending(KEY_NAME);
            query.findInBackground(callback);
        } catch (Exception e) {
            e.printStackTrace();
            callback.done(null, new ParseException(0,""));
        }
    }


    public static void setUserProfilePhoto(final User user, final ImageView imageView) {
        user.fetchIfNeededInBackground(new GetCallback<User>() {
            @Override
            public void done(final User fetchedUser, ParseException e) {
                if (e == null) {
                    fetchedUser.getProfilePhoto().getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            if (e == null && data.length > 0) {
                                imageView.setImageBitmap(BitmapFactory
                                        .decodeByteArray(data, 0, data.length));
                            }
                            else {
                                if (fetchedUser.getIsIphoneUser()) {
                                    imageView.setImageResource(R.drawable.steve_jobs);
                                }
                            }
                        }
                    });
                } else {
                    try {
                        if (user.getIsIphoneUser()) {
                            imageView.setImageResource(R.drawable.steve_jobs);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
    }

    public void getPastAttendingRuns(FindCallback<Run> callBack) {
        ArrayList<User> userCollection = new ArrayList<>();
        userCollection.add(this);
        ParseQuery<Run> query = Run.getQuery();
        query.whereEqualTo(Run.KEY_GROUP, this.getGroup());
        query.whereLessThanOrEqualTo(Run.KEY_RUN_TIME, new Date());
        query.whereContainsAll(Run.KEY_ATTENDING, userCollection);
        query.orderByAscending(Run.KEY_RUN_TIME);
        query.findInBackground(callBack);
    }
}
