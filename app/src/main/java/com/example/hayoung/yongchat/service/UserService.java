package com.example.hayoung.yongchat.service;

import android.support.annotation.NonNull;

import com.example.hayoung.yongchat.db.Database;
import com.example.hayoung.yongchat.model.User;

/**
 * Created by brannpark on 2017. 11. 15..
 */

public class UserService {

    public void updateUser(@NonNull User user) {
        // users 데이터베이스의 정보 갱신
        Database.users().child(user.getUid()).setValue(user);
        // 채팅방에 있는 사용자 정보 갱신

    }
}
