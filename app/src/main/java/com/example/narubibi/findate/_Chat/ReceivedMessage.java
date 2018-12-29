package com.example.narubibi.findate._Chat;

public class ReceivedMessage extends Message {
    private String userName;
    private String profileImageUrl;

    public ReceivedMessage(String chatId, String sentUserId, String userName, String profileImageUrl, String message, String sentTime) {
        super(chatId, sentUserId, message, sentTime);
        this.userName = userName;
        this.profileImageUrl = profileImageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public int getMessageType() {
        return 0;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
