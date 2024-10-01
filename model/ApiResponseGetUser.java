package com.example.project_fakebook.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ApiResponseGetUser {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private UserInfo data;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public UserInfo getData() {
        return data;
    }

    public void setData(UserInfo data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
