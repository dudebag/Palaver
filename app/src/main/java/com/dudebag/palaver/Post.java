package com.dudebag.palaver;

import com.google.gson.annotations.SerializedName;

public class Post {
    @SerializedName("Username")
    private String username;

    @SerializedName("Password")
    private String password;

    @SerializedName("MsgType")
    private int msgType;

    @SerializedName("Info")
    private String info;

    @SerializedName("Data")
    private String data;


    public Post(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getMsgType() {
        return msgType;
    }

    public String getInfo() {
        return info;
    }

    public String getData() {
        return data;
    }
}
