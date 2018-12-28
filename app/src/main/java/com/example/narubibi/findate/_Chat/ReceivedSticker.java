package com.example.narubibi.findate._Chat;

public class ReceivedSticker extends ReceivedMessage {
    public ReceivedSticker(String sentUserId, String userName, String profileImageUrl, String message, String sentTime) {
        super(sentUserId, userName, profileImageUrl, message, sentTime);
    }

    @Override
    public int getMessageType() {
        return 3;
    }
}
