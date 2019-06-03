package com.dudebag.palaver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddFriendActivity extends AppCompatActivity {



    private Button button;

    JsonApi jsonApi;

    Post responsePost;

    EditText et_nameInput;

    String benutzername;
    String passwort;

    String nameInput;

    final String msg1 = "Freund hinzugefügt";

    final String error1 = "Error Code: ";
    final String error2 = "Freund dem System nicht bekannt";
    final String error3 = "Freund bereits auf der Liste";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        //Retrofit einrichten
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://palaver.se.paluno.uni-due.de/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Gson Konverter einrichten
        jsonApi = retrofit.create(JsonApi.class);

        //Benutzername und Passwort wird in Empfang genommen
        Intent intent = getIntent();
        benutzername = intent.getStringExtra(MainActivity.EXTRA_BENUTZERNAME);
        passwort = intent.getStringExtra(MainActivity.EXTRA_PASSWORT);

        button = findViewById(R.id.addFriend);
        et_nameInput = findViewById(R.id.friendName);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardButton();
                fillForm();
                if (!validateForm()) {
                    return;
                }
                else {
                    Post post = new Post(benutzername, passwort, nameInput);
                    addFriends(post);
                    //Toast.makeText(getApplicationContext(), error2, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void addFriends(Post post) {
        Call<Post> call = jsonApi.addFriends(post);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Fehler aufgetaucht", Toast.LENGTH_SHORT).show();
                    return;
                }

                else {

                    responsePost = response.body();

                    //Feld nach jedem Klick löschen
                    et_nameInput.setText("");

                    //Freund hinzugefügt
                    if (responsePost.getMsgType() == 1) {
                        Toast.makeText(getApplicationContext(), msg1, Toast.LENGTH_LONG).show();
                        return;
                    }
                    //Freund dem System nicht bekannt
                    else if (responsePost.getMsgType() == 0 && responsePost.getInfo().equals(error2)) {
                        Toast.makeText(getApplicationContext(), error2, Toast.LENGTH_LONG).show();
                        return;
                    }
                    //Freund bereits auf der Liste
                    else if (responsePost.getMsgType() == 0 && responsePost.getInfo().equals(error3)) {
                        Toast.makeText(getApplicationContext(), error3, Toast.LENGTH_LONG).show();
                        return;
                    }
                    //Random Nachricht vom Server
                    else {
                        Toast.makeText(getApplicationContext(), error1 + responsePost.getInfo(), Toast.LENGTH_LONG).show();
                        return;
                    }

                }


            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

                //textViewResult.setText(t.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
        });
    }

    public void fillForm() {
         nameInput = et_nameInput.getText().toString().trim();
    }

    public boolean validateForm() {
        if (nameInput.isEmpty()) {
            return false;
        }
        else {
            return true;
        }
    }



    //Methode zum Frame Layout damit Tastatur verschwindet
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //Tastatur bei Buttonklick schließen
    public void hideKeyboardButton() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
