package com.example.hayoung.yongchat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hayoung.yongchat.R;
import com.example.hayoung.yongchat.model.User;

import java.util.List;

/**
 * Created by hayoung on 2017. 11. 6..
 */

public class FriendRecyclerAdapter extends RecyclerView.Adapter<FriendRecyclerAdapter.FriendViewHolder> {

    private List<User> items;

    public List<User> getItems() {
        return items;
    }

    public void setItems(List<User> items) {
        this.items = items;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        User user = items.get(position);
        holder.nameTextView.setText(user.getName());
        holder.emailTextView.setText(user.getEmail());
        
        Glide.with(holder.profileImageView)
                .load(user.getImageUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.profileImageView);
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {

        private ImageView profileImageView;
        private TextView nameTextView;
        private TextView emailTextView;

        public FriendViewHolder(View itemView) {
            super(itemView);

            this.profileImageView = (ImageView)itemView.findViewById(R.id.profile_image_view);
            this.nameTextView = (TextView)itemView.findViewById(R.id.name_text_view);
            this.emailTextView = (TextView)itemView.findViewById(R.id.email_text_view);
        }
    }


}
