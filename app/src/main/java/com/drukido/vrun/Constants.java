package com.drukido.vrun;

import android.*;
import android.content.Context;

public class Constants {
    public static final String PREF_IS_USER_LOGGED_IN = "is_user_logged_in";
    public static final String VRUN_PREFS_NAME = "VRunPrefs";
    public static final String VRUN_GROUP_OBJECT_ID = "hM5byuhi4e";
    public static final String EMPTY_STRING = "";
    public static final String EXTRA_RUN_ID = "run_id";
    public static final String DATE_HELPER_FORMAT = "dd-MM-yyyy HH:mm";
    public static final String KEY_GROUP = "group";
    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String LOG_TAG = "VRun";
    public static final String KEY_NAME = "name";
    public static final String KEY_IS_IPHONE_USER = "isIphoneUser";
    public static final int NOTIFICATION_ID = 100;

    public static final int[] SWIPE_LAYOUT_COLORS =
            {R.color.colorGreenSuccess,
            R.color.colorPrimary,
            R.color.colorRedFailed};

    public static int[] getSwipeLayoutColors(Context context) {
        int[] colors = {context.getResources().getColor(R.color.colorGreenSuccess),
                context.getResources().getColor(R.color.colorPrimary),
                context.getResources().getColor(R.color.colorRedFailed)};
        return colors;
    }
}
