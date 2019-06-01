package com.dudebag.palaver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    //Variablen um Cursor aus Eingabefeldern zu nehmen
    private Context mContext;
    private Activity mActivity;
    private RelativeLayout mCLayout;
    private EditText mEditText;

    private Button buttonRegister;
    private Button buttonLogin;

    EditText et_benutzername;
    EditText et_passwort;

    String benutzername;
    String passwort;

    String error1 = "Error Code: ";
    String error2 = "Benutzer existiert bereits";
    String error3 = "Es besteht keine Internetverbindung";

    String msg2 = "Benutzername fehlt";
    String msg3 = "Passwort fehlt";
    String msg4 = "Benutzername und Passwort fehlen";
    String msg5 = "Benutzer erfolgreich registriert";

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
                    Toast.makeText(getApplicationContext(), error3, Toast.LENGTH_SHORT).show();

                    //Passwordfeld löschen
                    et_passwort.setText("");

                    //Wenn ein Cursor angezeigt wird dann im Passwortfeld
                    et_passwort.requestFocus();

                    return;
                }

                Post post = new Post(benutzername, passwort);

                //post.setUsername(benutzername);
                //post.setPassword(passwort);

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
                if(!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), error1 + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                responsePost = response.body();

                //Benutzer erfolgreich angelegt
                if (responsePost.getMsgType() == 1) {
                    Toast.makeText(getApplicationContext(), msg5, Toast.LENGTH_LONG).show();
                    startActivity(intent);
                    return;
                }
                //Benutzer existiert bereits
                else if (responsePost.getMsgType() == 0) {
                    Toast.makeText(getApplicationContext(), error2, Toast.LENGTH_LONG).show();
                    return;
                }
                //Random Nachricht vom Server
                else {
                    Toast.makeText(getApplicationContext(), error1 + responsePost.getInfo(), Toast.LENGTH_LONG).show();
                    return;
                }

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
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


    private boolean checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
