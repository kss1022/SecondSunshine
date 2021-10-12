package com.example.secondsunshine.Sync;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.secondsunshine.MainActivity;
import com.example.secondsunshine.R;

public class NotificationUtils {


    private static final String WEATHER_UPDATE_CHENNEL_ID = "chennel id";
    private static final int WEATHER_UPDATE_PENDING_INTENT_ID = 33;
    private static final int WEATEHR_UPDATE_NOTIFICATION_ID = 11;

    public static void NotificationWeather(Context context)
    {
        NotificationManager notificationManager = (NotificationManager)context.
                getSystemService(context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel   = new NotificationChannel(
                    WEATHER_UPDATE_CHENNEL_ID,
                    context.getString(R.string.notification_channel),
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, WEATHER_UPDATE_CHENNEL_ID)
                        .setColor(ContextCompat.getColor(context, R.color.white))
                        .setSmallIcon(R.drawable.ic_clear)
                        .setLargeIcon(largeIcon(context))
                        .setContentText(context.getString(R.string.update_weatehr_notification_title))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(
                                context.getString(R.string.update_weatehr_notification_body)))
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setContentIntent(contentIntent(context))
                        .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        notificationManager.notify(WEATEHR_UPDATE_NOTIFICATION_ID, notificationBuilder.build());
    }

    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, MainActivity.class);

        return PendingIntent.getActivity(
                context,
                WEATHER_UPDATE_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context) {
        Resources resources = context.getResources();

        Bitmap largeIcon = BitmapFactory.decodeResource(resources,
                R.drawable.ic_clear);

        return largeIcon;
    }

}
