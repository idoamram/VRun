package com.drukido.vrun.utils;

import com.drukido.vrun.Constants;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ido on 11/8/2015.
 */
public class DateHelper {
    public static Date stringToDate(String string){
        if (string != null) {
            DateFormat format = new SimpleDateFormat(Constants.DATE_HELPER_FORMAT, Locale.ENGLISH);
            try {
                return format.parse(string);
            } catch (ParseException e) {
                return null;
            }
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

    public static String getTimeStringFromDate(Date date){
        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_HELPER_FORMAT);
            String fullDateTime[] = dateFormat.format(date).split(" ");
            return fullDateTime[1];
        } else return "";
    }

    public static String getDateStringFromDate(Date date){
        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_HELPER_FORMAT);
            String fullDateTime[] = dateFormat.format(date).split(" ");
            return fullDateTime[0];
        } else return "";
    }
}
