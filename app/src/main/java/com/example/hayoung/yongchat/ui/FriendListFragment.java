package com.example.hayoung.yongchat.ui;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hayoung.yongchat.R;
import com.example.hayoung.yongchat.adapter.FriendRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendListFragment extends Fragment {
    private FloatingActionButton mFab;
    private CustomDialog dialog;

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mDb;
    private DatabaseReference mUserRef;

    private FriendRecyclerAdapter mFriendRecyclerAdapter;

    private TextView test;

    public FriendListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mDb = FirebaseDatabase.getInstance();
        mUserRef = mDb.getReference("users");
        mFriendRecyclerAdapter = new FriendRecyclerAdapter();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        test = (TextView) view.findViewById(R.id.test);
        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mFab.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
        mFab.setRippleColor(ContextCompat.getColor(getApplicationContext(), R.color.fabRipple));


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomDialog dialog = new CustomDialog(getActivity(), new CustomDialog.CustomDialogClickListener() {
                    @Override
                    public void onDialogSearchButtonClick(String email) {
                        if(email.equals(mFirebaseUser.getEmail())) {
                            Toast.makeText(getContext(), "자신은 친구로 등록할 수 없습니다.", Toast.LENGTH_SHORT).show();
                            //dialog 안 꺼지도록

                        } else {
                            //친구 등록 기능 구현
                            searchAndAddFriend(email);
                        }
                    }

                    @Override
                    public void onDialogCancelButtonClick() {
                    }
                });
                dialog.show();
            }
        });
        return view;
    }

    private void searchAndAddFriend(String email) {
        mUserRef.orderByChild("email").equalTo(email.trim().toLowerCase()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    Toast.makeText(getContext(), "없당", Toast.LENGTH_SHORT).show();
                    return ;
                }

                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    Toast.makeText(getContext(), "있당", Toast.LENGTH_SHORT).show();
//                    test.setText("있당");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }


}
