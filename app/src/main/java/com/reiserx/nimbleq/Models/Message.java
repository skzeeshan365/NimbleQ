package com.reiserx.nimbleq.Models;

import com.google.firebase.firestore.DocumentSnapshot;

public class Message {
    private String messageId, message, senderId, senderName, timeStamp, replymsg, replyuid, replyid, replyname;
    long queryStamp;
    String imageUrl;
    DocumentSnapshot snapshot;

    public Message(String message, String senderId, String senderName, String timeStamp, String replymsg, String replyname, String replyuid, String replyid, long queryStamp) {
        this.message = message;
        this.senderId = senderId;
        this.timeStamp = timeStamp;
        this.replymsg = replymsg;
        this.replyuid = replyuid;
        this.replyid = replyid;
        this.replyname = replyname;
        this.senderName = senderName;
        this.queryStamp = queryStamp;
    }

    public Message(String message, String senderId, String senderName, String timeStamp, long queryStamp) {
        this.message = message;
        this.senderId = senderId;
        this.timeStamp = timeStamp;
        this.senderName = senderName;
        this.queryStamp = queryStamp;
    }

    public Message() {
    }

    public Message(String messageId, String message, String senderId, String senderName, String imageUrl, String timeStamp, String replymsg, String replyname, String replyuid, String replyid, long queryStamp) {
        this.message = message;
        this.senderId = senderId;
        this.timeStamp = timeStamp;
        this.replymsg = replymsg;
        this.replyuid = replyuid;
        this.replyid = replyid;
        this.replyname = replyname;
        this.senderName = senderName;
        this.imageUrl = imageUrl;
        this.queryStamp = queryStamp;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getReplymsg() {
        return replymsg;
    }

    public void setReplymsg(String replymsg) {
        this.replymsg = replymsg;
    }

    public String getReplyuid() {
        return replyuid;
    }

    public void setReplyuid(String replyuid) {
        this.replyuid = replyuid;
    }

    public String getReplyid() {
        return replyid;
    }

    public void setReplyid(String replyid) {
        this.replyid = replyid;
    }

    public String getReplyname() {
        return replyname;
    }

    public void setReplyname(String replyname) {
        this.replyname = replyname;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public DocumentSnapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(DocumentSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public long getQueryStamp() {
        return queryStamp;
    }

    public void setQueryStamp(long queryStamp) {
        this.queryStamp = queryStamp;
    }
}