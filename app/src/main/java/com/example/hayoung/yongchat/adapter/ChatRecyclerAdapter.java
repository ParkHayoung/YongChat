package com.example.hayoung.yongchat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hayoung.yongchat.R;
import com.example.hayoung.yongchat.model.TextMessage;

import org.w3c.dom.Text;

/**
 * Created by hayoung on 2017. 11. 5..
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatViewHolder> {


    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_chat_left, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        private ImageView profileImageView;
        private TextView nameTextView;
        private TextView messageTextView;
        private TextView unreadCountTextView;
        private TextView dateTextView;

        public ChatViewHolder(View itemView) {
            super(itemView);

            profileImageView = (ImageView) itemView.findViewById(R.id.profile_image_view);
            nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
            messageTextView = (TextView) itemView.findViewById(R.id.message_text_view);
            unreadCountTextView = (TextView) itemView.findViewById(R.id.unread_count_text_view);
            dateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
        }
    }
}
