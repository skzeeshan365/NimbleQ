package com.reiserx.nimbleq.Models;

import com.google.firebase.database.DatabaseReference;

public class subjectAndTimeSlot {
    String subject, topic, timeSlot;
    boolean current;
    String key;
    DatabaseReference reference;

    public subjectAndTimeSlot(String subject, String topic, String timeSlot, boolean current) {
        this.subject = subject;
        this.topic = topic;
        this.timeSlot = timeSlot;
        this.current = current;
    }

    public subjectAndTimeSlot(String subject, String topic, String timeSlot) {
        this.subject = subject;
        this.topic = topic;
        this.timeSlot = timeSlot;
        this.current = current;
    }

    public subjectAndTimeSlot(String subject, String topic) {
        this.subject = subject;
        this.topic = topic;
    }

    public subjectAndTimeSlot() {
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

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DatabaseReference getReference() {
        return reference;
    }

    public void setReference(DatabaseReference reference) {
        this.reference = reference;
    }
}
