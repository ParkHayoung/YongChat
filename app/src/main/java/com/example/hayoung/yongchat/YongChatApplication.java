package com.example.hayoung.yongchat;

import android.app.Application;

import com.example.hayoung.yongchat.session.UserSession;
import com.tsengvn.typekit.Typekit;

/**
 * Created by brannpark on 2017. 11. 6..
 */

public class YongChatApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        UserSession.init(this);
    }
}
