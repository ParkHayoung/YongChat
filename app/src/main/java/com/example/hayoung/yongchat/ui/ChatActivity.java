package com.example.hayoung.yongchat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.example.hayoung.yongchat.service.DataCallback;
import com.example.hayoung.yongchat.service.RoomService;
import com.example.hayoung.yongchat.session.UserSession;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
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
    private TextView mTitleTextView;

    private Retrofit retrofit;
    private FcmApi api;
    private boolean readyForNewMessage;
    private ChildEventListener newMessagesEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 사용자 세션이 없다면 (로그아웃 상태),
        // 현재화면 종료하고 로그인화면으로 보내기
        if (!UserSession.getInstance().isLoggedIn()) {
            LoginActivity.start(this);
            finish();
            return;
        }

        setContentView(R.layout.activity_chat);

        initRetrofitService();

        mRoom = getIntent().getParcelableExtra(EXTRA_KEY_CHAT_ROOM);
        mChatRecyclerAdapter = new ChatRecyclerAdapter();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.chat_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        mRecyclerView.setAdapter(mChatRecyclerAdapter);

        mTitleTextView = (TextView) findViewById(R.id.user_name_text_view);
        mTitleTextView.setText(mRoom.getRoomTitle(UserSession.getInstance().getCurrentUser()));

        mChatEditText = (EditText) findViewById(R.id.chat_edit_text);
        Button sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mChatEditText.getText().length() > 0) {
                    sendTextMessage(mChatEditText.getText().toString());
                    mChatEditText.setText("");
                }
            }
        });

        loadMessages();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleOpenedThroughNotification(intent);
    }

    private void unsubscribeNewMessages() {
        if (mRoom != null && newMessagesEventListener != null) {
            Database.messages().child(mRoom.getRoomId()).removeEventListener(newMessagesEventListener);
        }

    }

    private void handleOpenedThroughNotification(Intent intent) {
        String roomId = intent.getStringExtra("room_id");
        if (TextUtils.isEmpty(roomId)) {
            return ;
        }

        if (mRoom != null && mRoom.getRoomId().equals(roomId)) {
            return ;
        }

        unsubscribeNewMessages();
        mTitleTextView.setText(null);
        mChatRecyclerAdapter.getItems().clear();
        mChatRecyclerAdapter.notifyDataSetChanged();

        // Notification 을 터치해서 진입함
        new RoomService().requestRoom(roomId, new DataCallback<ChatRoom>() {
            @Override
            public void onResults(@NonNull List<ChatRoom> items) {
                // non used
            }

            @Override
            public void onResult(@NonNull ChatRoom item) {
                mRoom = item;

                Intent baseIntent = getIntent();
                baseIntent.putExtra(EXTRA_KEY_CHAT_ROOM, mRoom);
                setIntent(baseIntent);

                mTitleTextView.setText(mRoom.getRoomTitle(UserSession.getInstance().getCurrentUser()));
                loadMessages();
            }
        });
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
                    loadMessages();
                    sendTextMessage(message);
                }
            });
            return;
        }

        User me = UserSession.getInstance().getCurrentUser();

        // room 이 있으면 바로 메시지 전송
        final TextMessage textMessage = new TextMessage();
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
    }


    private void requestPostFcmSend(@NonNull TextMessage textMessage, User recipient) {
        User me = UserSession.getInstance().getCurrentUser();
        final FcmSendMessageBody body = new FcmSendMessageBody(mRoom, me, recipient, textMessage);

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
        User me = UserSession.getInstance().getCurrentUser();
        for (int i = 0; i < mRoom.getMembers().size(); i++) {
            String uid = mRoom.getMembers().get(i).getUid();
            if (uid.equals(me.getUid())) {
                continue;
            }

            Database.users().child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        requestPostFcmSend(textMessage, user);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
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
    }

    private void subscribeNewMessages() {
        String roomId = mRoom.getRoomId();
        if (roomId == null) {
            return ;
        }

        newMessagesEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!readyForNewMessage) {
                    return ;
                }

                LinkedList<TextMessage> items = mChatRecyclerAdapter.getItems();
                items.addFirst(dataSnapshot.getValue(TextMessage.class));
                mChatRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // non used
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // non used
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // non used
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // non used
            }
        };

        Database.messages()
                .child(roomId)
                .limitToLast(1)
                .addChildEventListener(newMessagesEventListener);
    }

    @Override
    public void finish() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(mainIntent);
    }

    private void loadMessages() {
        String roomId = mRoom.getRoomId();
        if (roomId == null) {
            return ;
        }

        readyForNewMessage = false;
        subscribeNewMessages();
        Database.messages().child(roomId).limitToLast(300).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LinkedList<TextMessage> items = mChatRecyclerAdapter.getItems();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    items.addFirst(snapshot.getValue(TextMessage.class));
                }
                mChatRecyclerAdapter.notifyDataSetChanged();
                readyForNewMessage = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // non used
            }
        });


    }

}
