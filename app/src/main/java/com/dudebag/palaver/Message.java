package com.dudebag.palaver;

public class Message {

    private String text;

    private boolean own;

    private String x;
    private String y;

    private String pic;


    public Message(String text, boolean own, String x, String y, String pic) {
        this.text = text;
        this.own = own;
        this.x = x;
        this.y = y;
        this.pic = pic;
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

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
