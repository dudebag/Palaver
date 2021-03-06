package com.dudebag.palaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_USER = "com.dudebag.palaver.EXTRA_User";

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PALAVER_ID = "palaver_id";
    public static final String PALAVER_PW = "palaver_pw";
    public static final String LOGGED_IN = "logged_in";
    public static final String FROM_LOGIN = "from_login";
    public static final String FRIEND_LIST = "friend_list";
    public static final String PUSHTOKEN = "pushtoken";
    public static final String MESSAGE_LIST = "message_list";

    String benutzername;
    String passwort;

    String token;

    boolean loggedIn;
    boolean fromLogin;

    JsonApi jsonApi;

    Post responsePost;

    private RecyclerView mRecyclerView;
    private FriendAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public ArrayList<Friend> friendList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //Retrofit einrichten
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://palaver.se.paluno.uni-due.de/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Gson Konverter einrichten
        jsonApi = retrofit.create(JsonApi.class);

        friendList = new ArrayList<>();

        loadFirstLogin();



        //Erster Login -- Man hat immer Internet
        if (fromLogin) {

            loadData();

            endLogin();

            Post post = new Post(benutzername, passwort);
            getFriends(post);



            replaceToken();
            loadData();

            Pushtoken pushtoken = new Pushtoken(benutzername, passwort, token);

            refreshToken(pushtoken);

            return;

        }
        //Ab Zweiter Appstart
        else {

            //Internet
            if (checkInternet()) {

                replaceToken();
                loadData();

                friendList.clear();

                Post post = new Post(benutzername, passwort);
                getFriends(post);

                Pushtoken pushtoken = new Pushtoken(benutzername, passwort, token);
                refreshToken(pushtoken);

                return;

            }

            //kein Internet
            else {

                loadData();
                loadFriendlist();

                mRecyclerView = findViewById(R.id.recyclerView);
                //setHasFixedSize auf true wenn recyclerview immer gleich groß weil performance
                mRecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mAdapter = new FriendAdapter(friendList);

                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);


                //Klick auf einen Freund zum Öffnen des Chatverlaufs
                mAdapter.setOnItemClickListener(new FriendAdapter.OnItemClickListener() {

                    @Override
                    public void onItemClick(int position) {

                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);

                        intent.putExtra(EXTRA_USER, friendList.get(position).getName());

                        startActivity(intent);


                    }
                });

                return;


            }

        }

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
        Intent intent4 = new Intent(this.getApplicationContext(), ChangePasswordActivity.class);
        switch (item.getItemId()){
            case R.id.add_friend:
                startActivity(intent);
                return true;
            case R.id.delete_friend:
                startActivity(intent3);
                return true;
            case R.id.change_password:
                startActivity(intent4);
                return true;
            case R.id.logout:
                Pushtoken empty = new Pushtoken(benutzername, passwort, "");
                refreshToken(empty);
                deleteData();
                startActivity(intent2);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        //Internet
        if (checkInternet()) {

            replaceToken();
            loadData();
            friendList.clear();

            Post post = new Post(benutzername, passwort);
            getFriends(post);

            Pushtoken pushtoken = new Pushtoken(benutzername, passwort, token);
            refreshToken(pushtoken);

            return;

        }

        //Kein Internet
        else {

            loadData();
            loadFriendlist();

            mRecyclerView = findViewById(R.id.recyclerView);
            //setHasFixedSize auf true wenn recyclerview immer gleich groß weil performance
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mAdapter = new FriendAdapter(friendList);

            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mAdapter);


            //Klick auf einen Freund zum Öffnen des Chatverlaufs
            mAdapter.setOnItemClickListener(new FriendAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(int position) {

                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);

                    intent.putExtra(EXTRA_USER, friendList.get(position).getName());

                    startActivity(intent);


                }
            });

            return;

        }




    }


    private void getFriends(Post post){
        Call<Post> call = jsonApi.getFriends(post);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error) + response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }

                responsePost = response.body();

                for (int i = 0; i < responsePost.getData().size(); i++) {
                    friendList.add(new Friend(responsePost.getDataDetail(i)));

                }

                saveFriendlist();

                mRecyclerView = findViewById(R.id.recyclerView);
                //setHasFixedSize auf true wenn recyclerview immer gleich groß weil performance
                mRecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mAdapter = new FriendAdapter(friendList);

                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);




                //Klick auf einen Freund zum Öffnen des Chatverlaufs
                mAdapter.setOnItemClickListener(new FriendAdapter.OnItemClickListener() {

                    @Override
                    public void onItemClick(int position) {

                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);

                        intent.putExtra(EXTRA_USER, friendList.get(position).getName());

                        startActivity(intent);

                    }
                });


            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getString(R.string.error) + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void createRecycler(ArrayList<Friend> arrayList) {

    }


    private void refreshToken(Pushtoken pushtoken) {

        Call<Pushtoken> call = jsonApi.refreshToken(pushtoken);

        call.enqueue(new Callback<Pushtoken>() {
            @Override
            public void onResponse(Call<Pushtoken> call, Response<Pushtoken> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error) + response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }

                Pushtoken responseBody3 = response.body();

                if (responseBody3.getMsgType() == 1) {
                    //Toast.makeText(getApplicationContext(), responseBody3.getInfo(), Toast.LENGTH_SHORT).show();
                    return;
                }

                else {
                    Toast.makeText(getApplicationContext(), getString(R.string.error) + responseBody3.getInfo(), Toast.LENGTH_SHORT).show();
                    return;
                }



            }

            @Override
            public void onFailure(Call<Pushtoken> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getString(R.string.error) + t.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        });


    }






    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        loggedIn = sharedPreferences.getBoolean(LOGGED_IN, false);
        fromLogin = sharedPreferences.getBoolean(FROM_LOGIN, false);
        benutzername = sharedPreferences.getString(PALAVER_ID, "");
        passwort = sharedPreferences.getString(PALAVER_PW, "");
        token = sharedPreferences.getString(PUSHTOKEN, "");

    }

    public void endLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(FROM_LOGIN, false);
        fromLogin = false;

        editor.apply();
    }

    public void deleteData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(PALAVER_ID, "");
        editor.putString(PALAVER_PW, "");
        editor.putString(FRIEND_LIST, "");
        editor.putString(PUSHTOKEN, "");
        editor.putString(MESSAGE_LIST, "");
        editor.putBoolean(LOGGED_IN, false);
        editor.putBoolean(FROM_LOGIN, false);
        loggedIn = false;
        fromLogin = false;

        editor.apply();
    }


    private boolean checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void saveFriendlist() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        String json = gson.toJson(friendList);

        editor.putString(FRIEND_LIST, json);
        editor.apply();

    }

    private void loadFriendlist() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString(FRIEND_LIST, "");
        Type type = new TypeToken<ArrayList<Friend>>() {}.getType();
        friendList = gson.fromJson(json, type);
    }

    private void loadFirstLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        fromLogin = sharedPreferences.getBoolean(FROM_LOGIN, false);
    }


    private void replaceToken() {

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Replace Token fehlgeschlagen", Toast.LENGTH_SHORT).show();
                    return;
                }

                String token = task.getResult().getToken();

                saveToken(token);

                return;
            }
        });
    }


    private void saveToken(String t) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(PUSHTOKEN, t);

        editor.apply();
    }

    @Override
    public void onBackPressed() {

        //öffne Homescreen wenn Zurück-Taste gedrückt wird
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
