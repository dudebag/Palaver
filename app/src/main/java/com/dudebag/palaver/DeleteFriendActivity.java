package com.dudebag.palaver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class DeleteFriendActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PALAVER_ID = "palaver_id";
    public static final String PALAVER_PW = "palaver_pw";

    private Button button;

    JsonApi jsonApi;

    Post responsePost;

    EditText et_nameInput;

    String benutzername;
    String passwort;


    String nameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_friend);

        //Retrofit einrichten
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://palaver.se.paluno.uni-due.de/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Gson Konverter einrichten
        jsonApi = retrofit.create(JsonApi.class);

        loadData();

        button = findViewById(R.id.delete_friend_btn);
        et_nameInput = findViewById(R.id.friend_name_delete);

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
                    deleteFriends(post);
                }
            }
        });

    }


    private void deleteFriends(Post post) {
        Call<Post> call = jsonApi.deleteFriends(post);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error) + response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }

                responsePost = response.body();

                //Feld nach jedem Klick löschen
                et_nameInput.setText("");

                //Freund entfernen
                if (responsePost.getMsgType() == 1) {
                    Toast.makeText(getApplicationContext(), R.string.msg4, Toast.LENGTH_SHORT).show();
                    return;
                }
                //Freund nicht auf der Liste
                else if (responsePost.getMsgType() == 0 && responsePost.getInfo().equals(getString(R.string.error10))) {
                    Toast.makeText(getApplicationContext(), R.string.error10, Toast.LENGTH_SHORT).show();
                    return;
                }
                //Freund dem System nicht bekannt
                else if (responsePost.getMsgType() == 0 && responsePost.getInfo().equals(getString(R.string.error8))) {
                    Toast.makeText(getApplicationContext(), R.string.error8, Toast.LENGTH_SHORT).show();
                    return;
                }
                //Random Nachricht vom Server
                else {
                    Toast.makeText(getApplicationContext(), getString(R.string.error) + responsePost.getInfo(), Toast.LENGTH_SHORT).show();
                    return;
                }

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getString(R.string.error) + t.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        });
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


    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        benutzername = sharedPreferences.getString(PALAVER_ID, "");
        passwort = sharedPreferences.getString(PALAVER_PW, "");

    }

}


