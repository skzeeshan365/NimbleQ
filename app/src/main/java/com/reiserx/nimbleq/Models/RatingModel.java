package com.reiserx.nimbleq.Models;

public class RatingModel {
    int rating;
    String userID, feedback, name;
    long timeStamp;

    public RatingModel(int rating, String userID, String feedback, long timeStamp) {
        this.rating = rating;
        this.userID = userID;
        this.feedback = feedback;
        this.timeStamp = timeStamp;
    }

    public RatingModel(int rating, String userID, long timeStamp) {
        this.rating = rating;
        this.userID = userID;
        this.timeStamp = timeStamp;
    }

    public RatingModel() {
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
