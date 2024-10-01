package com.example.project_fakebook.model;

import java.util.ArrayList;
import java.util.List;

public class Posts {
    private int userId;
    private String userName;
    private String avtSrcImg;
    private String contentText;
    private String postSrcImg;
    private ArrayList<String> listLikes;
    private ArrayList<String> listComments;
    private boolean likePost;

    public Posts(int userId, String userName, String avtSrcImg, String contentText, String postSrcImg, ArrayList<String> listLikes, ArrayList<String> listComments, boolean likePost) {
        this.userId = userId;
        this.userName = userName;
        this.avtSrcImg = avtSrcImg;
        this.contentText = contentText;
        this.postSrcImg = postSrcImg;
        this.listLikes = listLikes;
        this.listComments = listComments;
        this.likePost = likePost;
    }

    @Override
    public String toString() {
        return "Posts{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", avtSrcImg='" + avtSrcImg + '\'' +
                ", contentText='" + contentText + '\'' +
                ", postSrcImg='" + postSrcImg + '\'' +
                ", listLikes=" + listLikes +
                ", listComments=" + listComments +
                ", likePost=" + likePost +
                '}';
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvtSrcImg() {
        return avtSrcImg;
    }

    public void setAvtSrcImg(String avtSrcImg) {
        this.avtSrcImg = avtSrcImg;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getPostSrcImg() {
        return postSrcImg;
    }

    public void setPostSrcImg(String postSrcImg) {
        this.postSrcImg = postSrcImg;
    }

    public ArrayList<String> getListLikes() {
        return listLikes;
    }

    public void setListLikes(ArrayList<String> listLikes) {
        this.listLikes = listLikes;
    }

    public ArrayList<String> getListComments() {
        return listComments;
    }

    public void setListComments(ArrayList<String> listComments) {
        this.listComments = listComments;
    }


    public boolean isLikePost() {
        return likePost;
    }

    public void setLikePost(boolean likePost) {
        this.likePost = likePost;
    }
}
