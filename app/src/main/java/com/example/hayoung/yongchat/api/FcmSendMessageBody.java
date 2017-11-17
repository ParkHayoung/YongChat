package com.example.hayoung.yongchat.api;

import android.support.annotation.NonNull;

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

    public FcmSendMessageBody(@NonNull String roomId,
                              @NonNull User sentUser,
                              @NonNull String token,
                              @NonNull TextMessage textMessage) {

        this.collapse_key = roomId;
        this.to = token;
        this.notification = new Notification();
        notification.title = sentUser.getName();
        notification.body = textMessage.getMessage();
        notification.sound = "nyang";
        notification.tag = roomId;
        this.data = new Data();

    }

    public static class Notification {
        private String title;
        private String body;
        private String sound;
        private String tag;
    }

    public static class Data {

    }
}
