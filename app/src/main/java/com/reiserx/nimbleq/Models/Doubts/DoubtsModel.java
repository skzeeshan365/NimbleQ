package com.reiserx.nimbleq.Models.Doubts;

import com.reiserx.nimbleq.Models.Announcements.linkModel;

import java.util.List;

public class DoubtsModel {
    String subject, topic, short_desc, long_desc, userID, id;
    long timeStamp, answerCount;
    boolean solved;
    List<linkModel> linkModels;

    public DoubtsModel(String subject, String topic, String short_desc, String long_desc, long timeStamp, List<linkModel> linkModels) {
        this.subject = subject;
        this.topic = topic;
        this.short_desc = short_desc;
        this.long_desc = long_desc;
        this.timeStamp = timeStamp;
        this.linkModels = linkModels;
    }

    public DoubtsModel(String subject, String topic, String short_desc, String long_desc, String userID, long timeStamp) {
        this.subject = subject;
        this.topic = topic;
        this.short_desc = short_desc;
        this.long_desc = long_desc;
        this.userID = userID;
        this.timeStamp = timeStamp;
    }

    public DoubtsModel() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getShort_desc() {
        return short_desc;
    }

    public void setShort_desc(String short_desc) {
        this.short_desc = short_desc;
    }

    public String getLong_desc() {
        return long_desc;
    }

    public void setLong_desc(String long_desc) {
        this.long_desc = long_desc;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public List<linkModel> getLinkModels() {
        return linkModels;
    }

    public void setLinkModels(List<linkModel> linkModels) {
        this.linkModels = linkModels;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(long answerCount) {
        this.answerCount = answerCount;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
