package com.reiserx.nimbleq.Models;

public class userType {
    boolean learner = false, teacher = false;
    int currentStatus = 0;

    public userType(int currentStatus) {
        this.currentStatus = currentStatus;
    }

    public userType() {
    }

    public boolean isLearner() {
        return learner;
    }

    public void setLearner(boolean learner) {
        this.learner = learner;
    }

    public boolean isTeacher() {
        return teacher;
    }

    public void setTeacher(boolean teacher) {
        this.teacher = teacher;
    }

    public int getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(int currentStatus) {
        this.currentStatus = currentStatus;
    }
}
