package com.example.narubibi.findate._Chat;

public class SentSticker extends Message {
    public SentSticker(String chatId, String sentUserId, String message, String sentTime) {
        super(chatId, sentUserId, message, sentTime);
    }

    @Override
    public int getMessageType() {
        return 2;
    }
}
