package com.example.project_fakebook.model;

public class ChatMessage {
    private int sender_id;
    private int receiver_id;
    private String message;
    private String updated_at;
    private String created_at;
    private int id;

    public int getSender_id() {
        return sender_id;
    }

    public int getReceiver_id() {
        return receiver_id;
    }

    public String getMessage() {
        return message;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public int getId() {
        return id;
    }
}
