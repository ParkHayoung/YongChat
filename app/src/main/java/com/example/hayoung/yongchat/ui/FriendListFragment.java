package com.example.hayoung.yongchat.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hayoung.yongchat.R;
import com.example.hayoung.yongchat.adapter.FriendRecyclerAdapter;
import com.example.hayoung.yongchat.db.Database;
import com.example.hayoung.yongchat.listener.RecyclerItemClickListener;
import com.example.hayoung.yongchat.model.ChatRoom;
import com.example.hayoung.yongchat.model.User;
import com.example.hayoung.yongchat.session.UserSession;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendListFragment extends Fragment {

    private FriendRecyclerAdapter mFriendRecyclerAdapter;

    public FriendListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFriendRecyclerAdapter = new FriendRecyclerAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friend, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
        mFab.setRippleColor(ContextCompat.getColor(getApplicationContext(), R.color.fabRipple));
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomDialog dialog = new CustomDialog(getActivity(), new CustomDialog.CustomDialogClickListener() {
                    @Override
                    public void onDialogSearchButtonClick(String email) {
                        User me = UserSession.getInstance().getCurrentUser();
                        if (me == null) {
                            return ;
                        }

                        if(email.equals(me.getEmail())) {
                            Toast.makeText(getContext(), "자신은 친구로 등록할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            //친구 등록 기능 구현
                            searchAndAddFriend(email);
                        }
                    }

                    @Override
                    public void onDialogCancelButtonClick() {
                    }
                });
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mFriendRecyclerAdapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final User friend = mFriendRecyclerAdapter.getUser(position);
                        Snackbar.make(view, friend.getName() + "님 과 대화하시겠습니까?", Snackbar.LENGTH_SHORT)
                                .setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //ChatActivity 로 이동
                                        startChat(friend);
                                    }
                                }).show();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                })
        );

        loadFriendList();
    }

    private void startChat(final User friend) {
        User me = UserSession.getInstance().getCurrentUser();

        // 새로운 채팅방을 생성
//        Map<String, Boolean> roomMembers = new HashMap<>();
//        roomMembers.put(friend.getUid(), true);
//        roomMembers.put(me.getUid(),true);

        List<User> roomMembers = new ArrayList<>();
        roomMembers.add(me);
        roomMembers.add(friend);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setMembers(roomMembers);
        chatRoom.setTitle(friend.getName());
        chatRoom.setMessageCreatedAt(System.currentTimeMillis());
        chatRoom.setTag(friend.getUid());

        Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
        chatIntent.putExtra(ChatActivity.EXTRA_KEY_CHAT_ROOM, chatRoom);
        startActivity(chatIntent);
    }

    private void loadFriendList() {
        User me = UserSession.getInstance().getCurrentUser();
        Database.friends().child(me.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> friendList = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    User friend = dataSnapshot1.getValue(User.class);
                    friendList.add(friend);
                }
                mFriendRecyclerAdapter.setItems(friendList);
                mFriendRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void searchAndAddFriend(String email) {
        Database.users().orderByChild("email").equalTo(email.trim().toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    Toast.makeText(getContext(), "해당 이메일을 가진 사용자가 없습니다.", Toast.LENGTH_SHORT).show();
                    return ;
                }

                User user = dataSnapshot.getChildren().iterator().next().getValue(User.class);
                addUserToMyFriendList(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addUserToMyFriendList(final User user) {
        final User me = UserSession.getInstance().getCurrentUser();

        Database.friends().child(me.getUid()).orderByChild("uid").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    // 친구 추가
                    Toast.makeText(getContext(), "친구 추가가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    Database.friends().child(me.getUid()).push().setValue(user);
                } else {
                    // 이미 친구로 등록되어있음
                    Toast.makeText(getContext(), "이미 친구로 등록된 사용자입니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
