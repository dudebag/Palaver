package com.dudebag.palaver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreenActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String LOGGED_IN = "logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        if (sharedPreferences.getBoolean(LOGGED_IN, false) == true) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return;
        }


        EasySplashScreen config = new EasySplashScreen(SplashScreenActivity.this)
                .withFullScreen()
                .withTargetActivity(RegisterActivity.class)
                .withSplashTimeOut(1000)
                .withBackgroundColor(Color.parseColor("#cce6ff"))
                .withBackgroundResource(R.drawable.bg_login)
                .withLogo(R.mipmap.palaver_logo)
                ;

        View easySplashScreen = config.create();
        setContentView(easySplashScreen);
    }
}
