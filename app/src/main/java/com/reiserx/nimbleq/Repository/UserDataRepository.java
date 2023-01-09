package com.reiserx.nimbleq.Repository;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.nimbleq.Models.UserData;

public class UserDataRepository {
    private final DatabaseReference databaseReference;
    private final UserDataRepository.OnRealtimeDbTaskComplete onRealtimeDbTaskComplete;
    private final UserDataRepository.getUsernameComplete getUsernameComplete;

    public UserDataRepository(UserDataRepository.OnRealtimeDbTaskComplete onRealtimeDbTaskComplete, UserDataRepository.getUsernameComplete getUsernameComplete) {
        this.onRealtimeDbTaskComplete = onRealtimeDbTaskComplete;
        this.getUsernameComplete = getUsernameComplete;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Data").child("UserData");
    }

    public void getUserData(String userID) {
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserData userData = snapshot.getValue(UserData.class);
                    if (userData != null)
                        onRealtimeDbTaskComplete.onSuccess(userData);
                    else
                        onRealtimeDbTaskComplete.onFailure("User not found");
                } else
                    onRealtimeDbTaskComplete.onFailure("User does not exist");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onRealtimeDbTaskComplete.onFailure(error.toString());
            }
        });
    }

    public void getUsername(String userID) {
        databaseReference.child(userID).child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.getValue(String.class);
                    if (username != null)
                        getUsernameComplete.onSuccess(username);
                } else
                    getUsernameComplete.onFailed("Username not available");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                getUsernameComplete.onFailed(error.toString());
            }
        });
    }

    public interface OnRealtimeDbTaskComplete {
        void onSuccess(UserData userData);

        void onFailure(String error);
    }

    public interface getUsernameComplete {
        void onSuccess(String username);

        void onFailed(String error);
    }
}
