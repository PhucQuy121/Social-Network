package com.example.project_fakebook.model;


import java.util.ArrayList;
import java.util.List;

public class profile {
    private UserInfo info;
    private ArrayList<Post> posts;
    private ArrayList<Friend> friends;
    private ArrayList<RequestFriends> requestFriends;

    private ArrayList<FriendRequests> friendRequests;

    public UserInfo getInfo() {
        return info;
    }

    public void setInfo(UserInfo info) {
        this.info = info;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    public ArrayList<Friend> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<Friend> friends) {
        this.friends = friends;
    }

    public ArrayList<RequestFriends> getRequestFriends() {
        return requestFriends;
    }

    public void setRequestFriends(ArrayList<RequestFriends> requestFriends) {
        this.requestFriends = requestFriends;
    }

    public ArrayList<FriendRequests> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(ArrayList<FriendRequests> friendRequests) {
        this.friendRequests = friendRequests;
    }
}

