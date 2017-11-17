package com.example.hayoung.yongchat.service;

import android.support.annotation.NonNull;

import com.example.hayoung.yongchat.db.Database;
import com.example.hayoung.yongchat.model.ChatRoom;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by brannpark on 2017. 11. 17..
 */

public class RoomService {

    public void requestRoom(final @NonNull String roomId,
                            final @NonNull DataCallback<ChatRoom> callback) {

        Database.rooms().child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatRoom room = dataSnapshot.getValue(ChatRoom.class);
                callback.onResult(room);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
