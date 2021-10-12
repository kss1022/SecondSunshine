package com.example.secondsunshine.Utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {
    private static final String DATE_FORMAT = "yyyy-MM-dd  hh";



    //UTC 타임을 Local 타임으로 변경
    public static String convertUtcToLocal(long utcTime) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String strUtctime = sdf.format(utcTime * 1000 );

        return strUtctime;
    }


}
