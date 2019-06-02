package com.dudebag.palaver;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class bootClass extends Application {

    public static final String CHANNEL_ID = "channel_id";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        //API 26 oder höher (Android 8)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription("Hier werden Nachrichten angezeigt");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
