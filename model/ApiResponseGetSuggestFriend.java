package com.example.project_fakebook.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ApiResponseGetSuggestFriend {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private ArrayList<UserInfo> data;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ArrayList<UserInfo> getData() {
        return data;
    }

    public void setData(ArrayList<UserInfo> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
