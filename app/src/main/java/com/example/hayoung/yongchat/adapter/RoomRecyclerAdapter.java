package com.example.hayoung.yongchat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hayoung.yongchat.R;
import com.example.hayoung.yongchat.model.ChatRoom;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by hayoung on 2017. 11. 5..
 */

public class RoomRecyclerAdapter extends RecyclerView.Adapter<RoomRecyclerAdapter.RoomViewHolder> {

    private List<ChatRoom> items;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yy/MM/dd HH:mm");

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_room, parent, false);

        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        ChatRoom chatRoom = items.get(position);

        holder.titleTextView.setText(chatRoom.getTitle());
        holder.messageTextView.setText(chatRoom.getMessage());
//        holder.unreadCountTextView.setText(String.valueOf(chatRoom.getUnreadCount()));
        holder.dateTextView.setText(dateFormat.format(chatRoom.getDateTime()));

    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    public ChatRoom getChatRoom(int position) {
        return items.get(position);
    }

    public void setItems(List<ChatRoom> items) {
        this.items = items;
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView messageTextView;
        private TextView unreadCountTextView;
        private TextView dateTextView;


        public RoomViewHolder(View itemView) {
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.title_text_view);
            messageTextView = (TextView) itemView.findViewById(R.id.message_text_view);
            unreadCountTextView = (TextView) itemView.findViewById(R.id.unread_count_text_view);
            dateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
        }
    }
}
