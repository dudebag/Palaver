package com.dudebag.palaver;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostAnswer{


    @SerializedName("Username")
    private String username;

    @SerializedName("Password")
    private String password;

    @SerializedName("Data")
    private List<PostMessage> data;

    @SerializedName("Recipient")
    private String recipient;

    @SerializedName("MsgType")
    private int msgType;

    @SerializedName("Info")
    private String info;

    public PostAnswer(String username, String password, String recipient) {
        this.username = username;
        this.password = password;
        this.recipient = recipient;
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

    public List<PostMessage> getData() {
        return data;
    }

    public void setData(List<PostMessage> data) {
        this.data = data;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
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
}
