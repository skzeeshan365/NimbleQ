package com.reiserx.nimbleq.Models.Announcements;

import java.util.List;

public class announcementsModel {
    String name, info, classId;
    long timeStamp;
    List<linkModel> linkModels;

    public announcementsModel(String name, String info, String classId, long timeStamp, List<linkModel> linkModels) {
        this.name = name;
        this.info = info;
        this.classId = classId;
        this.timeStamp = timeStamp;
        this.linkModels = linkModels;
    }

    public announcementsModel(String name, String info, String classId, long timeStamp) {
        this.name = name;
        this.info = info;
        this.classId = classId;
        this.timeStamp = timeStamp;
        this.linkModels = linkModels;
    }

    public announcementsModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public List<linkModel> getLinkModels() {
        return linkModels;
    }

    public void setLinkModels(List<linkModel> linkModels) {
        this.linkModels = linkModels;
    }
}
