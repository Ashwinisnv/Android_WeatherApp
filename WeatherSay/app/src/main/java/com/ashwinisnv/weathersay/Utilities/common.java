package com.ashwinisnv.weathersay.Utilities;

/**
 * Created by ashwinivishwas on 4/7/18.
 */

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class common {
    public static String API_KEY = "<API_KEY>";
    public static String API = "http://api.openweathermap.org/data/2.5/weather";

    @NonNull
    public static String getAPI(String lat, String lng) {
        StringBuffer sb = new StringBuffer(API);
        sb.append(String.format("?lat=%s&lon=%s&appid=%s",lat,lng,API_KEY));
        return sb.toString();
    }

    public static String timeStampToDateTime(double timeStamp)
    {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setTime((long)timeStamp*1000);
        return df.format(date);
    }

    public static String getImage(String icon)
    {
        return String.format("http://openweathermap.org/img/w/%s.png",icon);
    }

    public static String getTime()
    {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        Date date = Calendar.getInstance(TimeZone.getDefault()).getTime();
        return df.format(date);
    }
}
