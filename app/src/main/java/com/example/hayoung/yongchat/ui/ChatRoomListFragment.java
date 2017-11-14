package com.example.hayoung.yongchat.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hayoung.yongchat.R;
import com.example.hayoung.yongchat.adapter.RoomRecyclerAdapter;
import com.example.hayoung.yongchat.listener.RecyclerItemClickListener;
import com.example.hayoung.yongchat.model.ChatRoom;
import com.example.hayoung.yongchat.model.User;
import com.example.hayoung.yongchat.session.UserSession;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatRoomListFragment extends Fragment {
    private FirebaseDatabase mDb;
    private DatabaseReference mRoomsRef;
    private RecyclerView mRecyclerView;
    private RoomRecyclerAdapter mRoomRecyclerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDb = FirebaseDatabase.getInstance();
        mRoomsRef = mDb.getReference("rooms");
        mRoomRecyclerAdapter = new RoomRecyclerAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mRoomRecyclerAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        loadRooms();

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ChatRoom chatRoom = mRoomRecyclerAdapter.getChatRoom(position);
                        goToChatRoom(chatRoom);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );
    }

    private void goToChatRoom(ChatRoom chatRoom) {
        Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
        chatIntent.putExtra(ChatActivity.EXTRA_KEY_CHAT_ROOM, chatRoom);
        startActivity(chatIntent);
    }

    private void loadRooms() {
        User me = UserSession.getInstance().getCurrentUser();

        mRoomsRef.orderByChild("userId").equalTo(me.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ChatRoom> chatRoomList = new ArrayList<>();

                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    ChatRoom chatRoom = iterator.next().getValue(ChatRoom.class);
                    chatRoomList.add(chatRoom);
                }

                 mRoomRecyclerAdapter.setItems(chatRoomList);
                 mRoomRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
