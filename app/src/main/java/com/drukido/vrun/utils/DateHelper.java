package com.drukido.vrun.utils;

import com.drukido.vrun.Constants;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ido on 11/8/2015.
 */
public class DateHelper {
    public static Date stringToDate(String string) throws Exception{
        if (string != null) {
            DateFormat format = new SimpleDateFormat(Constants.DATE_HELPER_FORMAT, Locale.ENGLISH);
            return format.parse(string);
        } else {
            return null;
        }
    }

    public static String dateToString(@NotNull Date date) {
        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_HELPER_FORMAT);
            return dateFormat.format(date);
        } else return "";
    }
}
