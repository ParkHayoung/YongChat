package com.example.hayoung.yongchat.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hayoung on 2017. 11. 5..
 */

public class ChatRoom implements Parcelable {

    private String roomId; // 채팅방 아이디
    private String title; // 채팅방 타이틀
    private Map<String, Boolean> members; // 채팅방 멤버들
    private String message; // 채팅방 마지막 메시지
    private long messageCreatedAt; // 채팅방 마지막 메시지가 작성된 시간
    private String tag; // 채팅방 태그

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Boolean> members) {
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
        dest.writeMap(this.members);
        dest.writeString(this.message);
        dest.writeLong(this.messageCreatedAt);
        dest.writeString(this.tag);
        dest.writeString(this.roomId);
        dest.writeString(this.title);
    }

    public ChatRoom() {
    }

    protected ChatRoom(Parcel in) {
        this.members = new HashMap<>();
        in.readMap(this.members, User.class.getClassLoader());
        this.message = in.readString();
        this.messageCreatedAt = in.readLong();
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
