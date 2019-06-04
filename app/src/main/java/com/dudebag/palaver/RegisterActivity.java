package com.dudebag.palaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private Button buttonRegister;
    private Button buttonLogin;



    EditText et_benutzername;
    EditText et_passwort;

    String benutzername;
    String passwort;

    JsonApi jsonApi;

    Post responsePost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Retrofit einrichten
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://palaver.se.paluno.uni-due.de/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Gson Konverter einrichten
        jsonApi = retrofit.create(JsonApi.class);

        //Eingabefelder zuweisen
        et_benutzername = findViewById(R.id.reg_Benutzername);
        et_passwort = findViewById(R.id.reg_Passwort);


        //Registrieren Button einrichten
        buttonRegister = findViewById(R.id.reg_Register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fillForm();

                if (!validateForm()) {
                    return;
                }

                if (!checkInternet()) {
                    Toast.makeText(getApplicationContext(), R.string.error1, Toast.LENGTH_SHORT).show();

                    //Passwordfeld löschen
                    et_passwort.setText("");

                    //Wenn ein Cursor angezeigt wird dann im Passwortfeld
                    et_passwort.requestFocus();

                    return;
                }

                Post post = new Post(benutzername, passwort);

                processRegistration(post);

            }
        });


        //Zum Login Button einrichten
        buttonLogin = findViewById(R.id.reg_Login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });


    }


    @Override
    protected void onRestart() {
        super.onRestart();

        //Benutzername- und Passwordfeld löschen
        et_benutzername.setText("");
        et_passwort.setText("");

        //Wenn ein Cursor angezeigt wird dann im Benutzernamefeld
        et_benutzername.requestFocus();


    }

    private void processRegistration(Post post) {

        final Intent intent = new Intent(this.getApplicationContext(), LoginActivity.class);

        Call<Post> call = jsonApi.processRegistration(post);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                //wenn nicht successfull
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error) + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                responsePost = response.body();

                //Benutzer erfolgreich angelegt
                if (responsePost.getMsgType() == 1) {
                    Toast.makeText(getApplicationContext(), R.string.msg1, Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    return;
                }
                //Benutzer existiert bereits
                else if (responsePost.getMsgType() == 0) {
                    Toast.makeText(getApplicationContext(), R.string.error2, Toast.LENGTH_SHORT).show();
                    return;
                }
                //Random Nachricht vom Server
                else {
                    Toast.makeText(getApplicationContext(), R.string.error + responsePost.getInfo(), Toast.LENGTH_SHORT).show();
                    return;
                }

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getString(R.string.error) + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void fillForm() {
        benutzername = et_benutzername.getText().toString().trim();
        passwort = et_passwort.getText().toString().trim();
    }

    public boolean validateForm() {
        if (benutzername.isEmpty() && !passwort.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.error3, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!benutzername.isEmpty() && passwort.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.error4, Toast.LENGTH_SHORT).show();
            return false;
        } else if (benutzername.isEmpty() && passwort.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.error5, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    //Methode zum Frame Layout damit Tastatur verschwindet
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private boolean checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
