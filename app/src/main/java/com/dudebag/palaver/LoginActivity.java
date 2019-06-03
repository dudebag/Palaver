package com.dudebag.palaver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private Button buttonLogin;

    public static final String EXTRA_BENUTZERNAME = "com.dudebag.palaver.EXTRA_TEXT";
    public static final String EXTRA_PASSWORT = "com.dudebag.palaver.EXTRA_PASSWORT";

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PALAVER_ID = "palaver_id";
    public static final String PALAVER_PW = "palaver_pw";
    public static final String LOGGED_IN = "logged_in";
    public static final String FROM_LOGIN = "from_login";

    boolean loggedIn;
    boolean fromLogin;

    EditText et_benutzername;
    EditText et_passwort;

    String benutzername;
    String passwort;

    final String error1 = "Error Code: ";
    //String error2 = "Benutzer existiert bereits";
    final String error3 = "Benutzername oder Passwort falsch";
    final String error4 = "Passwort nicht korrekt";
    final String error5 = "Benutzer existiert nicht";
    final String error6 = "Es besteht keine Internetverbindung";

    final String msg1 = "Benutzer erfolgreich validiert";
    final String msg2 = "Benutzername fehlt";
    final String msg3 = "Passwort fehlt";
    final String msg4 = "Benutzername und Passwort fehlen";

    JsonApi jsonApi;

    Post responsePost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Retrofit einrichten
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://palaver.se.paluno.uni-due.de/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Gson Konverter einrichten
        jsonApi = retrofit.create(JsonApi.class);


        //Eingabefelder zuweisen
        et_benutzername = findViewById(R.id.log_Benutzername);
        et_passwort = findViewById(R.id.log_Passwort);



        //Login Button einrichten
        buttonLogin = findViewById(R.id.log_Login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillForm();
                if (!validateForm()) {
                    return;
                }

                if (!checkInternet()) {
                    Toast.makeText(getApplicationContext(), error6, Toast.LENGTH_SHORT).show();

                    //Passwordfeld löschen
                    et_passwort.setText("");

                    //Wenn ein Cursor angezeigt wird dann im Passwortfeld
                    et_passwort.requestFocus();

                    return;
                }

                Post post = new Post(benutzername, passwort);

                //post.setUsername(benutzername);
                //post.setPassword(passwort);

                processLogin(post);

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

    private void processLogin(Post post) {

        final Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);

        Call<Post> call = jsonApi.processLogin(post);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if(!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), error1 + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                responsePost = response.body();

                //Benutzer erfolgreich validiert
                if (responsePost.getMsgType() == 1) {
                    //Toast.makeText(getApplicationContext(), msg1, Toast.LENGTH_LONG).show();

                    saveData();

                    //Benutzername und Passwort werden in die nächste Activity übergeben
                    intent.putExtra(EXTRA_BENUTZERNAME, benutzername);
                    intent.putExtra(EXTRA_PASSWORT, passwort);
                    startActivity(intent);
                }
                //Passwort nicht korrekt
                else if (responsePost.getMsgType() == 0 && responsePost.getInfo().equals(error4)){
                    Toast.makeText(getApplicationContext(), error4, Toast.LENGTH_LONG).show();
                    //Passwort-Feld wird bei falscher Eingabe gelöscht
                    et_passwort.setText("");
                    return;
                }
                //Benutzer existiert nicht
                else if (responsePost.getMsgType() == 0 && responsePost.getInfo().equals(error5)){
                    Toast.makeText(getApplicationContext(), error5, Toast.LENGTH_LONG).show();
                    //Passwort-Feld wird bei falscher Eingabe gelöscht
                    et_passwort.setText("");
                    return;
                }
                //Random Nachricht vom Server
                else {
                    Toast.makeText(getApplicationContext(), error1 + responsePost.getInfo(), Toast.LENGTH_LONG).show();
                    return;
                }

                return;

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
        });



    }

    public void fillForm(){
        benutzername = et_benutzername.getText().toString().trim();
        passwort = et_passwort.getText().toString().trim();
    }

    public boolean validateForm(){
        if (benutzername.isEmpty() && !passwort.isEmpty()) {
            Toast.makeText(getApplicationContext(), msg2, Toast.LENGTH_LONG).show();
            return false;
        }
        else if (!benutzername.isEmpty() && passwort.isEmpty()) {
            Toast.makeText(getApplicationContext(), msg3, Toast.LENGTH_LONG).show();
            return false;
        }
        else if (benutzername.isEmpty() && passwort.isEmpty()) {
            Toast.makeText(getApplicationContext(), msg4, Toast.LENGTH_LONG).show();
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



    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(PALAVER_ID, benutzername);
        editor.putString(PALAVER_PW, passwort);
        editor.putBoolean(LOGGED_IN, true);
        editor.putBoolean(FROM_LOGIN, true);

        editor.apply();
    }


    private boolean checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onBackPressed() {

        //öffne Homescreen wenn Zurück-Taste gedrückt wird
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
        finish();
    }
}

