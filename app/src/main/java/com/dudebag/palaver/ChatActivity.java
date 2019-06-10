package com.dudebag.palaver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PALAVER_ID = "palaver_id";
    public static final String PALAVER_PW = "palaver_pw";
    public static final String MESSAGE_LIST = "message_list";


    ImageButton button;
    ImageButton fileSelectbtn;

    EditText editText;

    Bitmap bitmap  ;

    String benutzername;
    String passwort;
    String user;

    JsonApi jsonApi;
    ArrayList<Message> messageList;

    PostAnswer responsePost;
    PostMessage responsePost2;

    private RecyclerView mRecyclerView;
    private MessageAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Uri fileUri;
    private   InputStream imageStream = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        loadData();

        //Benutzername und Passwort wird in Empfang genommen
        Intent intent = getIntent();
        user = intent.getStringExtra(MainActivity.EXTRA_USER);

        //Retrofit einrichten
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://palaver.se.paluno.uni-due.de/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Gson Konverter einrichten
        jsonApi = retrofit.create(JsonApi.class);

        editText = findViewById(R.id.input_message);
       // fileSelectbtn = findViewById(R.id.file_slct_btn);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
                    // zurzeit wird GPS als Toast gesendet
            case R.id.gps:

                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {


                        PostMessage gpsString = new PostMessage(benutzername, passwort, user, "gpsMessage", location.getLatitude() + "x" + location.getLongitude());
                        PostAnswer messages = new PostAnswer(benutzername, passwort, user);

                        sendMessage(gpsString);
                        getMessages(messages);
                        return;
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);

                    }
                };

                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //  Toast.makeText(getApplicationContext(),"Test10",Toast.LENGTH_SHORT).show();
                    requestPermissions(new String[]{


                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.INTERNET,


                }, 10);


                }
                if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates("gps", 5000, 50, locationListener);
                    return true;
                }

                return true;



           case R.id.image_menu:

               Intent intent = new Intent();
               intent.setAction(Intent.ACTION_GET_CONTENT);
               intent.setType("image/*");
               startActivityForResult(intent.createChooser(intent,"Wähle ein Bild"),20);
               return true;




            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)

                    //KEIN FEHLER NICHT RESOLVEN ist in Ordnung, dass rot angezeigt wird
                    locationManager.requestLocationUpdates("gps", 5000, 50, locationListener);
                return;

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // Toast.makeText(getApplicationContext(), "om", Toast.LENGTH_SHORT).show();
        if(requestCode == 20 &&  data != null && data.getData() != null){

            fileUri = data.getData();
            try {
               imageStream = getContentResolver().openInputStream(fileUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap = BitmapFactory.decodeStream(imageStream);

            //Image ist nun encodeter String
            String encodedImage = encodeImage(bitmap);

            PostMessage imgP= new PostMessage(benutzername, passwort, user,"imageMessage", encodedImage);
            PostAnswer imagePost = new PostAnswer(benutzername, passwort, user);

            sendMessage(imgP);

            getMessages(imagePost);
        }
    }

    private String encodeImage(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }



    private void sendMessage(PostMessage post) {
        Call<PostMessage> call = jsonApi.sendMessage(post);

        call.enqueue(new Callback<PostMessage>() {
            @Override
            public void onResponse(Call<PostMessage> call, Response<PostMessage> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error) + response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (responsePost2 == null)
                    return;

                responsePost2 = response.body();


                if (responsePost2.getMsgType() == 1) {
                    Toast.makeText(getApplicationContext(), "MsgType: " + responsePost2.getMsgType() + "\n" + "Info: " + responsePost2.getInfo(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "MsgType: " + responsePost2.getMsgType() + "\n" + "Info: " + responsePost2.getInfo(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostMessage> call, Throwable t) {
                /*if (t instanceof IOException)
                    Toast.makeText(getApplicationContext(), "2: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                else if (t instanceof IllegalStateException)
                    Toast.makeText(getApplicationContext(), "3: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "4: " + t.getMessage(), Toast.LENGTH_SHORT).show();*/
                //Toast.makeText(getApplicationContext(), getString(R.string.error) + t.getMessage(), Toast.LENGTH_SHORT).show();
                return;

            }
        });

    }


    private void getMessages(PostAnswer post) {
        Call<PostAnswer> call = jsonApi.getMessages(post);

        call.enqueue(new Callback<PostAnswer>() {
            @Override
            public void onResponse(Call<PostAnswer> call, Response<PostAnswer> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error) + response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.body() == null || response.body().isEmpty())
                    return;

                responsePost = response.body();

                messageList.clear();




                for (int i = 0; i < responsePost.getData().size(); i++) {

                    String text = responsePost.getData().get(i).getData();

                    //wenn Nachricht von uns selbst geschrieben
                    if (responsePost.getData().get(i).getSender().equals(benutzername)) {

                        //GPS Nachricht
                        if (responsePost.getData().get(i).getMimeType().equals("gpsMessage")) {

                            String [] parts = responsePost.getData().get(i).getData().split("x");
                            String part1 = parts[0];
                            String part2 = parts[1];
                            messageList.add(new Message(benutzername + "-Standort", true, part1, part2, ""));
                        }

                        //Bild Nachricht
                        else if (responsePost.getData().get(i).getMimeType().equals("imageMessage")) {
                            messageList.add(new Message(benutzername + "-Bild", true, "", "", responsePost.getData().get(i).getData()));
                        }

                        //Text Nachricht
                        else {
                            messageList.add(new Message(text, true, "", "", ""));
                        }
                    }


                    //Nachricht von dem anderen geschrieben
                    else {

                        //GPS Nachricht
                        if (responsePost.getData().get(i).getMimeType().equals("gpsMessage")) {

                            String [] parts = responsePost.getData().get(i).getData().split("x");
                            String part1 = parts[0];
                            String part2 = parts[1];
                            messageList.add(new Message(user + "-Standort", false, part1, part2, ""));
                        }

                        //Bild Nachricht
                        else if (responsePost.getData().get(i).getMimeType().equals("imageMessage")) {
                            messageList.add(new Message(user + "-Bild", false, "", "", responsePost.getData().get(i).getData()));
                        }

                        //Text Nachricht
                        else {
                            messageList.add(new Message(text, false, "", "", ""));
                        }
                    }


                }


                mRecyclerView = findViewById(R.id.private_messages);
                mRecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mAdapter = new MessageAdapter(messageList);


                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);

                mAdapter.setOnItemClickListener(new MessageAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        //wenn GPS ist
                        if (!messageList.get(position).getX().equals("")) {
                            String gpsUri= "http://maps.google.com/maps?daddr=" + messageList.get(position).getX() + "," + messageList.get(position).getY();
                            Intent intentGps = new Intent(Intent.ACTION_VIEW,Uri.parse(gpsUri));
                            startActivity(intentGps);
                        }

                        //wenn Image ist
                        /*else if (!messageList.get(position).getPic().equals("")) {
                            Intent imageIntent = new Intent();
                            imageIntent.setAction(Intent.ACTION_GET_CONTENT);
                            imageIntent.setType("image/*");
                            startActivityForResult(imageIntent.createChooser(imageIntent, "Wähle ein Foto"), 30);
                        }*/
                    }
                });


                return;

            }

            @Override
            public void onFailure(Call<PostAnswer> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getString(R.string.error) + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        benutzername = sharedPreferences.getString(PALAVER_ID, "");
        passwort = sharedPreferences.getString(PALAVER_PW, "");

    }



    private void saveMessagelist() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        String json = gson.toJson(messageList);

        editor.putString(MESSAGE_LIST, json);
        editor.apply();

    }

    private void loadMessagelist() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString(MESSAGE_LIST, "");
        Type type = new TypeToken<ArrayList<Friend>>() {}.getType();
        messageList = gson.fromJson(json, type);
    }




}
