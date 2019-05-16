package com.dudebag.palaver;

import com.google.gson.annotations.SerializedName;

public class Post {

    @SerializedName("Username")
    private String username;

    @SerializedName("Password")
    private String password;

    @SerializedName("NewPassword")
    private String newPassword;

    @SerializedName("MsgType")
    private int msgType;

    @SerializedName("Info")
    private String info;

    @SerializedName("Data")
    private String data;

    @SerializedName("PushToken")
    private String pushToken;

    @SerializedName("Recipient")
    private String recipient;

    @SerializedName("Mimetype")
    private String mimeType;

    @SerializedName("DateTime")
    private String dateTime;

    @SerializedName("Offset")
    private String offset;

    @SerializedName("Friend")
    private String friend;


    public Post(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Post(String username, String password, String newPassword) {
        this.username = username;
        this.password = password;
        this.newPassword = newPassword;
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

    public String getNewPassword() { return newPassword; }

    public String getPushToken() { return pushToken; }

    public String getRecipient() { return recipient; }

    public String getMimeType() { return mimeType; }

    public String getDateTime() { return dateTime; }

    public String getOffset() { return offset; }

    public String getFriend() { return friend; }
}
