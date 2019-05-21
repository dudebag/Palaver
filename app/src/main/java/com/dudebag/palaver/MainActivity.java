package com.dudebag.palaver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    protected String benutzername;
    protected String passwort;

    JsonApi jsonApi;

    Post responsePost;

    TextView textViewResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        benutzername = intent.getStringExtra(LoginActivity.EXTRA_BENUTZERNAME);
        passwort = intent.getStringExtra(LoginActivity.EXTRA_PASSWORT);

        //Retrofit einrichten
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://palaver.se.paluno.uni-due.de/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Gson Konverter einrichten
        jsonApi = retrofit.create(JsonApi.class);

        textViewResult = findViewById(R.id.text_view_result);

        Post post = new Post(benutzername, passwort);
        //Post post = new Post(benutzername, passwort, "bello");

        getFriends(post);
        //addFriends(post);

    }


    private void getMessages(Post post){
        Call<Post> call = jsonApi.getMessages(post);
        //2--Toast.makeText(getApplicationContext(), "rip1", Toast.LENGTH_LONG).show();

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if (!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "rip2", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), "rip3", Toast.LENGTH_LONG).show();
                responsePost = response.body();

                String text = "";
                text += "Code: " + response.code() + "\n";
                text += "MsgType: " + responsePost.getMsgType() + "\n";
                text += "Info: " + responsePost.getInfo() + "\n";
                text += "Data: " + "\n";

                String[] friends;
                        //= new String[responsePost.getData().length];
                friends = responsePost.getData();

                for (int i=0; i< friends.length; i++) {
                    text += friends[i] + "\n";
                }


                textViewResult.setText(text);
                return;

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "ripTODES", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getFriends(Post post){
        Call<Post> call = jsonApi.getFriends(post);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Fehler aufgetaucht", Toast.LENGTH_LONG).show();
                    return;
                }

                responsePost = response.body();

                String text = "";
                text += "Code: " + response.code() + "\n";
                text += "MsgType: " + responsePost.getMsgType() + "\n";
                text += "Info: " + responsePost.getInfo() + "\n";
                text += "Data: " + responsePost.getData() + "\n\n";

                textViewResult.setText(text);
                return;
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

                textViewResult.setText(t.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addFriends(Post post) {
        Call<Post> call = jsonApi.addFriends(post);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Fehler aufgetaucht", Toast.LENGTH_LONG).show();
                    return;
                }

                responsePost = response.body();

                String text = "";
                text += "Code: " + response.code() + "\n";
                text += "MsgType: " + responsePost.getMsgType() + "\n";
                text += "Info: " + responsePost.getInfo() + "\n";
                text += "Data: " + responsePost.getData() + "\n\n";

                textViewResult.setText(text);
                return;

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

                textViewResult.setText(t.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
        });
    }
}
