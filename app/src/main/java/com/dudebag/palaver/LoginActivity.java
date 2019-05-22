package com.dudebag.palaver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

    EditText et_benutzername;
    EditText et_passwort;

    String benutzername;
    String passwort;

    String error1 = "Error Code: ";
    //String error2 = "Benutzer existiert bereits";
    String error3 = "Benutzername oder Passwort falsch";
    String error4 = "Passwort nicht korrekt";
    String error5 = "Benutzer existiert nicht";

    String msg1 = "Benutzer erfolgreich validiert";
    String msg2 = "Benutzername fehlt";
    String msg3 = "Passwort fehlt";
    String msg4 = "Benutzername und Passwort fehlen";

    JsonApi jsonApi;

    Post responsePost;

    TextView test;

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

        //test = findViewById(R.id.text_view_result);

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
                Post post = new Post("a", "b");
                post.setUsername(benutzername);
                post.setPassword(passwort);
                processLogin(post);

            }
        });




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
                    Toast.makeText(getApplicationContext(), msg1, Toast.LENGTH_LONG).show();
                    intent.putExtra(EXTRA_BENUTZERNAME, benutzername);
                    intent.putExtra(EXTRA_PASSWORT, passwort);
                    startActivity(intent);
                }
                //Passwort nicht korrekt
                else if (responsePost.getMsgType() == 0 && responsePost.getInfo() == error4){
                    Toast.makeText(getApplicationContext(), error4, Toast.LENGTH_LONG).show();
                    //Passwort-Feld wird bei falscher Eingabe gelöscht
                    et_passwort.setText("");
                    return;
                }
                //Benutzer existiert nicht
                else if (responsePost.getMsgType() == 0 && responsePost.getInfo() == error5){
                    Toast.makeText(getApplicationContext(), error5, Toast.LENGTH_LONG).show();
                    //Passwort-Feld wird bei falscher Eingabe gelöscht
                    et_passwort.setText("");
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
}
