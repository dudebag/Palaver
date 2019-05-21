package com.dudebag.palaver;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
