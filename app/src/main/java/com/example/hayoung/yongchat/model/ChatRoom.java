package com.example.hayoung.yongchat.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hayoung on 2017. 11. 5..
 */

public class ChatRoom implements Parcelable {

    String roomId;
    String title;
    List<User> members;
    String message;
    int unreadCount;
    long messageCreatedAt;
    String userId;
    String tag;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public long getMessageCreatedAt() {
        return messageCreatedAt;
    }

    public void setMessageCreatedAt(long messageCreatedAt) {
        this.messageCreatedAt = messageCreatedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
        dest.writeInt(this.unreadCount);
        dest.writeLong(this.messageCreatedAt);
        dest.writeString(this.userId);
        dest.writeString(this.tag);
        dest.writeString(this.roomId);
        dest.writeString(this.title);
    }

    public ChatRoom() {
    }

    protected ChatRoom(Parcel in) {
        this.members = new ArrayList<>();
        in.readList(this.members, User.class.getClassLoader());
        this.message = in.readString();
        this.unreadCount = in.readInt();
        this.messageCreatedAt = in.readLong();
        this.userId = in.readString();
        this.tag = in.readString();
        this.roomId = in.readString();
        this.title = in.readString();
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
