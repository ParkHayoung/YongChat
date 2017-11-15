package com.example.hayoung.yongchat.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hayoung.yongchat.R;
import com.example.hayoung.yongchat.model.TextMessage;
import com.example.hayoung.yongchat.model.User;
import com.example.hayoung.yongchat.session.UserSession;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hayoung on 2017. 11. 5..
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatViewHolder> {

    private static final int TYPE_TM_ME = 0;
    private static final int TYPE_TM_YOU = 1;

    private List<TextMessage> items = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    private User me;

    public ChatRecyclerAdapter() {
        me = UserSession.getInstance().getCurrentUser();
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = null;
        if (viewType == TYPE_TM_ME) {
            view = inflater.inflate(R.layout.item_chat_tm_me, parent, false);
        } else if (viewType == TYPE_TM_YOU) {
            view = inflater.inflate(R.layout.item_chat_tm_you, parent, false);
        }
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        TextMessage textMessage = items.get(position);

        holder.dateTextView.setText(dateFormat.format(new Date(textMessage.getCreatedAt())));
        holder.messageTextView.setText(textMessage.getMessage());
        holder.nameTextView.setText(textMessage.getSent().getName());

        Glide.with(holder.profileImageView)
                .load(items.get(position).getSent().getImageUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.profileImageView);
    }

    @Override
    public int getItemViewType(int position) {
        TextMessage textMessage = items.get(position);
        if (textMessage.getSent().getUid().equals(me.getUid())) {
            return TYPE_TM_ME;
        } else {
            return TYPE_TM_YOU;
        }
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
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

    public void setItems(@NonNull List<TextMessage> items) {
        this.items = items;
    }

    public List<TextMessage> getItems() {
        return items;
    }
}
