package com.example.project_fakebook.model;

import java.util.ArrayList;

public class UserList {
    private int user_id;
    private String message;
    private ArrayList<UserProfile> info;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<UserProfile> getInfo() {
        return info;
    }

    public void setInfo(ArrayList<UserProfile> info) {
        this.info = info;
    }
}
