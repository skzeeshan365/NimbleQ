package com.reiserx.nimbleq.Repository;

import androidx.annotation.NonNull;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.reiserx.nimbleq.Models.AdminListModel;
import com.reiserx.nimbleq.Models.FCMCREDENTIALS;
import com.reiserx.nimbleq.Models.RatingModel;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.Models.mimeTypesModel;
import com.reiserx.nimbleq.Models.userDetails;
import com.reiserx.nimbleq.Models.zoomCredentials;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdministrationRepository {
    private final AdministrationRepository.OnGetMimetypesCompleted onGetMimetypesCompleted;
    private final AdministrationRepository.OnGetFileEnabledComplete onGetFileEnabledComplete;
    private final AdministrationRepository.OnGetUserListComplete onGetUserListComplete;
    private final AdministrationRepository.OnGetUserDetailsComplete onGetUserDetailsComplete;
    private final AdministrationRepository.OnGetClassJoinCountComplete onGetClassJoinCountComplete;
    private final AdministrationRepository.OnGetClassCreateCountComplete onGetClassCreateCountComplete;
    private final AdministrationRepository.OnGetListStringDataCountComplete onGetListStringDataCountComplete;
    private final AdministrationRepository.OnGetAdminModelListComplete onGetAdminModelListComplete;
    private final AdministrationRepository.OnUpdateModelListComplete onUpdateModelListComplete;
    private final AdministrationRepository.OnGetZoomCredentialsComplete onGetZoomCredentialsComplete;
    private final AdministrationRepository.OnGetFCMCredentialsComplete onGetFCMCredentialsComplete;
    private final AdministrationRepository.OnGetAdministratorComplete onGetAdministratorComplete;
    private final AdministrationRepository.OnGetSlotLimitComplete onGetSlotLimitComplete;
    private final AdministrationRepository.OnGetFileSizeLimitComplete onGetFileSizeLimitComplete;
    private final AdministrationRepository.OnGetLecturesLimitComplete onGetLecturesLimitComplete;
    private final AdministrationRepository.OnGetLinkPrivacyPolicyComplete onGetLinkPrivacyPolicyComplete;
    private final AdministrationRepository.OnGetLinkTermsOfServiceComplete onGetLinkTermsOfServiceComplete;

    DatabaseReference reference;
    DatabaseReference userDataReference;
    DatabaseReference userTypeReference;
    DatabaseReference classJoinReference;

    CollectionReference userDetailsReference;
    DocumentReference classReference;
    CollectionReference credentialReference;
    DocumentReference teacherRatingReference;

    float rating1, rating2, rating3, rating4, rating5;

    public AdministrationRepository(AdministrationRepository.OnGetMimetypesCompleted onGetMimetypesCompleted,
                                    AdministrationRepository.OnGetFileEnabledComplete onGetFileEnabledComplete,
                                    AdministrationRepository.OnGetUserListComplete onGetUserListComplete,
                                    AdministrationRepository.OnGetUserDetailsComplete onGetUserDetailsComplete,
                                    AdministrationRepository.OnGetClassJoinCountComplete onGetClassJoinCountComplete,
                                    AdministrationRepository.OnGetClassCreateCountComplete onGetClassCreateCountComplete,
                                    AdministrationRepository.OnGetListStringDataCountComplete onGetListStringDataCountComplete,
                                    AdministrationRepository.OnGetAdminModelListComplete onGetAdminModelListComplete,
                                    AdministrationRepository.OnUpdateModelListComplete onUpdateModelListComplete,
                                    AdministrationRepository.OnGetZoomCredentialsComplete onGetZoomCredentialsComplete,
                                    AdministrationRepository.OnGetFCMCredentialsComplete onGetFCMCredentialsComplete,
                                    AdministrationRepository.OnGetAdministratorComplete onGetAdministratorComplete,
                                    AdministrationRepository.OnGetSlotLimitComplete onGetSlotLimitComplete,
                                    AdministrationRepository.OnGetFileSizeLimitComplete onGetFileSizeLimitComplete,
                                    AdministrationRepository.OnGetLinkPrivacyPolicyComplete onGetLinkPrivacyPolicyComplete,
                                    AdministrationRepository.OnGetLinkTermsOfServiceComplete onGetLinkTermsOfServiceComplete,
                                    AdministrationRepository.OnGetLecturesLimitComplete onGetLecturesLimitComplete) {

        this.onGetMimetypesCompleted = onGetMimetypesCompleted;
        this.onGetFileEnabledComplete = onGetFileEnabledComplete;
        this.onGetUserListComplete = onGetUserListComplete;
        this.onGetUserDetailsComplete = onGetUserDetailsComplete;
        this.onGetClassJoinCountComplete = onGetClassJoinCountComplete;
        this.onGetClassCreateCountComplete = onGetClassCreateCountComplete;
        this.onGetListStringDataCountComplete = onGetListStringDataCountComplete;
        this.onGetAdminModelListComplete = onGetAdminModelListComplete;
        this.onUpdateModelListComplete = onUpdateModelListComplete;
        this.onGetZoomCredentialsComplete = onGetZoomCredentialsComplete;
        this.onGetFCMCredentialsComplete = onGetFCMCredentialsComplete;
        this.onGetAdministratorComplete = onGetAdministratorComplete;
        this.onGetSlotLimitComplete = onGetSlotLimitComplete;
        this.onGetFileSizeLimitComplete = onGetFileSizeLimitComplete;
        this.onGetLinkPrivacyPolicyComplete = onGetLinkPrivacyPolicyComplete;
        this.onGetLinkTermsOfServiceComplete = onGetLinkTermsOfServiceComplete;
        this.onGetLecturesLimitComplete = onGetLecturesLimitComplete;

        reference = FirebaseDatabase.getInstance().getReference().child("Data").child("Administration");
        userDataReference = FirebaseDatabase.getInstance().getReference().child("Data").child("UserData");
        userTypeReference = FirebaseDatabase.getInstance().getReference().child("Data").child("Main").child("UserType");
        classJoinReference = FirebaseDatabase.getInstance().getReference().child("Data").child("Main").child("Classes").child("ClassJoinState");

        userDetailsReference = FirebaseFirestore.getInstance().collection("UserData");
        classReference = FirebaseFirestore.getInstance().collection("Main").document("Class");
        credentialReference = FirebaseFirestore.getInstance().collection("CREDENTIALS");
        teacherRatingReference = FirebaseFirestore.getInstance().collection("Main").document("Class").collection("Ratings").document("TeacherRating");
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
        reference.child("Filetypes").child("ImagesOnly").addValueEventListener(new ValueEventListener() {
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

    public void updateFilesEnabled(boolean value) {
        reference.child("Filetypes").child("ImagesOnly").setValue(value);
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

    public void getGradeModelList() {
        List<AdminListModel> gradeList = new ArrayList<>();
        reference.child("Lists").child("GradeList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    gradeList.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String value = snapshot1.getValue(String.class);
                        if (value != null && snapshot1.getKey() != null)
                            gradeList.add(new AdminListModel(value, reference.child("Lists").child("GradeList").child(snapshot1.getKey())));
                    }
                    onGetAdminModelListComplete.onGetAdminModelListSuccess(gradeList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onGetAdminModelListComplete.onFailed(error.toString());
            }
        });
    }

    public void getSubjectModelList() {
        List<AdminListModel> gradeList = new ArrayList<>();
        reference.child("Lists").child("SubjectList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    gradeList.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String value = snapshot1.getValue(String.class);
                        if (value != null && snapshot1.getKey() != null)
                            gradeList.add(new AdminListModel(value, reference.child("Lists").child("SubjectList").child(snapshot1.getKey())));
                    }
                    onGetAdminModelListComplete.onGetAdminModelListSuccess(gradeList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onGetAdminModelListComplete.onFailed(error.toString());
            }
        });
    }

    public void getSlotModelList() {
        List<AdminListModel> gradeList = new ArrayList<>();
        reference.child("Lists").child("SlotList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    gradeList.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String value = snapshot1.getValue(String.class);
                        if (value != null && snapshot1.getKey() != null)
                            gradeList.add(new AdminListModel(value, reference.child("Lists").child("SlotList").child(snapshot1.getKey())));
                    }
                    onGetAdminModelListComplete.onGetAdminModelListSuccess(gradeList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onGetAdminModelListComplete.onFailed(error.toString());
            }
        });
    }

    public void getFileList() {
        List<AdminListModel> gradeList = new ArrayList<>();
        reference.child("Filetypes").child("GroupChats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    gradeList.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        mimeTypesModel value = snapshot1.getValue(mimeTypesModel.class);
                        if (value != null && snapshot1.getKey() != null)
                            gradeList.add(new AdminListModel(value.getMimetype(), reference.child("Filetypes").child("GroupChats").child(snapshot1.getKey())));
                    }
                    onGetAdminModelListComplete.onGetAdminModelListSuccess(gradeList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onGetAdminModelListComplete.onFailed(error.toString());
            }
        });
    }

    public void updateGradeModelList(String grade) {
        Calendar cal = Calendar.getInstance();
        long currentTime = cal.getTimeInMillis();
        reference.child("Lists").child("GradeList").child(String.valueOf(currentTime)).setValue(grade).addOnSuccessListener(unused -> onUpdateModelListComplete.onUpdateModelListSuccess(new AdminListModel(grade, reference.child("Lists").child("GradeList").child(String.valueOf(currentTime))))).addOnFailureListener(e -> onUpdateModelListComplete.onFailed(e.toString()));
    }

    public void updateSubjectModelList(String grade) {
        Calendar cal = Calendar.getInstance();
        long currentTime = cal.getTimeInMillis();
        reference.child("Lists").child("SubjectList").child(String.valueOf(currentTime)).setValue(grade).addOnSuccessListener(unused -> onUpdateModelListComplete.onUpdateModelListSuccess(new AdminListModel(grade, reference.child("Lists").child("SubjectList").child(String.valueOf(currentTime))))).addOnFailureListener(e -> onUpdateModelListComplete.onFailed(e.toString()));
    }

    public void updateSlotModelList(String grade) {
        Calendar cal = Calendar.getInstance();
        long currentTime = cal.getTimeInMillis();
        reference.child("Lists").child("SlotList").child(String.valueOf(currentTime)).setValue(grade).addOnSuccessListener(unused -> onUpdateModelListComplete.onUpdateModelListSuccess(new AdminListModel(grade, reference.child("Lists").child("SlotList").child(String.valueOf(currentTime))))).addOnFailureListener(e -> onUpdateModelListComplete.onFailed(e.toString()));
    }

    public void updateFileModelList(mimeTypesModel model) {
        Calendar cal = Calendar.getInstance();
        long currentTime = cal.getTimeInMillis();
        reference.child("Filetypes").child("GroupChats").child(String.valueOf(currentTime)).setValue(model).addOnSuccessListener(unused -> onUpdateModelListComplete.onUpdateModelListSuccess(new AdminListModel(model.getMimetype(), reference.child("Filetypes").child("GroupChats").child(String.valueOf(currentTime))))).addOnFailureListener(e -> onUpdateModelListComplete.onFailed(e.toString()));
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
                        if (snapshot1.getKey() != null)
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
                        if (snapshot1.getKey() != null)
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

    public void getLearnerListForClass(String classID) {
        List<UserData> data = new ArrayList<>();
        classJoinReference.child(classID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (snapshot1.getKey() != null)
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

                                                    teacherRatingReference.collection(UserData.getUid()).get().addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            QuerySnapshot ratingSnapshot = task1.getResult();
                                                            if (ratingSnapshot != null) {
                                                                UserData.setRating(calculateRating(ratingSnapshot.toObjects(RatingModel.class)));
                                                            }
                                                        }
                                                        data.add(UserData);
                                                        onGetUserListComplete.onGetUserListSuccess(data);
                                                    });
                                                }
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
        }).addOnFailureListener(e -> onGetUserDetailsComplete.onFailed(e.toString()));
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
                if (task1.getException() != null)
                    onGetClassCreateCountComplete.onFailed(task1.getException().toString());
            }
        });
    }

    public void getZoomCredentials() {
        credentialReference.document("ZoomCredentials").get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                zoomCredentials zoomCredentials = snapshot.toObject(com.reiserx.nimbleq.Models.zoomCredentials.class);
                onGetZoomCredentialsComplete.onGetZoomCredentialsSuccess(zoomCredentials);
            } else
                onGetZoomCredentialsComplete.onFailed("Failed to get credentials");
        }).addOnFailureListener(e -> onGetZoomCredentialsComplete.onFailed(e.toString()));
    }

    public void getFCMCredentials() {
        credentialReference.document("FCMCREDENTIALS").get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                FCMCREDENTIALS fcmcredentials = snapshot.toObject(com.reiserx.nimbleq.Models.FCMCREDENTIALS.class);
                onGetFCMCredentialsComplete.onGetFCMCredentialsSuccess(fcmcredentials);
            } else
                onGetFCMCredentialsComplete.onFailed("Failed to get credentials");
        }).addOnFailureListener(e -> onGetFCMCredentialsComplete.onFailed(e.toString()));
    }

    public void getAdministrator(String userID) {
        reference.child("Administrators").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    onGetAdministratorComplete.onGetAdminSuccess(true);
                else
                    onGetAdministratorComplete.onAdminFailed("You are not allowed to access this service");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onGetAdministratorComplete.onAdminFailed(error.toString());
            }
        });
    }

    public void getSlotLimit() {
        reference.child("Limits").child("slotLimit").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Long limit = snapshot.getValue(Long.class);
                    onGetSlotLimitComplete.onGetSlotLimitSuccess(limit);
                } else
                    onGetSlotLimitComplete.onAdminFailed("Limit not available");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onGetSlotLimitComplete.onAdminFailed(error.toString());
            }
        });
    }

    public void updateSlotLimit(Long value) {
        reference.child("Limits").child("slotLimit").setValue(value);
    }

    public void getFileSizeLimit() {
        reference.child("Limits").child("fileSize").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Long limit = snapshot.getValue(Long.class);
                    onGetFileSizeLimitComplete.onGetFileSizeLimitSuccess(limit);
                } else
                    onGetFileSizeLimitComplete.onAdminFailed("Limit not available");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onGetFileSizeLimitComplete.onAdminFailed(error.toString());
            }
        });
    }

    public void updateFileSizeLimit(Long value) {
        reference.child("Limits").child("fileSize").setValue(value);
    }

    public void getLecturesLimit() {
        reference.child("Limits").child("lectures").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Long limit = snapshot.getValue(Long.class);
                    onGetLecturesLimitComplete.onGetLecturesLimitSuccess(limit);
                } else
                    onGetLecturesLimitComplete.onAdminFailed("Limit not available");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onGetLecturesLimitComplete.onAdminFailed(error.toString());
            }
        });
    }

    public void updateLecturesLimit(Long value) {
        reference.child("Limits").child("lectures").setValue(value);
    }

    public void getLinkPrivacyPolicy() {
        reference.child("Links").child("privacyPolicy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String value = snapshot.getValue(String.class);
                    onGetLinkPrivacyPolicyComplete.onGetLinkPrivacyPolicySuccess(value);
                } else
                    onGetLinkPrivacyPolicyComplete.onAdminFailed("Limit not available");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onGetLinkPrivacyPolicyComplete.onAdminFailed(error.toString());
            }
        });
    }

    public void updateLinkPrivacyPolicy(String link) {
        reference.child("Links").child("privacyPolicy").setValue(link);
    }

    public void getLinkTermsOfService() {
        reference.child("Links").child("termsOfService").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String value = snapshot.getValue(String.class);
                    onGetLinkTermsOfServiceComplete.onGetLinkTermsOfServiceSuccess(value);
                } else
                    onGetLinkTermsOfServiceComplete.onAdminFailed("Limit not available");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onGetLinkTermsOfServiceComplete.onAdminFailed(error.toString());
            }
        });
    }

    public void updateLinkTermsOfService(String link) {
        reference.child("Links").child("termsOfService").setValue(link);
    }

    float calculateRating(List<RatingModel> ratingModelList) {
        rating1 = rating2 = rating3 = rating4 = rating5 = 0;
        for (RatingModel ratingModel : ratingModelList) {
            switch (ratingModel.getRating()) {
                case 1:
                    rating1++;
                    break;
                case 2:
                    rating2++;
                    break;
                case 3:
                    rating3++;
                    break;
                case 4:
                    rating4++;
                    break;
                case 5:
                    rating5++;
                    break;
            }
        }
        return (5 * rating5 + 4 * rating4 + 3 * rating3 + 2 * rating2 + 1 * rating1) / (rating5 + rating4 + rating3 + rating2 + rating1);
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

    public interface OnGetAdminModelListComplete {
        void onGetAdminModelListSuccess(List<AdminListModel> data);

        void onFailed(String error);
    }

    public interface OnUpdateModelListComplete {
        void onUpdateModelListSuccess(AdminListModel adminListModel);

        void onFailed(String error);
    }

    public interface OnGetZoomCredentialsComplete {
        void onGetZoomCredentialsSuccess(zoomCredentials zoomCredentials);

        void onFailed(String error);
    }

    public interface OnGetFCMCredentialsComplete {
        void onGetFCMCredentialsSuccess(FCMCREDENTIALS fcmcredentials);

        void onFailed(String error);
    }

    public interface OnGetAdministratorComplete {
        void onGetAdminSuccess(Boolean admin);

        void onAdminFailed(String error);
    }

    public interface OnGetSlotLimitComplete {
        void onGetSlotLimitSuccess(Long limit);

        void onAdminFailed(String error);
    }

    public interface OnGetFileSizeLimitComplete {
        void onGetFileSizeLimitSuccess(Long limit);

        void onAdminFailed(String error);
    }

    public interface OnGetLinkPrivacyPolicyComplete {
        void onGetLinkPrivacyPolicySuccess(String value);

        void onAdminFailed(String error);
    }

    public interface OnGetLinkTermsOfServiceComplete {
        void onGetLinkTermsOfServiceSuccess(String Value);

        void onAdminFailed(String error);
    }

    public interface OnGetLecturesLimitComplete {
        void onGetLecturesLimitSuccess(Long limit);

        void onAdminFailed(String error);
    }
}
