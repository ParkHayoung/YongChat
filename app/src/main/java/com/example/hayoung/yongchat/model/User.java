package com.example.hayoung.yongchat.model;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

/**
 * Created by hayoung on 2017. 11. 5..
 */

public class User implements Serializable {

    private static final long serialVersionUID = 1293948272773842807L;

    String uid;
    String email;
    String name;
    String imageUrl;

    public User() {

    }

    public User(FirebaseUser firebaseUser) {
        uid = firebaseUser.getUid();
        email = firebaseUser.getEmail();
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
}
