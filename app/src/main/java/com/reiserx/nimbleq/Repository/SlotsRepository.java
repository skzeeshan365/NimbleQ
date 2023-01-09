package com.reiserx.nimbleq.Repository;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.nimbleq.Models.subjectAndTimeSlot;

public class SlotsRepository {
    private final DatabaseReference databaseReference;
    private final OnRealtimeDbTaskComplete onRealtimeDbTaskComplete;
    subjectAndTimeSlot subjectAndTimeSlot;

    public SlotsRepository(OnRealtimeDbTaskComplete onRealtimeDbTaskComplete) {
        this.onRealtimeDbTaskComplete = onRealtimeDbTaskComplete;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Data").child("Main").child("SubjectList");
    }

    public void getSubjectForStudents(String userID) {
        com.google.firebase.database.Query DB_query = databaseReference.child("subjectForStudents").child(userID).orderByChild("current").equalTo(true);
        DB_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        subjectAndTimeSlot = snapshot1.getValue(com.reiserx.nimbleq.Models.subjectAndTimeSlot.class);
                    }
                    onRealtimeDbTaskComplete.onSuccess(subjectAndTimeSlot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onRealtimeDbTaskComplete.onFailure(error);
            }
        });
    }

    public void getSubjectForTeachers(String userID) {
        com.google.firebase.database.Query DB_query = databaseReference.child("subjectForTeacher").child(userID).orderByChild("current").equalTo(true);
        DB_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        subjectAndTimeSlot = snapshot1.getValue(com.reiserx.nimbleq.Models.subjectAndTimeSlot.class);
                    }
                    onRealtimeDbTaskComplete.onSuccess(subjectAndTimeSlot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onRealtimeDbTaskComplete.onFailure(error);
            }
        });
    }

    public interface OnRealtimeDbTaskComplete {
        void onSuccess(subjectAndTimeSlot subjectAndTimeSlot);

        void onFailure(DatabaseError error);
    }
}
