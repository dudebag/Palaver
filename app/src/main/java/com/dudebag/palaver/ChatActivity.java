package com.dudebag.palaver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {

    String benutzername;
    String passwort;
    String user;

    JsonApi jsonApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Benutzername und Passwort wird in Empfang genommen
        Intent intent = getIntent();
        benutzername = intent.getStringExtra(MainActivity.EXTRA_BENUTZERNAME);
        passwort = intent.getStringExtra(MainActivity.EXTRA_PASSWORT);
        user = intent.getStringExtra(MainActivity.EXTRA_USER);

        //Retrofit einrichten
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://palaver.se.paluno.uni-due.de/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Gson Konverter einrichten
        jsonApi = retrofit.create(JsonApi.class);


    }

    //oben rechts optionsmenü mit benutzer hinzufügen
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }
}
