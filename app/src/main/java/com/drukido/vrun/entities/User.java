package com.drukido.vrun.entities;

import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.drukido.vrun.R;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

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
                                    imageView.setImageResource(R.drawable.apple);
                                }
                            }
                        }
                    });
                } else {
                    try {
                        if (user.getIsIphoneUser()) {
                            imageView.setImageResource(R.drawable.apple);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
    }
}
