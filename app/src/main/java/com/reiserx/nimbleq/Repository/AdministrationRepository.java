package com.reiserx.nimbleq.Repository;

import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.Announcements.linkModel;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.Models.mimeTypesModel;
import com.reiserx.nimbleq.Models.userDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdministrationRepository {
    private final AdministrationRepository.OnGetMimetypesCompleted onGetMimetypesCompleted;
    private final AdministrationRepository.OnGetFileEnabledComplete onGetFileEnabledComplete;
    private final AdministrationRepository.OnGetUserListComplete onGetUserListComplete;
    private final AdministrationRepository.OnGetUserDetailsComplete onGetUserDetailsComplete;
    private final AdministrationRepository.OnGetClassJoinCountComplete onGetClassJoinCountComplete;

    DatabaseReference reference;
    DatabaseReference userDataReference;
    DatabaseReference userTypeReference;
    DatabaseReference classJoinReference;

    CollectionReference userDetailsReference;

    public AdministrationRepository(AdministrationRepository.OnGetMimetypesCompleted onGetMimetypesCompleted,
                                    AdministrationRepository.OnGetFileEnabledComplete onGetFileEnabledComplete,
                                    AdministrationRepository.OnGetUserListComplete onGetUserListComplete,
                                    AdministrationRepository.OnGetUserDetailsComplete onGetUserDetailsComplete,
                                    AdministrationRepository.OnGetClassJoinCountComplete onGetClassJoinCountComplete) {

        this.onGetMimetypesCompleted = onGetMimetypesCompleted;
        this.onGetFileEnabledComplete = onGetFileEnabledComplete;
        this.onGetUserListComplete = onGetUserListComplete;
        this.onGetUserDetailsComplete = onGetUserDetailsComplete;
        this.onGetClassJoinCountComplete = onGetClassJoinCountComplete;

        reference = FirebaseDatabase.getInstance().getReference().child("Data").child("Administration");
        userDataReference = FirebaseDatabase.getInstance().getReference().child("Data").child("UserData");
        userTypeReference = FirebaseDatabase.getInstance().getReference().child("Data").child("Main").child("UserType");
        classJoinReference = FirebaseDatabase.getInstance().getReference().child("Data").child("Main").child("Classes").child("ClassJoinState");

        userDetailsReference = FirebaseFirestore.getInstance().collection("UserData");
    }

    public void getMimeTypesForGroupChats() {
        List<String> mimeTypes = new ArrayList<>();
        reference.child("Filetypes").child("GroupChats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot mimetype : snapshot.getChildren()) {
                        mimeTypesModel value = mimetype.getValue(mimeTypesModel.class);
                        if (value != null)
                            mimeTypes.add(value.getMimetype());
                    }
                    onGetMimetypesCompleted.onSuccess(mimeTypes);
                } else onGetMimetypesCompleted.onFailure("MimeTypes not available");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onGetMimetypesCompleted.onFailure(error.toString());
            }
        });
    }

    public void getFilesEnabled() {
        reference.child("Filetypes").child("ImagesOnly").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    boolean value = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                    onGetFileEnabledComplete.onSuccess(value);
                } else onGetFileEnabledComplete.onFailure("MimeTypes not available");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onGetFileEnabledComplete.onFailure(error.toString());
            }
        });
    }

    public void getAllUserList() {
        List<UserData> data = new ArrayList<>();

        userDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        UserData userData = snapshot1.getValue(UserData.class);
                        if (userData != null) {
                            data.add(userData);
                        }
                    }
                    onGetUserListComplete.onGetUserListSuccess(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onGetUserListComplete.onGetUserListFailure(error.toString());
            }
        });
    }

    public void getTeacherList() {
        List<UserData> data = new ArrayList<>();
        Query query = userTypeReference.orderByChild("teacher").equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        userDataReference.child(snapshot1.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    UserData UserData = snapshot.getValue(UserData.class);
                                    if (UserData != null) {
                                        data.add(UserData);
                                    }
                                    onGetUserListComplete.onGetUserListSuccess(data);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                onGetUserListComplete.onGetUserListFailure(error.toString());
                            }
                        });
                    }
                    onGetUserListComplete.onGetUserListSuccess(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getStudentList() {
        List<UserData> data = new ArrayList<>();
        Query query = userTypeReference.orderByChild("learner").equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        userDataReference.child(snapshot1.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    UserData UserData = snapshot.getValue(UserData.class);
                                    if (UserData != null) {
                                        data.add(UserData);
                                    }
                                    onGetUserListComplete.onGetUserListSuccess(data);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                onGetUserListComplete.onGetUserListFailure(error.toString());
                            }
                        });
                    }
                    onGetUserListComplete.onGetUserListSuccess(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getUserDetails(String userID) {
        userDetailsReference.document(userID).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.exists()) {
                userDetails userDetails = queryDocumentSnapshots.toObject(com.reiserx.nimbleq.Models.userDetails.class);
                onGetUserDetailsComplete.onGetUserDetailsSuccess(userDetails);
            } else {
                onGetUserDetailsComplete.onFailed("User does not exist in database");
            }
        }).addOnFailureListener(e -> {
            onGetUserDetailsComplete.onFailed(e.toString());
        });
    }

    public void getClassJoinCount(String userID) {
        com.google.firebase.database.Query query = classJoinReference.orderByChild(userID).equalTo(userID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    onGetClassJoinCountComplete.onGetClassJoinCountSuccess(snapshot.getChildrenCount());
                } else onGetClassJoinCountComplete.onFailed("No data available");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onGetClassJoinCountComplete.onFailed(error.toString());
            }
        });
    }

    public interface OnGetMimetypesCompleted {
        void onSuccess(List<String> mimetypes);

        void onFailure(String error);
    }

    public interface OnGetFileEnabledComplete {
        void onSuccess(Boolean enabled);

        void onFailure(String error);
    }

    public interface OnGetUserListComplete {
        void onGetUserListSuccess(List<UserData> userDataList);

        void onGetUserListFailure(String error);
    }

    public interface OnGetUserDetailsComplete {
        void onGetUserDetailsSuccess(userDetails userDetailsList);

        void onFailed(String error);
    }

    public interface OnGetClassJoinCountComplete {
        void onGetClassJoinCountSuccess(long count);

        void onFailed(String error);
    }
}
