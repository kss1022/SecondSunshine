package com.example.secondsunshine.Data;

import androidx.room.TypeConverter;

import java.util.Date;

public class DataConverter {
    @TypeConverter
    public static Date toDate(Long timeStamp)
    {
        return timeStamp == null ? null : new Date(timeStamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date)
    {
        return date == null ? null : date.getTime();
    }


}
