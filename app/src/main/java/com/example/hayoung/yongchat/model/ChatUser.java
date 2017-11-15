package com.example.hayoung.yongchat.model;

/**
 * Created by brannpark on 2017. 11. 15..
 */

public class ChatUser {
    private String uid;
    private String name;

    public ChatUser(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
