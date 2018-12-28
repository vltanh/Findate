package com.example.narubibi.findate._User;

public class User {
    private String userId;
    private String name;
    private String sex;
    private String preference;
    private String profileImageUrl;

    public User(String userId, String name, String sex, String preference, String profileImageUrl) {
        this.userId = userId;
        this.name = name;
        this.sex = sex;
        this.preference = preference;
        this.profileImageUrl = profileImageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }
}
