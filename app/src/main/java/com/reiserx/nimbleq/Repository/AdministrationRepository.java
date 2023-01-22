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
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.Announcements.linkModel;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.Models.mimeTypesModel;
import com.reiserx.nimbleq.Models.userDetails;
import com.reiserx.nimbleq.Utils.MapComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AdministrationRepository {
    private final AdministrationRepository.OnGetMimetypesCompleted onGetMimetypesCompleted;
    private final AdministrationRepository.OnGetFileEnabledComplete onGetFileEnabledComplete;
    private final AdministrationRepository.OnGetUserListComplete onGetUserListComplete;
    private final AdministrationRepository.OnGetUserDetailsComplete onGetUserDetailsComplete;
    private final AdministrationRepository.OnGetClassJoinCountComplete onGetClassJoinCountComplete;
    private final AdministrationRepository.OnGetClassCreateCountComplete onGetClassCreateCountComplete;
    private final AdministrationRepository.OnGetListStringDataCountComplete onGetListStringDataCountComplete;

    DatabaseReference reference;
    DatabaseReference userDataReference;
    DatabaseReference userTypeReference;
    DatabaseReference classJoinReference;

    CollectionReference userDetailsReference;
    DocumentReference classReference;

    public AdministrationRepository(AdministrationRepository.OnGetMimetypesCompleted onGetMimetypesCompleted,
                                    AdministrationRepository.OnGetFileEnabledComplete onGetFileEnabledComplete,
                                    AdministrationRepository.OnGetUserListComplete onGetUserListComplete,
                                    AdministrationRepository.OnGetUserDetailsComplete onGetUserDetailsComplete,
                                    AdministrationRepository.OnGetClassJoinCountComplete onGetClassJoinCountComplete,
                                    AdministrationRepository.OnGetClassCreateCountComplete onGetClassCreateCountComplete,
                                    AdministrationRepository.OnGetListStringDataCountComplete onGetListStringDataCountComplete) {

        this.onGetMimetypesCompleted = onGetMimetypesCompleted;
        this.onGetFileEnabledComplete = onGetFileEnabledComplete;
        this.onGetUserListComplete = onGetUserListComplete;
        this.onGetUserDetailsComplete = onGetUserDetailsComplete;
        this.onGetClassJoinCountComplete = onGetClassJoinCountComplete;
        this.onGetClassCreateCountComplete = onGetClassCreateCountComplete;
        this.onGetListStringDataCountComplete = onGetListStringDataCountComplete;

        reference = FirebaseDatabase.getInstance().getReference().child("Data").child("Administration");
        userDataReference = FirebaseDatabase.getInstance().getReference().child("Data").child("UserData");
        userTypeReference = FirebaseDatabase.getInstance().getReference().child("Data").child("Main").child("UserType");
        classJoinReference = FirebaseDatabase.getInstance().getReference().child("Data").child("Main").child("Classes").child("ClassJoinState");

        userDetailsReference = FirebaseFirestore.getInstance().collection("UserData");
        classReference = FirebaseFirestore.getInstance().collection("Main").document("Class");
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

    public void getGradeList() {
        List<String> gradeList = new ArrayList<>();
        gradeList.add("Select grade");
        reference.child("Lists").child("GradeList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String value = snapshot1.getValue(String.class);
                        if (value != null)
                            gradeList.add(value);
                    }
                    onGetListStringDataCountComplete.onGetListStringDataSuccess(gradeList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onGetListStringDataCountComplete.onFailed(error.toString());
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
                            userDetailsReference.document(userData.getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                                if (queryDocumentSnapshots.exists()) {
                                    userDetails userDetails = queryDocumentSnapshots.toObject(com.reiserx.nimbleq.Models.userDetails.class);
                                    userData.setUserDetails(userDetails);
                                }
                                data.add(userData);
                                onGetUserListComplete.onGetUserListSuccess(data);
                            });
                        }
                    }
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
                                        userDetailsReference.document(UserData.getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                                            if (queryDocumentSnapshots.exists()) {
                                                userDetails userDetails = queryDocumentSnapshots.toObject(com.reiserx.nimbleq.Models.userDetails.class);
                                                UserData.setUserDetails(userDetails);
                                            }
                                            data.add(UserData);
                                            onGetUserListComplete.onGetUserListSuccess(data);
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                onGetUserListComplete.onGetUserListFailure(error.toString());
                            }
                        });
                    }
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
                                        userDetailsReference.document(UserData.getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                                            if (queryDocumentSnapshots.exists()) {
                                                userDetails userDetails = queryDocumentSnapshots.toObject(com.reiserx.nimbleq.Models.userDetails.class);
                                                UserData.setUserDetails(userDetails);
                                            }
                                            data.add(UserData);
                                            onGetUserListComplete.onGetUserListSuccess(data);
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                onGetUserListComplete.onGetUserListFailure(error.toString());
                            }
                        });
                    }
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
                } else onGetClassJoinCountComplete.onGetClassJoinCountSuccess(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onGetClassJoinCountComplete.onFailed(error.toString());
            }
        });
    }

    public void getCreatedClassCount(String userID) {
        com.google.firebase.firestore.Query query1 = classReference.collection("ClassInfo").whereEqualTo("teacher_info", userID);
        AggregateQuery countQuery = query1.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                AggregateQuerySnapshot snapshot1 = task1.getResult();
                onGetClassCreateCountComplete.onGetClassCreateCountSuccess(snapshot1.getCount());
            } else {
                onGetClassCreateCountComplete.onFailed(task1.getException().toString());
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

    public interface OnGetClassCreateCountComplete {
        void onGetClassCreateCountSuccess(long count);

        void onFailed(String error);
    }

    public interface OnGetListStringDataCountComplete {
        void onGetListStringDataSuccess(List<String> data);

        void onFailed(String error);
    }
}
