package com.reiserx.nimbleq.Repository;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.Models.userType;

public class UserDataRepository {
    private final DatabaseReference databaseReference;
    private final UserDataRepository.OnRealtimeDbTaskComplete onRealtimeDbTaskComplete;
    private final UserDataRepository.getUserTypeComplete getUserTypeComplete;
    private final UserDataRepository.getUsernameComplete getUsernameComplete;
    private final DatabaseReference userTypeReference;

    public UserDataRepository(UserDataRepository.OnRealtimeDbTaskComplete onRealtimeDbTaskComplete, UserDataRepository.getUsernameComplete getUsernameComplete, UserDataRepository.getUserTypeComplete getUserTypeComplete) {
        this.onRealtimeDbTaskComplete = onRealtimeDbTaskComplete;
        this.getUsernameComplete = getUsernameComplete;
        this.getUserTypeComplete = getUserTypeComplete;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Data").child("UserData");
        userTypeReference = database.getReference().child("Data").child("Main").child("UserType");
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

    public void getUserType(String userID) {
        userTypeReference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userType userType = snapshot.getValue(com.reiserx.nimbleq.Models.userType.class);
                    if (userType != null) {
                        getUserTypeComplete.onSuccess(userType);
                    } else getUserTypeComplete.onFailed("Data not found");
                } else getUserTypeComplete.onFailed("Data not found");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                getUserTypeComplete.onFailed(error.toString());
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

    public interface getUserTypeComplete {
        void onSuccess(userType userType);

        void onFailed(String error);
    }
}
