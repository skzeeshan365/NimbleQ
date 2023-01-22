package com.reiserx.nimbleq.Models;

import com.google.firebase.database.DatabaseReference;

public class AdminListModel {
    String name;
    DatabaseReference reference;

    public AdminListModel(String name, DatabaseReference reference) {
        this.name = name;
        this.reference = reference;
    }

    public AdminListModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DatabaseReference getReference() {
        return reference;
    }

    public void setReference(DatabaseReference reference) {
        this.reference = reference;
    }
}
