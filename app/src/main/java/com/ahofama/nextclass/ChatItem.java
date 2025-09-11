package com.ahofama.nextclass;

public class ChatItem {
    private String userName;
    private String lastMessage;
    private String time;
    private int avatarResId; // drawable resource for avatar

    public ChatItem(String userName, String lastMessage, String time, int avatarResId) {
        this.userName = userName;
        this.lastMessage = lastMessage;
        this.time = time;
        this.avatarResId = avatarResId;
    }

    public String getUserName() {
        return userName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getTime() {
        return time;
    }

    public int getAvatarResId() {
        return avatarResId;
    }
}
