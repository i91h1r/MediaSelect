package com.github.hyr0318.mediaselect_library.utils;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class U {

    public static final String DATA_DIRECTORY = PickerConstants.CRP_AUDIO;

    public static void createDirectory() {
        if (sdCardExists()) {
            File file = new File(DATA_DIRECTORY);
            if (!file.exists()) {
                file.mkdirs();
            }
        } else {
            Log.e("U", "无sd卡...");
        }
    }

    public static boolean sdCardExists() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    @SuppressLint("SimpleDateFormat")
    public static String millis2CalendarString(long millis, String format) {
        if (millis > 0) {
            SimpleDateFormat yFormat = new SimpleDateFormat(format);
            Calendar yCalendar = Calendar.getInstance();
            yCalendar.setTimeInMillis(millis);

            try {
                return yFormat.format(yCalendar.getTime());
            } catch (Exception e) {

            } finally {
                yCalendar = null;
                yFormat = null;
                format = null;
            }
        }

        return "";
    }


}
