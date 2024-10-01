package com.example.project_fakebook.model;

import java.util.ArrayList;

public class Conversation {
    private ArrayList<ChatMessage> messages;
    private ArrayList<UserList> userList;
    private UserInfo receiver;

    public ArrayList<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<ChatMessage> messages) {
        this.messages = messages;
    }

    public ArrayList<UserList> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<UserList> userList) {
        this.userList = userList;
    }

    public UserInfo getReceiver() {
        return receiver;
    }

    public void setReceiver(UserInfo receiver) {
        this.receiver = receiver;
    }
}
