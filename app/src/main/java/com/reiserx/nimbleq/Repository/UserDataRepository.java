package com.reiserx.nimbleq.Repository;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.Models.userDetails;
import com.reiserx.nimbleq.Models.userType;

import java.util.ArrayList;
import java.util.List;

public class UserDataRepository {
    private final UserDataRepository.OnRealtimeDbTaskComplete onRealtimeDbTaskComplete;
    private final UserDataRepository.getUserTypeComplete getUserTypeComplete;
    private final UserDataRepository.getUsernameComplete getUsernameComplete;
    private final UserDataRepository.getUserDetailsComplete getUserDetailsComplete;
    private final UserDataRepository.getTeacherListComplete getTeacherListComplete;

    private final DatabaseReference userTypeReference;
    private final DatabaseReference databaseReference;
    private final CollectionReference collectionReference;

    public UserDataRepository(UserDataRepository.OnRealtimeDbTaskComplete onRealtimeDbTaskComplete,
                              UserDataRepository.getUsernameComplete getUsernameComplete,
                              UserDataRepository.getUserTypeComplete getUserTypeComplete,
                              UserDataRepository.getUserDetailsComplete getUserDetailsComplete,
                              UserDataRepository.getTeacherListComplete getTeacherListComplete) {

        this.onRealtimeDbTaskComplete = onRealtimeDbTaskComplete;
        this.getUsernameComplete = getUsernameComplete;
        this.getUserTypeComplete = getUserTypeComplete;
        this.getUserDetailsComplete = getUserDetailsComplete;
        this.getTeacherListComplete = getTeacherListComplete;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Data").child("UserData");
        userTypeReference = database.getReference().child("Data").child("Main").child("UserType");

        collectionReference = FirebaseFirestore.getInstance().collection("UserData");
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

    public void getTeacherList() {
        List<String> list = new ArrayList<>();
        Query query = userTypeReference.orderByChild("teacher");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        list.add(snapshot1.getKey());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getUserDetails(String userID) {
        collectionReference.document(userID).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.exists()) {
                userDetails userDetails = queryDocumentSnapshots.toObject(com.reiserx.nimbleq.Models.userDetails.class);
                getUserDetailsComplete.onSuccess(userDetails);
            }
        }).addOnFailureListener(e -> getUserTypeComplete.onFailed(e.toString()));
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

    public interface getUserDetailsComplete {
        void onSuccess(userDetails userDetailsList);

        void onFailed(String error);
    }

    public interface getTeacherListComplete {
        void onSuccess(List<String> teacherList);

        void onFailed(String error);
    }
}