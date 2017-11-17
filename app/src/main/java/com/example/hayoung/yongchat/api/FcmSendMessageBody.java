package com.example.hayoung.yongchat.api;

import android.support.annotation.NonNull;

import com.example.hayoung.yongchat.model.ChatRoom;
import com.example.hayoung.yongchat.model.TextMessage;
import com.example.hayoung.yongchat.model.User;

/**
 * Created by brannpark on 2017. 11. 14..
 */

public class FcmSendMessageBody {

    private String to;
    private int time_to_live = 3;
    private String collapse_key;
    private Notification notification;
    private String priority = "high";
    private Data data;

    public FcmSendMessageBody(@NonNull ChatRoom room,
                              @NonNull User fromUser,
                              @NonNull User toUser,
                              @NonNull TextMessage textMessage) {

        collapse_key = room.getRoomId();
        to = toUser.getToken();

        notification = new Notification();
        notification.title = fromUser.getName();
        notification.body = textMessage.getMessage();
        notification.sound = "nyang";
        notification.tag = room.getRoomId();
        notification.click_action = "chat";
        data = new Data();
        data.room_id = room.getRoomId();

    }

    public static class Notification {
        private String title;
        private String body;
        private String sound;
        private String tag;
        private String click_action;
    }

    public static class Data {
        private String room_id;
    }
}