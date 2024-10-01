package com.example.project_fakebook.model;

import com.google.gson.annotations.SerializedName;

public class ApiResponseGetUserProfile {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private profile data;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public profile getData() {
        return data;
    }

    public void setData(profile data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
