package com.example.hayoung.yongchat.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.example.hayoung.yongchat.model.User;
import com.google.gson.Gson;

/**
 * Created by brannpark on 2017. 11. 6..
 */

public class UserSession {

    private static UserSession userSession;

    private User user;
    private Context context;

    public static void init(@NonNull Context context) {
        getInstance();
        userSession.context = context.getApplicationContext();
        userSession.loadFromPreferences();
    }

    public static UserSession getInstance() {
        synchronized (UserSession.class) {
            if (userSession == null) {
                userSession = new UserSession();
            }
            return userSession;
        }
    }

    public void setCurrentUser(User user) {
        this.user = user;
        saveToPreferences();
    }

    public User getCurrentUser() {
        return user;
    }

    private void saveToPreferences() {
        SharedPreferences preferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (user == null) {
            editor.remove("user").apply();
        } else {
            Gson gson = new Gson();
            editor.putString("user", gson.toJson(user));
            editor.apply();
        }
    }

    private void loadFromPreferences() {
        SharedPreferences preferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String userString = preferences.getString("user", null);
        if (userString != null) {
            Gson gson = new Gson();
            user = gson.fromJson(userString, User.class);
        }
    }

}
