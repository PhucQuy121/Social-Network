package com.example.project_fakebook.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ApiResponseGetFriendResquest {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private ArrayList<FriendRequests> data;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ArrayList<FriendRequests> getData() {
        return data;
    }

    public void setData(ArrayList<FriendRequests> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
