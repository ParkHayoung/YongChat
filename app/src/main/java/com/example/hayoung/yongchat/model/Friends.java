package com.example.hayoung.yongchat.model;

import java.util.List;

/**
 * Created by hayoung on 2017. 11. 5..
 */

public class Friends {
    String uid;
    List<User> friends;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }
}
