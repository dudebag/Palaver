package com.dudebag.palaver;

public class Friend {

    private String name;

    public Friend(String name) {
        this.name = name;
    }

    public void changeText(String text) {
        name = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
