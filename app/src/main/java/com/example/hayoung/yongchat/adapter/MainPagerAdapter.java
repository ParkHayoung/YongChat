package com.example.hayoung.yongchat.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.hayoung.yongchat.ui.ChatRoomListFragment;
import com.example.hayoung.yongchat.ui.FriendListFragment;

/**
 * Created by hayoung on 2017. 11. 5..
 */

public class MainPagerAdapter extends FragmentPagerAdapter {

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new ChatRoomListFragment();
        } else {
            return new FriendListFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Rooms";
        } else {
            return "Friends";
        }
    }
}
