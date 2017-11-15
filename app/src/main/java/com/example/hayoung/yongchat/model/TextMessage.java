package com.example.hayoung.yongchat.model;

/**
 * Created by hayoung on 2017. 11. 5..
 */

public class TextMessage {
    String message;
    long createdAt;
    User sent;
    int unreadCount;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getSent() {
        return sent;
    }

    public void setSent(User sent) {
        this.sent = sent;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
