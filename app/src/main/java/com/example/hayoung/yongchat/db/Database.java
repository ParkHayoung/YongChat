package com.example.hayoung.yongchat.db;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by brannpark on 2017. 11. 15..
 */

public class Database {

    private static FirebaseDatabase db;

    public static void initialize() {
         db = FirebaseDatabase.getInstance();
    }

    public static DatabaseReference users() {
        return db.getReference("users");
    }

    public static DatabaseReference rooms() {
        return db.getReference("rooms");
    }

    public static DatabaseReference friends() {
        return db.getReference("friends");
    }

    public static DatabaseReference messages() {
        return db.getReference("messages");
    }

    public static DatabaseReference userRooms() {
        return db.getReference("user_rooms");
    }
}
