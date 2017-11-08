package com.example.hayoung.yongchat.model;

import java.util.Date;

/**
 * Created by hayoung on 2017. 11. 5..
 */

public class TextMessage {
    String message;
    Date date;
    User sent;
    String roomId;
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

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
