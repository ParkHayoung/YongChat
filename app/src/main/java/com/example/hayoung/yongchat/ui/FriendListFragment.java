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

import com.example.hayoung.yongchat.R;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendListFragment extends Fragment {
    private FloatingActionButton mFab;


    public FriendListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend, container, false);
        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mFab.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
        mFab.setRippleColor(ContextCompat.getColor(getApplicationContext(), R.color.fabRipple));
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = new CustomDialog(getActivity(), new CustomDialog.CustomDialogClickListener() {
                    @Override
                    public void onDialogSearchButtonClick(String text) {
                        searchAndAddFriend();
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

    private void searchAndAddFriend() {

    }


}
