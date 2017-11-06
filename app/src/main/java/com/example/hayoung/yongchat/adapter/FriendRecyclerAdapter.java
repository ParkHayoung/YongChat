package com.example.hayoung.yongchat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hayoung on 2017. 11. 6..
 */

public class FriendRecyclerAdapter extends RecyclerView.Adapter<FriendRecyclerAdapter.FriendViewHolder> {


    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {

        public FriendViewHolder(View itemView) {
            super(itemView);
        }
    }


}
