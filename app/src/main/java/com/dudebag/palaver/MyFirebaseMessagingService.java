package com.dudebag.palaver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationBuilderWithBuilderAccessor;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PUSHTOKEN = "pushtoken";
    public static final String CHANNEL_ID = "channel_id";



    public MyFirebaseMessagingService() {


    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //Wenn Antwort nicht leer
        if (remoteMessage.getData().size() > 0) {


            if (remoteMessage != null) {
                generateNotification("Du hast eine neue Nachricht von " + remoteMessage.getData().get("sender") +  " erhalten", "Palaver");
            }

            else {
                Log.d(TAG, "PENISPUMPE");
            }


        }
    }

    private void generateNotification(String body, String title) {


            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);

            //Onclick für notification
            Intent resultIntent = new Intent(this, MainActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.palaver_logo)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent);     //onclick für notification


            notificationManager.notify(new Random().nextInt(), notificationBuilder.build());

        }





    @Override
    public void onNewToken(String s) {
        //super.onNewToken(s);

        saveToken(s);
        Log.d(TAG, "TOKEN SAVED");
    }





    public void saveToken(String s) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(PUSHTOKEN, s);

        editor.apply();
    }








}
