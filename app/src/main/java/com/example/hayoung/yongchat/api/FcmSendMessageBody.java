package com.example.hayoung.yongchat.api;

import android.support.annotation.NonNull;

import com.example.hayoung.yongchat.model.TextMessage;
import com.example.hayoung.yongchat.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brannpark on 2017. 11. 14..
 */

public class FcmSendMessageBody {

    private List<String> registration_ids;
    private int time_to_live = 3;
    private String collapse_key;
    private Notification notification;
    private String priority = "high";
    //private Data data;

    public FcmSendMessageBody(@NonNull String roomId,
                              @NonNull User sentUser,
                              @NonNull List<User> recipients,
                              @NonNull TextMessage textMessage) {

        this.collapse_key = roomId;

        List<String> tokens = new ArrayList<>();
        for (User user : recipients) {
            tokens.add(user.getToken());
        }
        this.registration_ids = tokens;
        this.notification = new Notification();
        notification.title = sentUser.getName();
        notification.body = textMessage.getMessage();

        //this.data = new Data();

    }

    public static class Notification {
        String title;
        String body;
    }

//    public static class Data {
//
//    }
}
