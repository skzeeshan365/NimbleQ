package com.reiserx.nimbleq.Models.Doubts;

import com.reiserx.nimbleq.Models.Announcements.linkModel;

import java.util.List;

public class AnswerModel {
    String DOUBT_ID, answer, TEACHER_UID, id;
    long timeStamp;
    List<linkModel> linkModels;
    String teacherName;

    public AnswerModel(String DOUBT_ID, String answer, String TEACHER_UID) {
        this.DOUBT_ID = DOUBT_ID;
        this.answer = answer;
        this.TEACHER_UID = TEACHER_UID;
    }

    public AnswerModel() {
    }

    public String getDOUBT_ID() {
        return DOUBT_ID;
    }

    public void setDOUBT_ID(String DOUBT_ID) {
        this.DOUBT_ID = DOUBT_ID;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
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

    public String getTEACHER_UID() {
        return TEACHER_UID;
    }

    public void setTEACHER_UID(String TEACHER_UID) {
        this.TEACHER_UID = TEACHER_UID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
