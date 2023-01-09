package com.reiserx.nimbleq.Models;

public class UserData {
    String uid, phoneNumber, userName;

    public UserData(String uid, String phoneNumber, String userName) {
        this.uid = uid;
        this.phoneNumber = phoneNumber;
        this.userName = userName;
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
}