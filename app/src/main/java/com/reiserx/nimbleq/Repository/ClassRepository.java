package com.reiserx.nimbleq.Repository;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.ClassRequestModel;
import com.reiserx.nimbleq.Models.classModel;
import com.reiserx.nimbleq.Models.subjectAndTimeSlot;
import com.reiserx.nimbleq.Utils.NotificationUtils;
import com.reiserx.nimbleq.Utils.Notify;

import java.util.ArrayList;
import java.util.List;

public class ClassRepository {
    private final ClassRepository.OnRealtimeDbTaskComplete onRealtimeDbTaskComplete;
    private final ClassRepository.OnClassJoinStateChanged OnClassJoinStateChanged;
    private final ClassRepository.OnGetClassListComplete OnGetClassListComplete;
    private final ClassRepository.onGetClassRequestComplete onGetClassRequestComplete;
    private final DocumentReference reference;
    private final DatabaseReference classJoinReference;
    private final DatabaseReference userDataReference;
    Query query;

    public ClassRepository(ClassRepository.OnRealtimeDbTaskComplete onRealtimeDbTaskComplete,
                           ClassRepository.OnClassJoinStateChanged OnClassJoinStateChanged,
                           ClassRepository.OnGetClassListComplete OnGetClassListComplete,
                           ClassRepository.onGetClassRequestComplete onGetClassRequestComplete) {

        this.onRealtimeDbTaskComplete = onRealtimeDbTaskComplete;
        this.OnClassJoinStateChanged = OnClassJoinStateChanged;
        this.OnGetClassListComplete = OnGetClassListComplete;
        this.onGetClassRequestComplete = onGetClassRequestComplete;
        reference = FirebaseFirestore.getInstance().collection("Main").document("Class");
        classJoinReference = FirebaseDatabase.getInstance().getReference().child("Data").child("Main").child("Classes").child("ClassJoinState");
        userDataReference = FirebaseDatabase.getInstance().getReference().child("Data").child("UserData");
    }

    public void getClassData(String classID) {
        DocumentReference documentReference = reference.collection("ClassInfo").document(classID);

        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                com.reiserx.nimbleq.Models.classModel models = documentSnapshot.toObject(classModel.class);
                if (models != null) {
                    models.setClassID(classID);
                    onRealtimeDbTaskComplete.onSuccess(models);
                }
            } else {
                onRealtimeDbTaskComplete.onFailure("Class does not exist");
            }
        }).addOnFailureListener(e -> onRealtimeDbTaskComplete.onFailure(e.toString()));
    }

    public void setClassJoinState(String userID, String classID, String token, boolean join, Context context) {
        if (join) {
            classJoinReference.child(classID).child(userID).setValue(userID).addOnSuccessListener(unused -> OnClassJoinStateChanged.onSuccess(1)).addOnFailureListener(e -> OnClassJoinStateChanged.onGetClassStateFailure(e.toString()));

            FirebaseMessaging fm = FirebaseMessaging.getInstance();
            fm.subscribeToTopic(classID);

            userDataReference.child(userID).child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String username = snapshot.getValue(String.class);
                        if (username != null) {
                            Notify notify = new Notify(context);
                            notify.classJoinPayload("A learner left your class", username.concat(" has joined your class"), token, 1);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            classJoinReference.child(classID).child(userID).removeValue().addOnSuccessListener(unused -> OnClassJoinStateChanged.onSuccess(3)).addOnFailureListener(e -> OnClassJoinStateChanged.onGetClassStateFailure(e.toString()));

            userDataReference.child(userID).child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String username = snapshot.getValue(String.class);
                        if (username != null) {
                            Notify notify = new Notify(context);
                            notify.classJoinPayload("New learner in your class", username.concat(" has left your class"), token, 1);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    public void getClassJoinState(String userID, String classID) {
        classJoinReference.child(classID).child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    OnClassJoinStateChanged.onSuccess(2);
                else OnClassJoinStateChanged.onSuccess(3);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                OnClassJoinStateChanged.onGetClassStateFailure(error.toString());
            }
        });
    }

    public void getClassList(subjectAndTimeSlot subjectAndTimeSlot, String userID) {
        List<classModel> data = new ArrayList<>();
        query = reference.collection("ClassInfo")
                .whereEqualTo("subject", subjectAndTimeSlot.getSubject())
                .whereEqualTo("time_slot", subjectAndTimeSlot.getTimeSlot())
                .whereNotEqualTo("teacher_info", userID);

        query.get().addOnSuccessListener(task -> {
            if (task != null) {
                if (!task.isEmpty()) {
                    for (DocumentSnapshot document : task.getDocuments()) {
                        classModel classModel = document.toObject(com.reiserx.nimbleq.Models.classModel.class);
                        if (classModel != null) {
                            classModel.setClassID(document.getId());

                            userDataReference.child(classModel.getTeacher_info()).child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        String username = snapshot.getValue(String.class);
                                        if (username != null) {
                                            classModel.setTeacher_name(username);
                                            data.add(classModel);
                                        }
                                    }
                                    OnGetClassListComplete.onSuccess(data);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    OnGetClassListComplete.onGetClassListFailure(error.toString());
                                }
                            });
                        }
                    }
                } else OnGetClassListComplete.onGetClassListFailure("Class not available");
            } else OnGetClassListComplete.onGetClassListFailure("Class not available");
        }).addOnFailureListener(e -> OnGetClassListComplete.onGetClassListFailure(e.toString()));
    }

    public void getClassListForTeacher(String userID) {
        List<classModel> data = new ArrayList<>();
        query = reference.collection("ClassInfo")
                .whereEqualTo("teacher_info", userID);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                data.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    classModel classModel = document.toObject(com.reiserx.nimbleq.Models.classModel.class);
                    classModel.setClassID(document.getId());

                    userDataReference.child(classModel.getTeacher_info()).child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String username = snapshot.getValue(String.class);
                                if (username != null) {
                                    classModel.setTeacher_name(username);
                                    data.add(classModel);
                                }
                            }
                            if (!data.isEmpty())
                                OnGetClassListComplete.onSuccess(data);
                            else
                                OnGetClassListComplete.onGetClassListFailure("Class not available");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            OnGetClassListComplete.onGetClassListFailure(error.toString());
                        }
                    });
                }
            } else
                OnGetClassListComplete.onGetClassListFailure("Failed to get class list");
        });
    }

    public void getClassRequests(subjectAndTimeSlot subjectAndTimeSlot) {
        Log.d(CONSTANTS.TAG, subjectAndTimeSlot.getSubject());
        List<ClassRequestModel> requestModelList = new ArrayList<>();
        Query query = reference.collection("ClassRequests")
                .whereEqualTo("subject", subjectAndTimeSlot.getSubject());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                requestModelList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    ClassRequestModel classModel = document.toObject(com.reiserx.nimbleq.Models.ClassRequestModel.class);
                    classModel.setId(document.getId());
                    requestModelList.add(classModel);
                }
                if (!requestModelList.isEmpty())
                    onGetClassRequestComplete.onGetClassRequestsSuccess(requestModelList);
                else
                    onGetClassRequestComplete.onGetClassListFailure("No requests available");
            } else
                onGetClassRequestComplete.onGetClassListFailure("Failed to get class list");
        }).addOnFailureListener(e -> onGetClassRequestComplete.onGetClassListFailure(e.toString()));
    }

    public interface OnRealtimeDbTaskComplete {
        void onSuccess(classModel classModel);

        void onFailure(String error);
    }

    public interface OnClassJoinStateChanged {
        void onSuccess(int state);

        void onGetClassStateFailure(String error);
    }

    public interface OnGetClassListComplete {
        void onSuccess(List<classModel> classModelList);

        void onGetClassListFailure(String error);
    }

    public interface onGetClassRequestComplete {
        void onGetClassRequestsSuccess(List<ClassRequestModel> classModelList);

        void onGetClassListFailure(String error);
    }
}
