package com.reiserx.nimbleq.Models;

public class UserData {
    String uid, phoneNumber, userName, FCM_TOKEN;
    long created_timestamp, lastLogin_timestamp;
    userDetails userDetails;
    float rating;

    public UserData(String uid, String phoneNumber, String userName, String FCM_TOKEN, long created_timestamp, long lastLogin_timestamp) {
        this.uid = uid;
        this.phoneNumber = phoneNumber;
        this.userName = userName;
        this.FCM_TOKEN = FCM_TOKEN;
        this.created_timestamp = created_timestamp;
        this.lastLogin_timestamp = lastLogin_timestamp;
    }

    public UserData() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFCM_TOKEN() {
        return FCM_TOKEN;
    }

    public void setFCM_TOKEN(String FCM_TOKEN) {
        this.FCM_TOKEN = FCM_TOKEN;
    }

    public com.reiserx.nimbleq.Models.userDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(com.reiserx.nimbleq.Models.userDetails userDetails) {
        this.userDetails = userDetails;
    }

    public long getCreated_timestamp() {
        return created_timestamp;
    }

    public void setCreated_timestamp(long created_timestamp) {
        this.created_timestamp = created_timestamp;
    }

    public long getLastLogin_timestamp() {
        return lastLogin_timestamp;
    }

    public void setLastLogin_timestamp(long lastLogin_timestamp) {
        this.lastLogin_timestamp = lastLogin_timestamp;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
