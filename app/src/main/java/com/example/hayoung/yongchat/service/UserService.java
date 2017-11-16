package com.example.hayoung.yongchat.service;

import android.support.annotation.NonNull;

import com.example.hayoung.yongchat.db.Database;
import com.example.hayoung.yongchat.model.ChatRoom;
import com.example.hayoung.yongchat.model.User;
import com.example.hayoung.yongchat.session.UserSession;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class UserService {

    public interface DataCallback<T> {
        void onResults(@NonNull List<T> items);
        void onResult(@NonNull T item);
    }

    public void signUpUser(@NonNull User user) {
        // users 데이터베이스에 사용자 추가
        // FirebaseUser 의 uid 를 키로 사용
        Database.users().child(user.getUid()).setValue(user);
        // 로컬 사용자 세션에 사용자 등록
        UserSession.getInstance().setCurrentUser(user);
    }

    public void updateUser(final @NonNull User user) {
        // users 데이터베이스의 정보 갱신
        Database.users().child(user.getUid()).setValue(user);
        // 로컬 사용자 세션에 사용자 등록
        UserSession.getInstance().setCurrentUser(user);
        // 채팅방에 있는 사용자 정보 갱신
        requestUserChatRooms(user.getUid(), new DataCallback<ChatRoom>() {
            @Override
            public void onResults(@NonNull List<ChatRoom> items) {
                updateUserInUserRooms(user, items);
            }

            @Override
            public void onResult(@NonNull ChatRoom item) {
                // non used
            }
        });
    }

    private void updateUserInUserRooms(final @NonNull User user,
                                       final @NonNull List<ChatRoom> chatRooms) {

        for (final ChatRoom room : chatRooms) {
            Database.rooms().child(room.getRoomId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ChatRoom userRoom = dataSnapshot.getValue(ChatRoom.class);
                    if (userRoom == null) {
                        return ;
                    }

                    // 사용자 정보 업데이트 필요)
                    for (User member : userRoom.getMembers()) {
                        if (member.getUid().equals(user.getUid())) {
                            // 내가 속한 채팅방 내의 사용자 정보 중 나의 정보를 최신 정보로 업데이트.
                            member.update(user);
                            break;
                        }
                    }
                    // 변경사항을 실제 디비에 반영
                    Database.rooms().child(room.getRoomId()).setValue(userRoom);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void loadRealRooms(List<String> roomIds, final DataCallback<ChatRoom> callback) {
        if (roomIds.isEmpty()) {
            // complete
            return ;
        }

        final LinkedList<String> ids = new LinkedList<>(roomIds);
        final List<ChatRoom> chatRooms = new ArrayList<>();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (ids.isEmpty()) {
                    callback.onResults(chatRooms);
                    return;
                }

                String roomId = ids.pollFirst();
                Database.rooms().child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ChatRoom room = dataSnapshot.getValue(ChatRoom.class);
                        if (room != null) {
                            chatRooms.add(room);
                        }
                        if (!ids.isEmpty()) {
                            run();
                        } else {
                            callback.onResults(chatRooms);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        runnable.run();

    }

    public void requestUserChatRooms(final @NonNull String uid,
                                     final @NonNull DataCallback<ChatRoom> callback) {

        Database.userRooms().child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> roomIds = new ArrayList<>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    roomIds.add(snapshot.getValue(String.class));
                }

                List<ChatRoom> outChatRooms = new ArrayList<>();
                loadRealRooms(roomIds, callback);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
