package com.example.project_fakebook.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class data {
    @SerializedName("info")
    private UserInfo info;

    @SerializedName("posts")
    private List<Post> posts;

    @SerializedName("friends")
    private List<Friend> friends;

    public UserInfo getInfo() {
        return info;
    }

    public void setInfo(UserInfo info) {
        this.info = info;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }
}

