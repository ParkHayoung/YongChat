package com.example.hayoung.yongchat.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.hayoung.yongchat.R;
import com.example.hayoung.yongchat.model.ChatRoom;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {

    public static final String EXTRA_KEY_CHAT_ROOM = "chatRoom";

    private ChatRoom mChatRoom;
    private DatabaseReference mRoomsRef;
    private boolean mRoomExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatRoom = getIntent().getParcelableExtra(EXTRA_KEY_CHAT_ROOM);
        mRoomsRef = FirebaseDatabase.getInstance().getReference("rooms");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView userNameTextView = (TextView) findViewById(R.id.user_name_text_view);
        userNameTextView.setText(mChatRoom.getMembers().get(0).getName());
    }

    private void sendTextMessage(String message) {
        if (mChatRoom.getRoomId() == null) {
            // 2-1. room 이 없으면 생성 후 메시지 전송
        }


        // 2-2 room 이 있으면 바로 메시지 전송

    }

    private void createChatRoom() {
        String roomId = mRoomsRef.push().getKey();
        mChatRoom.setRoomId(roomId);
        mRoomsRef.child(roomId).setValue(mChatRoom);
    }
}
