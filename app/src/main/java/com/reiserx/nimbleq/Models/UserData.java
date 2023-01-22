package com.reiserx.nimbleq.Models;

public class UserData {
    String uid, phoneNumber, userName, FCM_TOKEN;
    userDetails userDetails;

    public UserData(String uid, String phoneNumber, String userName, String FCM_TOKEN) {
        this.uid = uid;
        this.phoneNumber = phoneNumber;
        this.userName = userName;
        this.FCM_TOKEN = FCM_TOKEN;
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
}
