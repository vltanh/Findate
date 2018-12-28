package com.example.narubibi.findate._Chat;

public class SentSticker extends Message {
    public SentSticker(String sentUserId, String message, String sentTime) {
        super(sentUserId, message, sentTime);
    }

    @Override
    public int getMessageType() {
        return 2;
    }
}
