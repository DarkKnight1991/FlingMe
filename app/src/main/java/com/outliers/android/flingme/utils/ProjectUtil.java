package com.outliers.android.flingme.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import com.outliers.android.flingme.constants.AppConstants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

/**
 * Created by outliersasu on 11/14/17.
 */

public class ProjectUtil {

    public static int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static int pxToDp(int px, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static int getRandomInt(int min, int max){
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    public static String formatDateTime(long time, String format){
        try {
            SimpleDateFormat simpleDateFormat;
            if (format == null) {
                simpleDateFormat = new SimpleDateFormat(AppConstants.DATE_TIME_FORMAT);
            } else {
                simpleDateFormat = new SimpleDateFormat(format);
            }
            return simpleDateFormat.format(new Date(time));
        }catch (Exception ex){
            return "";
        }
    }

}
