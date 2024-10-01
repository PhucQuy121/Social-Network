package com.example.project_fakebook.model;

import java.util.List;

public class Result {
    private Boolean success;

    private List<data> data;
    private String message;

    public List<com.example.project_fakebook.model.data> getData() {
        return data;
    }

    public void setData(List<com.example.project_fakebook.model.data> data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
