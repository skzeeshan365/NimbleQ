package com.reiserx.nimbleq.Models;

public class userDetails {
    String grade, schoolName, state, city, gender, username;

    public userDetails(String grade, String schoolName, String state, String city, String gender) {
        this.grade = grade;
        this.schoolName = schoolName;
        this.state = state;
        this.city = city;
        this.gender = gender;
    }

    public userDetails() {
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
