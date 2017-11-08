package com.example.hayoung.yongchat.ui;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hayoung.yongchat.R;
import com.example.hayoung.yongchat.adapter.ChatRecyclerAdapter;
import com.example.hayoung.yongchat.model.ChatRoom;
import com.example.hayoung.yongchat.model.TextMessage;
import com.example.hayoung.yongchat.model.User;
import com.example.hayoung.yongchat.session.UserSession;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    public static final String EXTRA_KEY_CHAT_ROOM = "chatRoom";

    private ChatRoom mChatRoom;
    private DatabaseReference mRoomsRef;
    private DatabaseReference mMessageRef;
    private EditText mChatEditText;
    private ChatRecyclerAdapter mChatRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatRoom = getIntent().getParcelableExtra(EXTRA_KEY_CHAT_ROOM);
        mRoomsRef = FirebaseDatabase.getInstance().getReference("rooms");
        mMessageRef = FirebaseDatabase.getInstance().getReference("message");
        mChatRecyclerAdapter = new ChatRecyclerAdapter();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView userNameTextView = (TextView) findViewById(R.id.user_name_text_view);
        userNameTextView.setText(mChatRoom.getMembers().get(0).getName());

        mChatEditText = (EditText) findViewById(R.id.chat_edit_text);
        Button sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mChatEditText.getText().length() > 0) {
                    sendTextMessage(mChatEditText.getText().toString());
                    mChatEditText.setText("");
//                    loadChat();
                } else {
                    return;
                }
//                loadChat(mChatRoom.getRoomId());
                loadChat();
            }
        });

    }

    private void sendTextMessage(String message) {
        final User me = UserSession.getInstance().getCurrentUser();
        if (mChatRoom.getRoomId() == null) {
            // 2-1. room 이 없으면 생성 후 메시지 전송
            createChatRoom();
        }
        // 2-2 room 이 있으면 바로 메시지 전송

        TextMessage textMessage = new TextMessage();

        textMessage.setRoomId(mChatRoom.getRoomId());
        textMessage.setMessage(message);
        textMessage.setSent(me);
        textMessage.setDate(new Date());
        textMessage.setUnreadCount(mChatRoom.getMembers().size());
        mMessageRef.push().setValue(textMessage);

//        loadChat(mChatRoom.getRoomId());
    }

    private void createChatRoom() {
        String roomId = mRoomsRef.push().getKey();
        mChatRoom.setRoomId(roomId);
        mRoomsRef.child(roomId).setValue(mChatRoom);
    }

    private void loadChat() {
        String roomId = mChatRoom.getRoomId();
        mMessageRef.orderByChild("roomId").equalTo(roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<TextMessage> messageList = new ArrayList<>();
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while(iterator.hasNext()) {
                    TextMessage textMessage = iterator.next().getValue(TextMessage.class);
                    messageList.add(textMessage);
                }

                mChatRecyclerAdapter.setItems(messageList);
                mChatRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
