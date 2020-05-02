package com.example.testwebscrape.Notifications;

import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.testwebscrape.MainActivity;
import com.example.testwebscrape.R;

public class NotificationHelper {

    public static void displayNotification(Context context, String title, String body) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat mNotificationMgr = NotificationManagerCompat.from(context);
        mNotificationMgr.notify(1, mBuilder.build());

    }

}