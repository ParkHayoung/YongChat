package com.example.hayoung.yongchat.model;

/**
 * Created by hayoung on 2017. 11. 5..
 */

public class TextMessage {
    String message;
    User sent;
    String chatRoomId;
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

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
