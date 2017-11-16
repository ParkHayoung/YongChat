package com.example.hayoung.yongchat.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.example.hayoung.yongchat.service.UserService;
import com.example.hayoung.yongchat.session.UserSession;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatRoomListFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RoomRecyclerAdapter mRoomRecyclerAdapter;
    private List<ChatRoom> rooms;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRoomRecyclerAdapter = new RoomRecyclerAdapter();
        rooms = new ArrayList<>();
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

    @Override
    public void onResume() {
        super.onResume();
        loadUserRooms();
    }

    private void loadUserRooms() {
        new UserService().requestUserChatRooms(
                UserSession.getInstance().getCurrentUser().getUid(),
                new UserService.DataCallback<ChatRoom>() {
                    @Override
                    public void onResults(@NonNull List<ChatRoom> items) {
                        mRoomRecyclerAdapter.setItems(items);
                        mRoomRecyclerAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onResult(@NonNull ChatRoom item) {
                        // non used
                    }
                });
    }

    private void goToChatRoom(ChatRoom chatRoom) {
        Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
        chatIntent.putExtra(ChatActivity.EXTRA_KEY_CHAT_ROOM, chatRoom);
        startActivity(chatIntent);
    }
}
