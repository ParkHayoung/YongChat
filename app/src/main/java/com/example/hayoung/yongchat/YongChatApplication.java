package com.example.hayoung.yongchat;

import android.app.Application;

import com.example.hayoung.yongchat.db.Database;
import com.example.hayoung.yongchat.session.UserSession;

/**
 * Created by brannpark on 2017. 11. 6..
 */

public class YongChatApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        UserSession.init(this);
        Database.initialize();
    }
}
