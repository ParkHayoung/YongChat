package com.example.hayoung.yongchat.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hayoung on 2017. 11. 5..
 */

public class ChatRoom implements Parcelable {

    private String roomId; // 채팅방 아이디
    private List<User> members; // 채팅방 멤버들
    private String message; // 채팅방 마지막 메시지
    private long messageCreatedAt; // 채팅방 마지막 메시지가 작성된 시간
    private String tag; // 채팅방 태그

    public String getRoomTitle(@NonNull User me) {
        if (members == null || members.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (User user : members) {
            if (!user.getUid().equals(me.getUid())) {
                sb.append(user.getName());
                sb.append(',');
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getMessageCreatedAt() {
        return messageCreatedAt;
    }

    public void setMessageCreatedAt(long messageCreatedAt) {
        this.messageCreatedAt = messageCreatedAt;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.members);
        dest.writeString(this.message);
        dest.writeLong(this.messageCreatedAt);
        dest.writeString(this.tag);
        dest.writeString(this.roomId);
    }

    public ChatRoom() {
    }

    protected ChatRoom(Parcel in) {
        this.members = new ArrayList<>();
        in.readList(this.members, User.class.getClassLoader());
        this.message = in.readString();
        this.messageCreatedAt = in.readLong();
        this.tag = in.readString();
        this.roomId = in.readString();
    }

    public static final Creator<ChatRoom> CREATOR = new Creator<ChatRoom>() {
        @Override
        public ChatRoom createFromParcel(Parcel source) {
            return new ChatRoom(source);
        }

        @Override
        public ChatRoom[] newArray(int size) {
            return new ChatRoom[size];
        }
    };
}
