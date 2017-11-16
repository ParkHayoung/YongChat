package com.example.hayoung.yongchat.ui;

import android.os.Bundle;
import android.os.Handler;
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
import com.example.hayoung.yongchat.db.Database;
import com.example.hayoung.yongchat.model.ChatRoom;
import com.example.hayoung.yongchat.model.TextMessage;
import com.example.hayoung.yongchat.model.User;
import com.example.hayoung.yongchat.session.UserSession;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.hayoung.yongchat.db.Database.rooms;

public class ChatActivity extends AppCompatActivity {

    public static final String EXTRA_KEY_CHAT_ROOM = "chatRoom";
    private static final String FCM_API_KEY = "AIzaSyAaWYAlTEt3d_5y7gm3NNtltWplDoeXTgo";

    private ChatRoom mRoom;
    private EditText mChatEditText;
    private ChatRecyclerAdapter mChatRecyclerAdapter;
    private RecyclerView mRecyclerView;

    private Retrofit retrofit;
    private FcmApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initRetrofitService();

        mRoom = getIntent().getParcelableExtra(EXTRA_KEY_CHAT_ROOM);
        mChatRecyclerAdapter = new ChatRecyclerAdapter();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.chat_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        mRecyclerView.setAdapter(mChatRecyclerAdapter);

        TextView userNameTextView = (TextView) findViewById(R.id.user_name_text_view);
        userNameTextView.setText(mRoom.getTitle());

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

    private void initRetrofitService() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://fcm.googleapis.com")
                .build();
        api = retrofit.create(FcmApi.class);
    }

    private void sendTextMessage(final String message) {
        if (mRoom.getRoomId() == null) {
            // room 이 없으면 생성
            createChatRoom(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    // 채팅메시지 목록 옵저버 등록
                    loadChat();
                    sendTextMessage(message);
                }
            });
            return;
        }

        User me = UserSession.getInstance().getCurrentUser();

        // room 이 있으면 바로 메시지 전송
        TextMessage textMessage = new TextMessage();
        textMessage.setMessage(message);
        textMessage.setSent(me);
        textMessage.setCreatedAt(System.currentTimeMillis());
        textMessage.setUnreadCount(mRoom.getMembers().size() - 1);

        // 채팅방에 메시지 저장
        Database.messages().child(mRoom.getRoomId()).push().setValue(textMessage);

        // 푸시 메시지 발송
        sendFcmToRecipients(textMessage);

        // 내 방 정보 업데이트
        mRoom.setMessage(textMessage.getMessage());
        mRoom.setMessageCreatedAt(textMessage.getCreatedAt());

        Database.rooms().child(mRoom.getRoomId()).setValue(mRoom);


//        // 대화방에 있는 사람들 각각의 채팅방에 메시지를 전송
//        for (final String friendUid : mRoom.getMembers().keySet()) {
//            if (friendUid.equals(me.getUid())) {
//                continue;
//            }
//
//            Database.rooms().child(friendUid).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (!dataSnapshot.exists()) {
//                        // 채팅방 생성
//                        createFriendChatRoomAndSendMessage(friendUid, textMessage);
//                    } else {
//                        sendMessageToRoom()
//                    }
//
//                    // 메시지 전송
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//            ChatRoom friendChatRoom = new ChatRoom();
//            friendChatRoom.setRoomId(roomId);
//            friendChatRoom.setTitle(me.getName());
//            friendChatRoom.setMembers(mRoom.getMembers());
//
//            Database.rooms().child(friendUid).child(roomId).setValue(mRoom);
//        }
//
//
//        Database.messages().child(mRoom.getRoomId()).push().setValue(textMessage);
//



        // 메시지를 받을 친구들이 나와의 채팅방이 개설되어있지 않다면 새롭게 만들고
        // 메시지를 전달
        //createFriendsChatRoomIfNotExist(textMessage);
    }

    private void createFriendChatRoomAndSendMessage(String uid, TextMessage textMessage) {

    }


    /**
     * 채팅방에 있는 사용자들에게 메시지를 보내기 전에
     * 각각의 사용자들의 채팅방이 생성되어있는지 확인하고
     * 생성되어있지 않다면 새롭게 생성한다.
     *
     * @param textMessage 보낼 메시지.
     */
    private void createFriendsChatRoomIfNotExist(final TextMessage textMessage) {
        final User me = UserSession.getInstance().getCurrentUser();

//        List<User> friends = mRoom.getMembers();
//        for (final User user : friends) {
//            if (user.getUid().equals(me.getUid())) {
//                continue;
//            }
//
//            Database.rooms().orderByChild("userId").equalTo(user.getUid())
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    ChatRoom room = null;
//                    for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
//                        ChatRoom chatRoom = itemSnapshot.getValue(ChatRoom.class);
//                        if (chatRoom != null && me.getUid().equals(chatRoom.getTag())) {
//                            // 이미 상대방과 나와의 1:1 채팅이 생성되어있음
//                            room = chatRoom;
//                            break;
//                        }
//                    }
//
//                    if (room == null) {
//                        // 생성되어있는 상대방과 나와의 채팅방이 없으므로 새로 생성해야함.
//                        room = new ChatRoom();
//                        room.setMessage(textMessage.getMessage());
//                        room.setMessageCreatedAt(textMessage.getCreatedAt());
//                        room.setTag(me.getUid());
//                                room.setMembers(mRoom.getMembers());
//                        room.setTitle(me.getName());
//
//                        String roomId = Database.rooms().push().getKey();
//                        room.setRoomId(roomId);
//                        Database.rooms().child(roomId).setValue(room);
//
//                    } else {
//                        // 상대방의 채팅방 정보를 갱신
//                        room.setMessage(textMessage.getMessage());
//                        room.setMessageCreatedAt(textMessage.getCreatedAt());
//                        Database.rooms().child(room.getRoomId()).setValue(room);
//                    }
//
//                    // 상대방에게 메시지를 전달
//                    Database.messages().child(room.getRoomId()).push().setValue(textMessage);
//                    // 푸시 메시지 발송
//                    sendFcmToRecipients(textMessage);
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }
    }

    private void requestPostFcmSend(@NonNull TextMessage textMessage, String token) {
        User me = UserSession.getInstance().getCurrentUser();
        final FcmSendMessageBody body = new FcmSendMessageBody(mRoom.getRoomId(), me, token, textMessage);

        Call<ResponseBody> call = api.sendMessage("key=" + FCM_API_KEY, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
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
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(ChatActivity.class.getSimpleName(), t.getMessage(), t);
            }
        });
    }

    private void sendFcmToRecipients(@NonNull final TextMessage textMessage) {

        for (int i = 0; i < mRoom.getMembers().size(); i++) {
            String uid = mRoom.getMembers().get(i).getUid();

            Database.users().child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        requestPostFcmSend(textMessage, user.getToken());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
//        for (String uid : mRoom.getMembers().keySet()) {
//            Database.users().child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    User user = dataSnapshot.getValue(User.class);
//                    if (user != null) {
//                        requestPostFcmSend(textMessage, user.getToken());
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestPostFcmSend(textMessage, UserSession.getInstance().getCurrentUser().getToken());
            }
        }, 5000);
    }

    private void createChatRoom(DatabaseReference.CompletionListener listener) {
        String roomId = rooms().push().getKey();
        mRoom.setRoomId(roomId);

        // 대화방 생성
        rooms().child(roomId).setValue(mRoom, listener);

        // 사용자 - 대화방 매칭 데이터 생성
        for (int i = 0; i < mRoom.getMembers().size(); i++) {
            String uid = mRoom.getMembers().get(i).getUid();
            Database.userRooms().child(uid).push().setValue(roomId);
        }
//        for (String uid : mRoom.getMembers().keySet()) {
//            Database.userRooms().child(uid).push().setValue(roomId);
//        }
    }

    private void loadChat() {
        String roomId = mRoom.getRoomId();
        if (roomId == null) {
            return ;
        }

        Database.messages().child(roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<TextMessage> messageList = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    TextMessage textMessage = dataSnapshot1.getValue(TextMessage.class);
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
