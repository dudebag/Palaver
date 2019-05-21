package com.dudebag.palaver;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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

    //@SerializedName("Data")
    //private String data [];

    @SerializedName("Data")
    private List<String> data;

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

    //processRegistration(), processLogin(), getFriends()
    public Post(String username, String password) {
        this.username = username;
        this.password = password;
    }

    //addFriends(), removeFriends()
    public Post(String username, String password, String friend) {
        this.username = username;
        this.password = password;
        this.friend = friend;
    }

    //getMessages()
    /*public Post(String username, String password, String recipient) {
        this.username = username;
        this.password = password;
        this.recipient = recipient;
    }*/


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public void addData(String name) {
        data.add(name);
    }

    /*public void setData(String[] data) {
        this.data = data;
    }*/

    /*public void addData(String name) {
        String list [] = new String[getData().length];
        for (int i = 0; i < list.length; i++){
            if (list[i] == null){
                list[i] = name;
                setData(list);
                return;
            }
        }
    }*/

    /*public int getLength() {
        return data.length;
    }*/

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

    /*public String getData(int i) {
        return data[i];
    }*/

    public List<String> getData() {
        return data;
    }

    public String getDataDetail(int i) {
        return data.get(i);
    }

    public String getNewPassword() { return newPassword; }

    public String getPushToken() { return pushToken; }

    public String getRecipient() { return recipient; }

    public String getMimeType() { return mimeType; }

    public String getDateTime() { return dateTime; }

    public String getOffset() { return offset; }

    public String getFriend() { return friend; }
}
