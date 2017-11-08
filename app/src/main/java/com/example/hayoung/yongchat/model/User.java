package com.example.hayoung.yongchat.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

/**
 * Created by hayoung on 2017. 11. 5..
 */

public class User implements Parcelable {

    String uid;
    String email;
    String name;
    String imageUrl;


    public User() {

    }

    public User(FirebaseUser firebaseUser) {
        uid = firebaseUser.getUid();
        email = firebaseUser.getEmail();
        if (email != null) {
            email = email.trim().toLowerCase();
        }
        name = firebaseUser.getDisplayName();
        imageUrl = firebaseUser.getPhotoUrl().toString();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.email);
        dest.writeString(this.name);
        dest.writeString(this.imageUrl);
    }

    protected User(Parcel in) {
        this.uid = in.readString();
        this.email = in.readString();
        this.name = in.readString();
        this.imageUrl = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
