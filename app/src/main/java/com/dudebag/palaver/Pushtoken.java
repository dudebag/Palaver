package com.dudebag.palaver;

import com.google.gson.annotations.SerializedName;

public class Pushtoken {

    @SerializedName("Username")
    private String username;

    @SerializedName("Password")
    private String password;

    @SerializedName("PushToken")
    private String pushToken;

    @SerializedName("MsgType")
    private int msgType;

    @SerializedName("Info")
    private String info;

    @SerializedName("Data")
    private String data;


    public Pushtoken(String username, String password, String pushToken) {
        this.username = username;
        this.password = password;
        this.pushToken = pushToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
