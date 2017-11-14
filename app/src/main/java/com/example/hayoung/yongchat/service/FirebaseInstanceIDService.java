package com.example.hayoung.yongchat.service;

import com.example.hayoung.yongchat.model.User;
import com.example.hayoung.yongchat.session.UserSession;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by hayoung on 2017. 11. 14..
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        sendRegistrationToServer(refreshedToken);
    }
}
