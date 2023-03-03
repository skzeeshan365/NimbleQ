package com.reiserx.nimbleq.Models;

public class ClassRequestModel {
    String subject, topic, timeSlot, id, studentID, teacherID;
    String accepted, acceptedID;
    String teacherName;

    public ClassRequestModel(String subject, String topic, String timeSlot, String teacher, String studentID) {
        this.subject = subject;
        this.topic = topic;
        this.teacherID = teacher;
        this.timeSlot = timeSlot;
        this.studentID = studentID;
    }

    public ClassRequestModel(String subject, String topic, String timeSlot, String studentID) {
        this.subject = subject;
        this.topic = topic;
        this.timeSlot = timeSlot;
        this.studentID = studentID;
    }

    public ClassRequestModel() {
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

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String isAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getAcceptedID() {
        return acceptedID;
    }

    public void setAcceptedID(String acceptedID) {
        this.acceptedID = acceptedID;
    }
}
