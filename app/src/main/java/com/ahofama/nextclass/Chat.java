package com.ahofama.nextclass;

import java.util.HashMap;
import java.util.Map;

public class Chat {
    private String id;  // chatId from Firebase
    private Map<String, Boolean> participants; // userId → true
    private String lastMessage;
    private long timestamp;

    // Required empty constructor for Firebase
    public Chat() {}

    public Chat(String id, Map<String, Boolean> participants, String lastMessage, long timestamp) {
        this.id = id;
        this.participants = participants;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    // ✅ Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Boolean> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<String, Boolean> participants) {
        this.participants = participants;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // 👇 Optional helper for showing in Inbox
    public String getName() {
        if (participants == null) return "Unknown Chat";
        // for now just show chatId (you can later resolve usernames from Firebase)
        return id != null ? id : "Unnamed Chat";
    }
}
