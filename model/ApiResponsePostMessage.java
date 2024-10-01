package com.example.project_fakebook.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ApiResponsePostMessage {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private ChatMessage data;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ChatMessage getData() {
        return data;
    }

    public void setData(ChatMessage data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
