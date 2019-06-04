package com.dudebag.palaver;

public class Message {

    private String text;
    private boolean own;

    public Message(String text, boolean own) {
        this.text = text;
        this.own = own;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isOwn() {
        return own;
    }

    public void setOwn(boolean own) {
        this.own = own;
    }


}
