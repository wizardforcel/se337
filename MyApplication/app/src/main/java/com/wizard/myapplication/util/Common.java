package com.wizard.myapplication.util;

import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by asus on 2015/8/15.
 */
public class Common {

    public static String dateFormat(String date)
    {
        long dateInt = Long.parseLong(date);
        int sec = (int) (dateInt % 100);
        dateInt /= 100;
        int min = (int) (dateInt % 100);
        dateInt /= 100;
        int hr = (int) (dateInt % 100);
        dateInt /= 100;
        int day = (int) (dateInt % 100);
        dateInt /= 100;
        int mon = (int) (dateInt % 100);
        int year = (int) (dateInt / 100);
        return String.format("%d-%d-%d %02d:%02d:%02d", year, mon, day, hr, min, sec);
    }

    public static String calToDateStr(Calendar c)
    {
        return String.format("%d-%d-%d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DATE));
    }

    public static String calToTimeStr(Calendar c)
    {
        return String.format("%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
    }

    public static String calToDateNum(Calendar c)
    {
        return String.format("%d%02d%02d%02d%02d00", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DATE),
                                                     c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
    }

    public static void calToDatePicker(Calendar c, DatePicker dp)
    {
        dp.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), null);
    }

    public static void calToTimePicker(Calendar c, TimePicker tp)
    {
        tp.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
        tp.setCurrentMinute(c.get(Calendar.MINUTE));
    }

}

