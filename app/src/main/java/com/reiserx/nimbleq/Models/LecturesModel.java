package com.reiserx.nimbleq.Models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;

public class LecturesModel {
    int lecture;
    boolean status;
    DocumentReference reference;
    DatabaseReference databaseReference;

    public LecturesModel(int lecture, boolean status) {
        this.lecture = lecture;
        this.status = status;
    }

    public LecturesModel() {
    }

    public int getLecture() {
        return lecture;
    }

    public void setLecture(int lecture) {
        this.lecture = lecture;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public DocumentReference getReference() {
        return reference;
    }

    public void setReference(DocumentReference reference) {
        this.reference = reference;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void setDatabaseReference(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }
}
