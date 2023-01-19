package com.reiserx.nimbleq.Models;

public class RatingModel {
    int rating;
    String userID, feedback;

    public RatingModel(int rating, String userID, String feedback) {
        this.rating = rating;
        this.userID = userID;
        this.feedback = feedback;
    }

    public RatingModel(int rating, String userID) {
        this.rating = rating;
        this.userID = userID;
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
}
