package com.example.project_fakebook.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ApiResponseGetConversation {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private Conversation data;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Conversation getData() {
        return data;
    }

    public void setData(Conversation data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
