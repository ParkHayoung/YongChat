package com.example.hayoung.yongchat.service;

import com.example.hayoung.yongchat.model.User;
import com.example.hayoung.yongchat.session.UserSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by hayoung on 2017. 11. 14..
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        User me = UserSession.getInstance().getCurrentUser();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (me != null && auth.getCurrentUser() != null) {
            String token = FirebaseInstanceId.getInstance().getToken();
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            db.getReference("users").child(me.getUid());
        }
    }
}
