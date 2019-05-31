package com.dudebag.palaver;

import com.google.gson.annotations.SerializedName;

public class PostMessage {

    @SerializedName("Username")
    private String username;

    @SerializedName("Password")
    private String password;

    @SerializedName("Data")
    private String data;

    @SerializedName("Recipient")
    private String recipient;

    @SerializedName("Mimetype")
    private String mimeType;

    @SerializedName("MsgType")
    private int msgType;

    @SerializedName("Info")
    private String info;

    @SerializedName("Sender")
    private String sender;

    @SerializedName("DateTime")
    private String dateTime;

    public PostMessage(String username, String password, String recipient, String mimeType, String data) {
        this.username = username;
        this.password = password;
        this.recipient = recipient;
        this.mimeType = mimeType;
        this.data = data;
    }

    public PostMessage(String username, String password, String recipient) {
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
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

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
