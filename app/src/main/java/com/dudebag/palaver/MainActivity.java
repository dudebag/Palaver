package com.dudebag.palaver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView textViewResult;

    JsonApi jsonApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Timer für Splash-Screen
        /*new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        }, 4000);*/




        textViewResult = findViewById(R.id.text_view_result);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://palaver.se.paluno.uni-due.de")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonApi = retrofit.create(JsonApi.class);

        createPost();

        /* DAS WAR FÜR GET-CALL
        Call<List<Post>> call = jsonApi.getPosts();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {

                if (!response.isSuccessful()) {
                    textViewResult.setText("Code: " + response.code());
                    return;
                }

                List<Post> posts = response.body();

                for (Post post : posts) {
                    String content = "";
                    content += "Username: " + post.getUsername() + "\n";
                    content += "Password: " + post.getPassword() + "\n\n";

                    textViewResult.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
        */
    }

    private void createPost() {
        // die Werte hier sollten vom User-Input eingefügt werden, das senden wir zum Server
        Post post = new Post("zufgjhjg", "lololol");

        Call<Post> call = jsonApi.createPost(post);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if (!response.isSuccessful()) {
                    textViewResult.setText("Code: " + response.code());
                    return;
                }

                Post postResponse = response.body();

                String content = "";
                content += "Code: " + response.code() + "\n";
                content += "MsgType: " + postResponse.getMsgType() + "\n";
                content += "Info: " + postResponse.getInfo() + "\n";
                content += "Data: " + postResponse.getData() + "\n\n";

                textViewResult.setText(content);
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }
}
