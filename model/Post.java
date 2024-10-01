package com.example.project_fakebook.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Post implements Parcelable {
    @SerializedName("id")
    private int id;

    @SerializedName("content")
    private String content;

    @SerializedName("image")
    private String image;


    @SerializedName("status")
    private Integer status;


    @SerializedName("user_id")
    private Integer user_id;


    @SerializedName("created_at")
    private String created_at;

    @SerializedName("updated_at")
    private String updated_at;

    @SerializedName("deleted_at")
    private String deleted_at;

    @SerializedName("likes_count")
    private Integer likes_count;

    @SerializedName("dislikes_count")
    private Integer dislikes_count;

    @SerializedName("comments_count")
    private Integer comments_count;

    @SerializedName("user")
    private UserProfile user;

    @SerializedName("reactions")
    private List<Reaction> reactions;

    public Post(int id, String content, String image, Integer status, Integer user_id, String created_at, String updated_at, String deleted_at, Integer likes_count, Integer dislikes_count, Integer comments_count, UserProfile user, List<Reaction> reactions) {
        this.id = id;
        this.content = content;
        this.image = image;
        this.status = status;
        this.user_id = user_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
        this.likes_count = likes_count;
        this.dislikes_count = dislikes_count;
        this.comments_count = comments_count;
        this.user = user;
        this.reactions = reactions;
    }

    protected Post(Parcel in) {
        id = in.readInt();
        content = in.readString();
        image = in.readString();
        if (in.readByte() == 0) {
            status = null;
        } else {
            status = in.readInt();
        }
        if (in.readByte() == 0) {
            user_id = null;
        } else {
            user_id = in.readInt();
        }
        created_at = in.readString();
        updated_at = in.readString();
        deleted_at = in.readString();
        if (in.readByte() == 0) {
            likes_count = null;
        } else {
            likes_count = in.readInt();
        }
        if (in.readByte() == 0) {
            dislikes_count = null;
        } else {
            dislikes_count = in.readInt();
        }
        if (in.readByte() == 0) {
            comments_count = null;
        } else {
            comments_count = in.readInt();
        }
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public Integer getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(Integer likes_count) {
        this.likes_count = likes_count;
    }

    public Integer getDislikes_count() {
        return dislikes_count;
    }

    public void setDislikes_count(Integer dislikes_count) {
        this.dislikes_count = dislikes_count;
    }

    public Integer getComments_count() {
        return comments_count;
    }

    public void setComments_count(Integer comments_count) {
        this.comments_count = comments_count;
    }

    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }

    public List<Reaction> getReactions() {
        return reactions;
    }

    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(content);
        dest.writeString(image);
        if (status == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(status);
        }
        if (user_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(user_id);
        }
        dest.writeString(created_at);
        dest.writeString(updated_at);
        dest.writeString(deleted_at);
        if (likes_count == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(likes_count);
        }
        if (dislikes_count == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(dislikes_count);
        }
        if (comments_count == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(comments_count);
        }
    }
}
