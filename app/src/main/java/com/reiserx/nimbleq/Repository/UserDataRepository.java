package com.reiserx.nimbleq.Repository;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.LecturesModel;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.Models.userDetails;
import com.reiserx.nimbleq.Models.userType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDataRepository {
    private final UserDataRepository.OnRealtimeDbTaskComplete onRealtimeDbTaskComplete;
    private final UserDataRepository.getUserTypeComplete getUserTypeComplete;
    private final UserDataRepository.getUsernameComplete getUsernameComplete;
    private final UserDataRepository.getUserDetailsComplete getUserDetailsComplete;
    private final UserDataRepository.getDataAsListString getDataAsListString;
    private final UserDataRepository.OnUpdateUsernameComplete onUpdateUsernameComplete;
    private final UserDataRepository.OnGetLecturesComplete onGetLecturesComplete;
    private final UserDataRepository.OnGetCompletedClassesComplete onGetCompletedClassesComplete;
    private final UserDataRepository.OnGetCompletedClasses1Complete onGetCompletedClasses1Complete;

    private final DatabaseReference userTypeReference;
    private final DatabaseReference databaseReference;
    private final DatabaseReference classJoinReference;
    private final DatabaseReference classReference;
    private final CollectionReference collectionReference;

    public UserDataRepository(UserDataRepository.OnRealtimeDbTaskComplete onRealtimeDbTaskComplete,
                              UserDataRepository.getUsernameComplete getUsernameComplete,
                              UserDataRepository.getUserTypeComplete getUserTypeComplete,
                              UserDataRepository.getUserDetailsComplete getUserDetailsComplete,
                              UserDataRepository.getDataAsListString getDataAsListString,
                              UserDataRepository.OnUpdateUsernameComplete onUpdateUsernameComplete,
                              UserDataRepository.OnGetLecturesComplete onGetLecturesComplete,
                              UserDataRepository.OnGetCompletedClassesComplete onGetCompletedClassesComplete,
                              UserDataRepository.OnGetCompletedClasses1Complete onGetCompletedClasses1Complete) {

        this.onRealtimeDbTaskComplete = onRealtimeDbTaskComplete;
        this.getUsernameComplete = getUsernameComplete;
        this.getUserTypeComplete = getUserTypeComplete;
        this.getUserDetailsComplete = getUserDetailsComplete;
        this.getDataAsListString = getDataAsListString;
        this.onUpdateUsernameComplete = onUpdateUsernameComplete;
        this.onGetLecturesComplete = onGetLecturesComplete;
        this.onGetCompletedClassesComplete = onGetCompletedClassesComplete;
        this.onGetCompletedClasses1Complete = onGetCompletedClasses1Complete;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Data").child("UserData");
        userTypeReference = database.getReference().child("Data").child("Main").child("UserType");
        classJoinReference = FirebaseDatabase.getInstance().getReference().child("Data").child("Main").child("Classes").child("ClassJoinState");
        classReference = FirebaseDatabase.getInstance().getReference().child("Data").child("Main").child("Classes");

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

    public void getUserDetails(String userID) {
        collectionReference.document(userID).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.exists()) {
                userDetails userDetails = queryDocumentSnapshots.toObject(com.reiserx.nimbleq.Models.userDetails.class);
                getUserDetailsComplete.onSuccess(userDetails);
                Log.d(CONSTANTS.TAG2, "exist");
            } else {
                getUserDetailsComplete.onFailed("Teacher does not exist in database");
                Log.d(CONSTANTS.TAG2, "not exist");
            }
        }).addOnFailureListener(e -> {
            getUserDetailsComplete.onFailed(e.toString());
            Log.d(CONSTANTS.TAG2, e.toString());
        });
    }

    public void updateFCMToken(String userID) {
        FirebaseMessaging fcm = FirebaseMessaging.getInstance();
        fcm.getToken().addOnSuccessListener(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("FCM_TOKEN", s);
            FirebaseDatabase.getInstance().getReference().child("Data").child("UserData").child(userID).updateChildren(map);
        }).addOnFailureListener(e -> Log.d(CONSTANTS.TAG2, e.toString()));
    }

    public void updateUsername(String userID, String s) {
        Map<String, Object> map = new HashMap<>();
        map.put("userName", s);
        FirebaseDatabase.getInstance().getReference().child("Data").child("UserData").child(userID).updateChildren(map).addOnSuccessListener(unused -> onUpdateUsernameComplete.onSuccess(null)).addOnFailureListener(e -> onUpdateUsernameComplete.onFailed(e.toString()));
    }

    public void getClassLectures(String classID, String userID) {
        List<LecturesModel> data = new ArrayList<>();
        classReference.child("Lectures").child(userID).child(classID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        LecturesModel lecturesModel = snapshot1.getValue(LecturesModel.class);
                        if (lecturesModel != null && snapshot1.getKey() != null) {
                            lecturesModel.setDatabaseReference(classReference.child("Lectures").child(userID).child(classID).child(snapshot1.getKey()));
                            data.add(lecturesModel);
                        }
                    }
                    onGetLecturesComplete.onGetLecturesSuccess(data);
                } else
                    onGetLecturesComplete.onFailure("Data not available");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onGetLecturesComplete.onFailure(error.toString());
            }
        });
    }

    public void getClassCompleteCount(String userID) {
        List<String> data = new ArrayList<>();
        Query query = classReference.child("Lectures").child(userID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot classes : snapshot.getChildren()) {
                        Long counter = 0L;
                        for (DataSnapshot objects : classes.getChildren()) {
                            LecturesModel model = objects.getValue(LecturesModel.class);
                            if (model != null) {
                                if (model.isStatus())
                                    counter++;
                            }
                        }
                        CollectionReference classRef = FirebaseFirestore.getInstance()
                                .collection("Main")
                                .document("Class")
                                .collection("ClassInfo")
                                .document(classes.getKey())
                                .collection("Lectures");

                        AggregateQuery countQuery = classRef.count();
                        Long finalCounter = counter;
                        countQuery.get(AggregateSource.SERVER).addOnSuccessListener(task1 -> {
                            if (task1.getCount() == finalCounter)
                                data.add(classes.getKey());
                            onGetCompletedClassesComplete.onGetCompletedClassesSuccess(data);
                        }).addOnFailureListener(e -> onGetCompletedClassesComplete.onTask1Failure(e.toString()));
                    }

                } else {
                    onGetCompletedClassesComplete.onTask1Failure("N/A");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onGetCompletedClassesComplete.onTask1Failure(error.toString());
            }
        });
    }

    public void getClassCompleteCount1(String userID) {
        List<String> data = new ArrayList<>();

        CollectionReference reference = FirebaseFirestore.getInstance()
                .collection("Main")
                .document("Class")
                .collection("ClassInfo");

        com.google.firebase.firestore.Query query1 = reference.whereEqualTo("teacher_info", userID);
        query1.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                    reference.document(snapshot.getId()).collection("Lectures").get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                        Long counter = 0L;
                        for (DocumentSnapshot snapshot1 : queryDocumentSnapshots1.getDocuments()) {
                            LecturesModel lecturesModel = snapshot1.toObject(LecturesModel.class);
                            if (lecturesModel.isStatus())
                                counter++;
                        }
                        if (queryDocumentSnapshots1.size() == counter) {
                            data.add(snapshot.getId());
                            Log.d(CONSTANTS.TAG2, snapshot.getId());
                        }
                        onGetCompletedClasses1Complete.onGetCompletedClasses1Success(data);
                    });
                }
            } else {
                onGetCompletedClasses1Complete.onTask2Failure("N/A");
            }
        }).addOnFailureListener(e -> {
            onGetCompletedClasses1Complete.onTask2Failure(e.toString());
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

    public interface getUserDetailsComplete {
        void onSuccess(userDetails userDetailsList);

        void onFailed(String error);
    }

    public interface getDataAsListString {
        void onSuccess(List<String> stringList);

        void onFailed(String error);
    }

    public interface OnUpdateUsernameComplete {
        void onSuccess(Void voids);

        void onFailed(String error);
    }

    public interface OnGetLecturesComplete {
        void onGetLecturesSuccess(List<LecturesModel> lecturesModelList);

        void onFailure(String error);
    }

    public interface OnGetCompletedClassesComplete {
        void onGetCompletedClassesSuccess(List<String> classIDList);

        void onTask1Failure(String error);
    }

    public interface OnGetCompletedClasses1Complete {
        void onGetCompletedClasses1Success(List<String> classIDList);

        void onTask2Failure(String error);
    }
}
