package com.example.narubibi.findate._Chat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Message {
    private String chatId, userId, message, timestamp;

    public Message(String chatId, String sentUserId, String message, String sentTime) {
        this.chatId = chatId;
        this.userId = sentUserId;
        this.message = message;
        this.timestamp = sentTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        DateFormat oldDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        DateFormat newDateFormat = new SimpleDateFormat("MMM dd HH:mm");
        try {
            return newDateFormat.format(oldDateFormat.parse(timestamp));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getMessageType() {
        return 1;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
