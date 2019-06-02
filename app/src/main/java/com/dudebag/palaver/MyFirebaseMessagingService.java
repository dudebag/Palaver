package com.dudebag.palaver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //Wenn Antwort nicht leer
        if (remoteMessage.getData().size() > 0) {

        }
    }


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        //sendRegistrationToServer(s);
    }


}
