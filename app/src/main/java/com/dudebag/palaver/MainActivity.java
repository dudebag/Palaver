package com.dudebag.palaver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_BENUTZERNAME = "com.dudebag.palaver.EXTRA_TEXT";
    public static final String EXTRA_PASSWORT = "com.dudebag.palaver.EXTRA_PASSWORT";

    String benutzername;
    String passwort;

    JsonApi jsonApi;

    Post savedPost;
    Post responsePost;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<Friend> friendList;

    String error1 = "Freund dem System nicht bekannt";
    String error2 = "Freund bereits auf der Liste";

    String msg1 = "Freund hinzugefügt";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Benutzername und Passwort wird in Empfang genommen
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

        friendList = new ArrayList<>();

        Post post = new Post(benutzername, passwort);

        savedPost = post;

        getFriends(post);

    }

    //oben rechts optionsmenü mit benutzer hinzufügen
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    //oben rechts optionsmenü mit benutzer hinzufügen
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this.getApplicationContext(), AddFriendsActivity.class);
        switch (item.getItemId()){
            case R.id.button1:
                intent.putExtra(EXTRA_BENUTZERNAME, benutzername);
                intent.putExtra(EXTRA_PASSWORT, passwort);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        //Freundesliste zurücksetzen und aktualisieren
        friendList.clear();
        getFriends(savedPost);

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

                for (int i = 0; i < responsePost.getData().size(); i++) {
                    friendList.add(new Friend(responsePost.getDataDetail(i)));
                }

                mRecyclerView = findViewById(R.id.recyclerView);
                //setHasFixedSize auf true wenn recyclerview immer gleich groß weil performance
                mRecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mAdapter = new FriendAdapter(friendList);

                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);

                return;


            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
                //friends = responsePost.getData();

                /*for (int i=0; i< friends.length; i++) {
                    text += friends[i] + "\n";
                }*/


                //textViewResult.setText(text);
                return;

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "ripTODES", Toast.LENGTH_LONG).show();
            }
        });
    }
}
