package com.example.hayoung.yongchat.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hayoung.yongchat.R;
import com.example.hayoung.yongchat.adapter.ChatRecyclerAdapter;
import com.example.hayoung.yongchat.api.FcmApi;
import com.example.hayoung.yongchat.api.FcmSendMessageBody;
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
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {

    public static final String EXTRA_KEY_CHAT_ROOM = "chatRoom";
    private static final String FCM_API_KEY = "AIzaSyAaWYAlTEt3d_5y7gm3NNtltWplDoeXTgo";

    private ChatRoom mMyRoom;
    private DatabaseReference mRoomsRef;
    private DatabaseReference mMessageRef;
    private EditText mChatEditText;
    private ChatRecyclerAdapter mChatRecyclerAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mMyRoom = getIntent().getParcelableExtra(EXTRA_KEY_CHAT_ROOM);
        mRoomsRef = FirebaseDatabase.getInstance().getReference("rooms");
        mMessageRef = FirebaseDatabase.getInstance().getReference("message");
        mChatRecyclerAdapter = new ChatRecyclerAdapter();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.chat_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        mRecyclerView.setAdapter(mChatRecyclerAdapter);

        TextView userNameTextView = (TextView) findViewById(R.id.user_name_text_view);
        userNameTextView.setText(mMyRoom.getTitle());

        mChatEditText = (EditText) findViewById(R.id.chat_edit_text);
        Button sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mChatEditText.getText().length() > 0) {
                    sendTextMessage(mChatEditText.getText().toString());
                    mChatEditText.setText("");
                } else {
                    return;
                }
                loadChat();
            }
        });

        loadChat();
    }

    private void sendTextMessage(String message) {
        final User me = UserSession.getInstance().getCurrentUser();
        if (mMyRoom.getRoomId() == null) {
            // room 이 없으면 생성 후 메시지 전송
            createChatRoom();
        }
        // room 이 있으면 바로 메시지 전송
        TextMessage textMessage = new TextMessage();
        textMessage.setMessage(message);
        textMessage.setSent(me);
        textMessage.setDate(new Date());
        textMessage.setUnreadCount(mMyRoom.getMembers().size());
        mMessageRef.child(mMyRoom.getRoomId()).push().setValue(textMessage);

        // 내 방 정보 업데이트
        mMyRoom.setMessage(message);
        mMyRoom.setDateTime(textMessage.getDate());
        mRoomsRef.child(mMyRoom.getRoomId()).setValue(mMyRoom);

        // 메시지를 받을 친구들이 나와의 채팅방이 개설되어있지 않다면 새롭게 만들고
        // 메시지를 전달
        createFriendsChatRoomIfNotExist(textMessage);
    }

    private void createFriendsChatRoomIfNotExist(final TextMessage textMessage) {
        final User me = UserSession.getInstance().getCurrentUser();
        List<User> friends = mMyRoom.getMembers();
        for (final User friend : friends) {
            if (friend.getUid().equals(me.getUid())) {
                continue;
            }

            mRoomsRef.orderByChild("userId").equalTo(friend.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean found = false;
                    ChatRoom room = null;
                    if (dataSnapshot.getChildrenCount() > 0) {
                        Iterator<DataSnapshot> itemSnapshot = dataSnapshot.getChildren().iterator();
                        while(itemSnapshot.hasNext()) {
                            room = itemSnapshot.next().getValue(ChatRoom.class);
                            if (room != null && me.getUid().equals(room.getTag())) {
                                // 이미 나와의 일댈 챗방이 생성되어있음
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        // 생성되어있는 채팅방이 없으므로 새로 생성해야함.
                        room = new ChatRoom();
                        room.setMessage(textMessage.getMessage());
                        room.setDateTime(textMessage.getDate());
                        room.setTag(me.getUid());
                        room.setUserId(friend.getUid());
                        room.setMembers(mMyRoom.getMembers());
                        room.setTitle(me.getName());
                        room.setUnreadCount(0);
                        room.setDateTime(new Date());

                        String roomId = mRoomsRef.push().getKey();
                        room.setRoomId(roomId);
                        mRoomsRef.child(roomId).setValue(room);

                        // 상대방에게 메시지를 전달
                        mMessageRef.child(room.getRoomId()).push().setValue(textMessage);

                    } else {

                        // 상대방에게 메시지를 전달
                        mMessageRef.child(room.getRoomId()).push().setValue(textMessage);

                        // 상대방의 채팅방 정보를 갱신
                        room.setMessage(textMessage.getMessage());
                        room.setDateTime(textMessage.getDate());

                        mRoomsRef.child(room.getRoomId()).setValue(room);
                    }

                    sendFcmToRecipients(textMessage);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void sendFcmToRecipients(@NonNull TextMessage textMessage) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://fcm.googleapis.com")
                .build();


        User me = UserSession.getInstance().getCurrentUser();
        List<User> recipients = new ArrayList<>();
        for (User user: mMyRoom.getMembers()) {
            if (!user.getUid().equals(me.getUid())) {
                recipients.add(user);
            }
        }
        FcmSendMessageBody body = new FcmSendMessageBody(
                mMyRoom.getRoomId(),
                me,
                recipients,
                textMessage);

        FcmApi api = retrofit.create(FcmApi.class);
        Call<ResponseBody> call = api.sendMessage("key=" + FCM_API_KEY, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody body = response.body();
                    if (body != null) {
                        Log.e(ChatActivity.class.getSimpleName(), body.string());
                    }
                } catch (Exception e) {
                    Log.e(ChatActivity.class.getSimpleName(), e.getMessage(), e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(ChatActivity.class.getSimpleName(), t.getMessage(), t);
            }
        });
    }

    private void createChatRoom() {
        String roomId = mRoomsRef.push().getKey();
        mMyRoom.setRoomId(roomId);
        mRoomsRef.child(roomId).setValue(mMyRoom);
        loadChat();
    }

    private void loadChat() {
        String roomId = mMyRoom.getRoomId();
        if (roomId == null) {
            return ;
        }

        mMessageRef.child(roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<TextMessage> messageList = new ArrayList<>();
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while(iterator.hasNext()) {
                    TextMessage textMessage = iterator.next().getValue(TextMessage.class);
                    messageList.add(textMessage);
                }

                Collections.reverse(messageList);
                mChatRecyclerAdapter.setItems(messageList);
                mChatRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
