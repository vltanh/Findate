package com.example.narubibi.findate._Chat;

public class ReceivedSticker extends ReceivedMessage {
    public ReceivedSticker(String chatId, String sentUserId, String userName, String profileImageUrl, String message, String sentTime) {
        super(chatId, sentUserId, userName, profileImageUrl, message, sentTime);
    }

    @Override
    public int getMessageType() {
        return 3;
    }
}
