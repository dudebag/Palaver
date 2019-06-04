package com.dudebag.palaver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
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

public class ChangePasswordActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PALAVER_ID = "palaver_id";
    public static final String PALAVER_PW = "palaver_pw";

    JsonApi jsonApi;

    PostChange responsePost;

    Button button;

    EditText et_id;
    EditText et_pw;
    EditText et_new_pw;

    String id;
    String pw;
    String newPw;

    String benutzername;
    String passwort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);


        //Retrofit einrichten
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://palaver.se.paluno.uni-due.de/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Gson Konverter einrichten
        jsonApi = retrofit.create(JsonApi.class);


        button = findViewById(R.id.change_password_btn);

        et_id = findViewById(R.id.change_password_id);
        et_pw = findViewById(R.id.change_password_pw);
        et_new_pw = findViewById(R.id.change_password_new_pw);

        loadData();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboardButton();
                fillForm();

                if (!validateForm()) {
                    et_pw.setText("");
                    et_new_pw.setText("");
                    return;
                }

                else {
                    et_id.setText("");
                    et_pw.setText("");
                    et_new_pw.setText("");

                    PostChange postChange = new PostChange(id, pw, newPw);

                    changePassword(postChange);
                    return;
                }
            }
        });


    }


    private void changePassword(PostChange postChange) {
        Call<PostChange> call = jsonApi.changePassword(postChange);

        call.enqueue(new Callback<PostChange>() {
            @Override
            public void onResponse(Call<PostChange> call, Response<PostChange> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error) + response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }

                else {
                    responsePost = response.body();

                    if (responsePost.getMsgType() == 1) {
                        saveData();
                        loadData();
                        Toast.makeText(getApplicationContext(), R.string.msg5, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    else {
                        Toast.makeText(getApplicationContext(), getString(R.string.error) + responsePost.getInfo(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

            }

            @Override
            public void onFailure(Call<PostChange> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getString(R.string.error) + t.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        });


    }



    //Tastatur bei Buttonklick schließen
    public void hideKeyboardButton() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //Methode zum Frame Layout damit Tastatur verschwindet
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void fillForm() {
        id = et_id.getText().toString().trim();
        pw = et_pw.getText().toString().trim();
        newPw = et_new_pw.getText().toString().trim();
    }

    public boolean validateForm() {
        if (id.isEmpty() || pw.isEmpty() || newPw.isEmpty()) {
            Toast.makeText(this, R.string.error11, Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!id.equals(benutzername)) {
            Toast.makeText(this, R.string.error12, Toast.LENGTH_SHORT).show();
            et_id.setText("");
            et_id.requestFocus();
            return false;
        }
        else if (!pw.equals(passwort)) {
            Toast.makeText(this, R.string.error6, Toast.LENGTH_SHORT).show();
            et_pw.requestFocus();
            return false;
        }
        else if (newPw.equals(passwort)) {
            Toast.makeText(this, R.string.error13, Toast.LENGTH_SHORT).show();
            et_pw.requestFocus();
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

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(PALAVER_PW, newPw);
        passwort = newPw;

        editor.apply();
    }


}
