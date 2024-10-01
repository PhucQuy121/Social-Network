package com.example.project_fakebook.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class UserProfile implements Parcelable {
    @SerializedName("id")
    private int id;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("avatar")
    private String avatar;

    public UserProfile(String lastName, String firstName, String avatar) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.avatar = avatar;
    }

    public UserProfile(int id, String lastName, String firstName, String avatar) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.avatar = avatar;
    }

    protected UserProfile(Parcel in) {
        id = in.readInt();
        lastName = in.readString();
        firstName = in.readString();
        avatar = in.readString();
    }

    public static final Creator<UserProfile> CREATOR = new Creator<UserProfile>() {
        @Override
        public UserProfile createFromParcel(Parcel in) {
            return new UserProfile(in);
        }

        @Override
        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(lastName);
        dest.writeString(firstName);
        dest.writeString(avatar);
    }
}
