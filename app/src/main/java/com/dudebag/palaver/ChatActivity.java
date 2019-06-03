package com.dudebag.palaver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {

    ImageButton button;

    EditText editText;

    String benutzername;
    String passwort;
    String user;

    JsonApi jsonApi;

    ArrayList<Message> messageList;
    ArrayList<String> testList;

    PostAnswer responsePost;
    PostMessage responsePost2;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        //Benutzername und Passwort wird in Empfang genommen
        Intent intent = getIntent();
        benutzername = intent.getStringExtra(MainActivity.EXTRA_BENUTZERNAME);
        passwort = intent.getStringExtra(MainActivity.EXTRA_PASSWORT);
        user = intent.getStringExtra(MainActivity.EXTRA_USER);

        //Retrofit einrichten
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://palaver.se.paluno.uni-due.de/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Gson Konverter einrichten
        jsonApi = retrofit.create(JsonApi.class);

        editText = findViewById(R.id.input_message);

        button = findViewById(R.id.send_msg_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Kein Internet
                if (!checkInternet()) {
                    Toast.makeText(getApplicationContext(), "Du hast kein Internet", Toast.LENGTH_SHORT).show();
                    return;
                }

                String input = editText.getText().toString().trim();
                PostMessage post2 = new PostMessage(benutzername, passwort, user, "text/plain", input);
                PostAnswer post = new PostAnswer(benutzername, passwort, user);

                sendMessage(post2);
                getMessages(post);

                editText.setText("");
            }
        });

        messageList = new ArrayList<>();



        PostAnswer post = new PostAnswer(benutzername, passwort, user);

        getMessages(post);


    }


    //oben rechts optionsmenü mit benutzer hinzufügen
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }


    private void sendMessage(PostMessage post) {
        Call<PostMessage> call = jsonApi.sendMessage(post);

        call.enqueue(new Callback<PostMessage>() {
            @Override
            public void onResponse(Call<PostMessage> call, Response<PostMessage> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "1: " + response.toString(), Toast.LENGTH_LONG).show();
                    return;
                }

                responsePost2 = response.body();

                if (responsePost2.getMsgType() == 1) {
                    Toast.makeText(getApplicationContext(), "MsgType: " + responsePost2.getMsgType() + "\n" + "Info: " + responsePost2.getInfo(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "MsgType: " + responsePost2.getMsgType() + "\n" + "Info: " + responsePost2.getInfo(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PostMessage> call, Throwable t) {
                /*if (t instanceof IOException)
                    Toast.makeText(getApplicationContext(), "2: " + t.getMessage(), Toast.LENGTH_LONG).show();
                else if (t instanceof IllegalStateException)
                    Toast.makeText(getApplicationContext(), "3: " + t.getMessage(), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), "4: " + t.getMessage(), Toast.LENGTH_LONG).show();*/
                Log.d("HELP", t.getMessage());

            }
        });

    }


    private void getMessages(PostAnswer post) {
        Call<PostAnswer> call = jsonApi.getMessages(post);

        call.enqueue(new Callback<PostAnswer>() {
            @Override
            public void onResponse(Call<PostAnswer> call, Response<PostAnswer> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Fehler aufgetaucht", Toast.LENGTH_LONG).show();
                    return;
                }

                //responsePost = new PostAnswer();

                responsePost = response.body();

                //if (responsePost.isEmpty())
                //    return;

                if (response.body().isEmpty())
                    return;

                messageList.clear();



                for (int i = 0; i < responsePost.getData().size(); i++) {

                    String text = responsePost.getData().get(i).getData();

                    //wenn Nachricht von uns selbst geschrieben
                    if (responsePost.getData().get(i).getSender().equals(benutzername)) {
                        messageList.add(new Message(text, true));
                    }
                    //Nachricht von dem anderen geschrieben
                    else {
                        messageList.add(new Message(text, false));
                    }



                }

                mRecyclerView = findViewById(R.id.private_messages);
                mRecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mAdapter = new MessageAdapter(messageList);



                //mAdapter.bindViewHolder();

                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);


                /*String text = "";
                text += "Code: " + response.code() + "\n";
                text += "MsgType: " + responsePost.getMsgType() + "\n";
                text += "Info: " + responsePost.getInfo() + "\n";
                text += "Data: " + "\n";*/


                return;

            }

            @Override
            public void onFailure(Call<PostAnswer> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "5: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }




    private boolean checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}
