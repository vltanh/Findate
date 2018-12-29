package com.example.narubibi.findate._Chat;

public class ReceivedInvitation extends ReceivedMessage {
    private String invitationId;
    private String time, place, status;

    public ReceivedInvitation(String chatId, String invitationId, String sentUserId, String userName, String profileImageUrl, String message, String sentTime, String time, String place, String status) {
        super(chatId, sentUserId, userName, profileImageUrl, message, sentTime);
        this.time = time;
        this.place = place;
        this.status = status;
        this.invitationId = invitationId;
    }

    @Override
    public int getMessageType() {
        return 4;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(String invitationId) {
        this.invitationId = invitationId;
    }
}
