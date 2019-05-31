package com.dudebag.palaver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_BENUTZERNAME = "com.dudebag.palaver.EXTRA_TEXT";
    public static final String EXTRA_PASSWORT = "com.dudebag.palaver.EXTRA_PASSWORT";
    public static final String EXTRA_USER = "com.dudebag.palaver.EXTRA_User";

    String benutzername;
    String passwort;

    JsonApi jsonApi;

    Post savedPost;
    Post responsePost;

    private RecyclerView mRecyclerView;
    private FriendAdapter mAdapter;
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
        Intent intent = new Intent(this.getApplicationContext(), AddFriendActivity.class);
        Intent intent2 = new Intent(this.getApplicationContext(), LoginActivity.class);
        Intent intent3 = new Intent(this.getApplicationContext(), DeleteFriendActivity.class);
        switch (item.getItemId()){
            case R.id.add_friend:
                intent.putExtra(EXTRA_BENUTZERNAME, benutzername);
                intent.putExtra(EXTRA_PASSWORT, passwort);
                startActivity(intent);
                return true;
            case R.id.delete_friend:
                intent3.putExtra(EXTRA_BENUTZERNAME, benutzername);
                intent3.putExtra(EXTRA_PASSWORT, passwort);
                startActivity(intent3);
                return true;
            case R.id.logout:
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        //Benutzername und Passwort wird in Empfang genommen
        Intent intent = getIntent();
        benutzername = intent.getStringExtra(LoginActivity.EXTRA_BENUTZERNAME);
        passwort = intent.getStringExtra(LoginActivity.EXTRA_PASSWORT);

        savedPost = new Post(benutzername, passwort);

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

                //Toast.makeText(getApplicationContext(), "CCCCCCCCCC", Toast.LENGTH_LONG).show();
                //Klick auf einen Freund zum Öffnen des Chatverlaufs
                mAdapter.setOnItemClickListener(new FriendAdapter.OnItemClickListener() {

                    @Override
                    public void onItemClick(int position) {
                        //friendList.get(position).changeText("HAHAHAHAHA");

                        //Toast.makeText(getApplicationContext(), "BBBBBBBBBB", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);

                        intent.putExtra(EXTRA_BENUTZERNAME, benutzername);
                        intent.putExtra(EXTRA_PASSWORT, passwort);
                        intent.putExtra(EXTRA_USER, friendList.get(position).getName());

                        //Toast.makeText(getApplicationContext(), "AAAAAAAAAAAA", Toast.LENGTH_LONG).show();

                        startActivity(intent);



                        //friendList.clear();
                        //getFriends(savedPost);
                    }
                });

                return;


            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}
