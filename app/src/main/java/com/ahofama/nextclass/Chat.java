package com.ahofama.nextclass;

public class Chat {
    private String name;
    private String lastMessage;
    private long timestamp;

    public Chat(String name, String lastMessage, long timestamp) {
        this.name = name;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public String getName() { return name; }
    public String getLastMessage() { return lastMessage; }
    public long getTimestamp() { return timestamp; }
}
