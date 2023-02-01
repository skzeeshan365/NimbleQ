package com.reiserx.nimbleq.Repository;

import android.content.Context;

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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.ClassRequestModel;
import com.reiserx.nimbleq.Models.RatingModel;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.Models.classModel;
import com.reiserx.nimbleq.Models.subjectAndTimeSlot;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.Notify;
import com.reiserx.nimbleq.Utils.TopicSubscription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassRepository {

    private final ClassRepository.OnRealtimeDbTaskComplete onRealtimeDbTaskComplete;
    private final ClassRepository.OnClassJoinStateChanged OnClassJoinStateChanged;
    private final ClassRepository.OnGetClassListComplete OnGetClassListComplete;
    private final ClassRepository.onGetClassRequestComplete onGetClassRequestComplete;
    private final ClassRepository.OnRatingSubmitted onRatingSubmitted;
    private final ClassRepository.OnCreateClassComplete onCreateClassComplete;
    private final ClassRepository.OnGetRatingsComplete onGetRatingsComplete;

    private final DocumentReference reference;
    private final DatabaseReference classJoinReference;
    private final DatabaseReference userDataReference;
    Query query;

    float rating1, rating2, rating3, rating4, rating5;

    public ClassRepository(ClassRepository.OnRealtimeDbTaskComplete onRealtimeDbTaskComplete,
                           ClassRepository.OnClassJoinStateChanged OnClassJoinStateChanged,
                           ClassRepository.OnGetClassListComplete OnGetClassListComplete,
                           ClassRepository.onGetClassRequestComplete onGetClassRequestComplete,
                           ClassRepository.OnRatingSubmitted onRatingSubmitted,
                           ClassRepository.OnCreateClassComplete onCreateClassComplete,
                           ClassRepository.OnGetRatingsComplete onGetRatingsComplete) {

        this.onRealtimeDbTaskComplete = onRealtimeDbTaskComplete;
        this.OnClassJoinStateChanged = OnClassJoinStateChanged;
        this.OnGetClassListComplete = OnGetClassListComplete;
        this.onGetClassRequestComplete = onGetClassRequestComplete;
        this.onRatingSubmitted = onRatingSubmitted;
        this.onCreateClassComplete = onCreateClassComplete;
        this.onGetRatingsComplete = onGetRatingsComplete;


        reference = FirebaseFirestore.getInstance().collection("Main").document("Class");
        classJoinReference = FirebaseDatabase.getInstance().getReference().child("Data").child("Main").child("Classes").child("ClassJoinState");
        userDataReference = FirebaseDatabase.getInstance().getReference().child("Data").child("UserData");
    }

    public void createClass(Context context, classModel classModel, String teacherName, ClassRequestModel request) {
        reference.collection("ClassInfo").add(classModel).addOnSuccessListener(reference -> {
            Notify notify = new Notify(context);
            String classNmae = context.getString(R.string.class1) + " " + classModel.getClassName() + "\n";
            String subject = context.getString(R.string.subject1) + " " + classModel.getSubject() + "\n";
            String topic = context.getString(R.string.topic1) + classModel.getTopic() + "\n";
            String schedule = context.getString(R.string.time1) + " " + classModel.getTime_slot() + "\n";
            String teacher = context.getString(R.string.teacher1) + " " + teacherName;
            String message = classNmae + subject + topic + schedule + teacher;

            notify.createClassPayload(context.getString(R.string.new_class_has_been_created_based_on_your_slot), message, TopicSubscription.getTopicForSlot(classModel), reference.getId());
            onCreateClassComplete.onClassCreated(reference.getId());

            userDataReference.child(request.getStudentID()).child("FCM_TOKEN").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String token = snapshot.getValue(String.class);
                        if (token != null)
                            notify.createClassPayloadForSingleUser(context.getString(R.string.new_class_has_been_created_based_on_your_request), message, token, reference.getId());
                    }

                    request.setAccepted(reference.getId());

                    FirebaseFirestore.getInstance().collection("Main").document("Class").collection("ClassRequests").document(request.getId()).set(request).addOnSuccessListener(unused -> {
                    }).addOnFailureListener(e -> Log.d(CONSTANTS.TAG2, e.toString()));
                    onCreateClassComplete.onClassCreated(reference.getId());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }).addOnFailureListener(e -> onCreateClassComplete.onFailure(e.toString()));
    }

    public void createClass(Context context, classModel classModel, String teacherName) {

        reference.collection("ClassInfo").add(classModel).addOnSuccessListener(reference -> {
            Notify notify = new Notify(context);
            String classNmae = context.getString(R.string.class1) + " " + classModel.getClassName() + "\n";
            String subject = context.getString(R.string.subject1) + " " + classModel.getSubject() + "\n";
            String topic = context.getString(R.string.topic1) + " " + classModel.getTopic() + "\n";
            String schedule = context.getString(R.string.time1) + " " + classModel.getTime_slot() + "\n";
            String teacher = context.getString(R.string.teacher1) + " " + teacherName;
            String message = classNmae + subject + topic + schedule + teacher;

            notify.createClassPayload(context.getString(R.string.new_class_has_been_created_based_on_your_slot), message, TopicSubscription.getTopicForSlot(classModel), reference.getId());
            onCreateClassComplete.onClassCreated(reference.getId());
        }).addOnFailureListener(e -> onCreateClassComplete.onFailure(e.toString()));
    }

    public void getClassData(String classID) {
        DocumentReference documentReference = reference.collection("ClassInfo").document(classID);

        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                com.reiserx.nimbleq.Models.classModel models = documentSnapshot.toObject(classModel.class);
                if (models != null) {
                    models.setClassID(classID);

                    reference.collection("Ratings").document("ClassRating").collection(models.getClassID()).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            QuerySnapshot snapshot = task1.getResult();
                            if (snapshot != null) {
                                models.setRating(calculateRating(snapshot.toObjects(RatingModel.class)));
                            }
                        }
                        onRealtimeDbTaskComplete.onSuccess(models);
                    });
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
                            notify.classJoinPayload(context.getString(R.string.new_learner_title) + " ", username.concat(" " + context.getString(R.string.joined_class)), token, classID);
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
                            notify.classJoinPayload(context.getString(R.string.left_class_title) + " ", username.concat(" " + context.getString(R.string.has_left_class)), token, classID);
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
                .whereEqualTo("time_slot", subjectAndTimeSlot.getTimeSlot());

        query.get().addOnSuccessListener(task -> {
            if (task != null) {
                if (!task.isEmpty()) {
                    for (DocumentSnapshot document : task.getDocuments()) {
                        classModel classModel = document.toObject(com.reiserx.nimbleq.Models.classModel.class);
                        if (classModel != null) {
                            classModel.setClassID(document.getId());
                            if (!classModel.getTeacher_info().equals(userID)) {

                                reference.collection("Ratings").document("ClassRating").collection(classModel.getClassID()).get().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        QuerySnapshot snapshot = task1.getResult();
                                        if (snapshot != null) {
                                            classModel.setRating(calculateRating(snapshot.toObjects(RatingModel.class)));
                                        }
                                    }
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
                                });
                            } else
                                OnGetClassListComplete.onGetClassListFailure("Class not available");
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
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                data.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    classModel classModel = document.toObject(com.reiserx.nimbleq.Models.classModel.class);
                    classModel.setClassID(document.getId());

                    reference.collection("Ratings").document("ClassRating").collection(classModel.getClassID()).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            QuerySnapshot ratingSnapshot = task1.getResult();
                            if (ratingSnapshot != null) {
                                classModel.setRating(calculateRating(ratingSnapshot.toObjects(RatingModel.class)));
                            }
                        }
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
                    });
                }
            } else {
                OnGetClassListComplete.onGetClassListFailure("Failed to get class list");
            }
        });
    }

    public void getAllJoinedClasses(String userID) {
        List<classModel> data = new ArrayList<>();
        com.google.firebase.database.Query query = classJoinReference.orderByChild(userID).equalTo(userID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (snapshot1.getKey() != null)
                            reference.collection("ClassInfo").document(snapshot1.getKey()).get().addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    com.reiserx.nimbleq.Models.classModel models = documentSnapshot.toObject(classModel.class);
                                    if (models != null) {
                                        models.setClassID(documentSnapshot.getId());

                                        reference.collection("Ratings").document("ClassRating").collection(models.getClassID()).get().addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                QuerySnapshot ratingSnapshot = task1.getResult();
                                                if (ratingSnapshot != null) {
                                                    models.setRating(calculateRating(ratingSnapshot.toObjects(RatingModel.class)));
                                                }
                                            }
                                            userDataReference.child(models.getTeacher_info()).child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        String username = snapshot.getValue(String.class);
                                                        if (username != null) {
                                                            models.setTeacher_name(username);
                                                            data.add(models);
                                                        }
                                                    }
                                                    OnGetClassListComplete.onSuccess(data);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    OnGetClassListComplete.onGetClassListFailure(error.toString());
                                                }
                                            });
                                        });
                                    }
                                }
                            });
                    }
                } else
                    OnGetClassListComplete.onGetClassListFailure("You have not joined any class yet");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                OnGetClassListComplete.onGetClassListFailure(error.toString());
            }
        });
    }

    public void getClassRequestsForStudents(subjectAndTimeSlot subjectAndTimeSlot, String userID) {
        Log.d(CONSTANTS.TAG, subjectAndTimeSlot.getSubject());
        List<ClassRequestModel> requestModelList = new ArrayList<>();
        Query query = reference.collection("ClassRequests")
                .whereEqualTo("subject", subjectAndTimeSlot.getSubject())
                .whereEqualTo("studentID", userID);
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

    public void getClassRequestsForTeachers(subjectAndTimeSlot subjectAndTimeSlot, String userID) {
        Log.d(CONSTANTS.TAG, subjectAndTimeSlot.getSubject());
        List<ClassRequestModel> requestModelList = new ArrayList<>();
        Query query = reference.collection("ClassRequests")
                .whereEqualTo("subject", subjectAndTimeSlot.getSubject());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                requestModelList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    ClassRequestModel classModel = document.toObject(com.reiserx.nimbleq.Models.ClassRequestModel.class);
                    if (!classModel.getStudentID().equals(userID) && classModel.isAccepted() == null) {
                        classModel.setId(document.getId());
                        requestModelList.add(classModel);
                    }
                }
                if (!requestModelList.isEmpty())
                    onGetClassRequestComplete.onGetClassRequestsSuccess(requestModelList);
                else
                    onGetClassRequestComplete.onGetClassListFailure("No requests available");
            } else
                onGetClassRequestComplete.onGetClassListFailure("Failed to get class list");
        }).addOnFailureListener(e -> onGetClassRequestComplete.onGetClassListFailure(e.toString()));
    }

    public void setClassRating(String classID, String className, UserData userID, RatingModel ratingModel, String token, Context context) {
        reference.collection("Ratings").document("ClassRating").collection(classID).document(userID.getUid()).set(ratingModel).addOnSuccessListener(unused -> {
            Notify notify = new Notify(context);
            if (ratingModel.getFeedback() == null)
                notify.classReviewPayload(context.getString(R.string.new_feedback_for_your_class).concat(className), userID.getUserName() + " ".concat(context.getString(R.string.has_given) + " " + ratingModel.getRating() + " " + context.getString(R.string.star_rating)), token);
            else
                notify.classReviewPayload(context.getString(R.string.new_feedback_for_your_class).concat(className), userID.getUserName() + " ".concat(context.getString(R.string.has_given) + " " + ratingModel.getRating() + " " + context.getString(R.string.star_rating_with_feedback) + " " + ratingModel.getFeedback()), token);
            onRatingSubmitted.onSuccess(null);
        }).addOnFailureListener(e -> onRatingSubmitted.onFailure(e.toString()));
    }

    public void getClassRating(String classID) {
        List<RatingModel> ratingModelList = new ArrayList<>();
        reference.collection("Ratings").document("ClassRating").collection(classID).get().addOnSuccessListener(task1 -> {
            if (!task1.isEmpty()) {
                for (DocumentSnapshot documentSnapshot : task1.getDocuments()) {
                    RatingModel ratingModel = documentSnapshot.toObject(RatingModel.class);
                    if (ratingModel != null) {
                        userDataReference.child(ratingModel.getUserID()).child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String username = snapshot.getValue(String.class);
                                    if (username != null) {
                                        ratingModel.setName(username);
                                        ratingModelList.add(ratingModel);
                                    }
                                }
                                onGetRatingsComplete.onGetRatingsSuccess(ratingModelList);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                onGetRatingsComplete.onFailure(error.toString());
                            }
                        });
                    }
                }
            } else
                onGetRatingsComplete.onFailure("Ratings not available");
        }).addOnFailureListener(e -> onGetRatingsComplete.onFailure(e.toString()));
    }

    public void setTeacherRating(String teacherID, UserData userID, RatingModel ratingModel, String token, Context context) {
        reference.collection("Ratings").document("TeacherRating").collection(teacherID).document(userID.getUid()).set(ratingModel).addOnSuccessListener(unused -> {
            Notify notify = new Notify(context);
            if (ratingModel.getFeedback() == null)
                notify.classReviewPayload(context.getString(R.string.new_feedback_for_you), userID.getUserName() + " ".concat(context.getString(R.string.has_given) + " " + ratingModel.getRating() + " " + context.getString(R.string.star_rating)), token);
            else
                notify.classReviewPayload(context.getString(R.string.new_feedback_for_you), userID.getUserName() + " ".concat(context.getString(R.string.has_given) + " " + ratingModel.getRating() + " " + context.getString(R.string.star_rating_with_feedback) + " " + ratingModel.getFeedback()), token);
            onRatingSubmitted.onSuccess(null);
        }).addOnFailureListener(e -> onRatingSubmitted.onFailure(e.toString()));
    }

    public void getTeacherRating(String userID) {
        List<RatingModel> ratingModelList = new ArrayList<>();
        reference.collection("Ratings").document("TeacherRating").collection(userID).get().addOnSuccessListener(task1 -> {
            if (!task1.isEmpty()) {
                for (DocumentSnapshot documentSnapshot : task1.getDocuments()) {
                    RatingModel ratingModel = documentSnapshot.toObject(RatingModel.class);
                    if (ratingModel != null) {
                        userDataReference.child(ratingModel.getUserID()).child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String username = snapshot.getValue(String.class);
                                    if (username != null) {
                                        ratingModel.setName(username);
                                        ratingModelList.add(ratingModel);
                                    }
                                }
                                onGetRatingsComplete.onGetRatingsSuccess(ratingModelList);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                onGetRatingsComplete.onFailure(error.toString());
                            }
                        });
                    }
                }
            } else
                onGetRatingsComplete.onFailure("Ratings not available");
        }).addOnFailureListener(e -> onGetRatingsComplete.onFailure(e.toString()));
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

    public void getClassListByDemand() {
        List<classModel> data = new ArrayList<>();
        reference.collection("ClassInfo").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                data.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    classModel classModel = document.toObject(com.reiserx.nimbleq.Models.classModel.class);
                    classModel.setClassID(document.getId());

                    reference.collection("Ratings").document("ClassRating").collection(classModel.getClassID()).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            QuerySnapshot ratingSnapshot = task1.getResult();
                            if (ratingSnapshot != null) {
                                classModel.setRating(calculateRating(ratingSnapshot.toObjects(RatingModel.class)));
                            }
                        }
                        classJoinReference.child(classModel.getClassID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    classModel.setStudent_count(snapshot.getChildrenCount());
                                }
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
                                        Collections.sort(data, (lhs, rhs) -> Long.compare(rhs.getStudent_count(), lhs.getStudent_count()));
                                        OnGetClassListComplete.onSuccess(data);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        OnGetClassListComplete.onGetClassListFailure(error.toString());
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    });
                }
            } else {
                OnGetClassListComplete.onGetClassListFailure("Failed to get class list");
            }
        });
    }

    public void getClassListByRating() {
        List<classModel> data = new ArrayList<>();
        reference.collection("ClassInfo").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                data.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    classModel classModel = document.toObject(com.reiserx.nimbleq.Models.classModel.class);
                    classModel.setClassID(document.getId());

                    reference.collection("Ratings").document("ClassRating").collection(classModel.getClassID()).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            QuerySnapshot ratingSnapshot = task1.getResult();
                            if (ratingSnapshot != null) {
                                classModel.setRating(calculateRating(ratingSnapshot.toObjects(RatingModel.class)));
                            }
                        }
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
                                Collections.sort(data, (lhs, rhs) -> Float.compare(lhs.getRating(), rhs.getRating()));
                                OnGetClassListComplete.onSuccess(data);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                OnGetClassListComplete.onGetClassListFailure(error.toString());
                            }
                        });
                    });
                }
            } else {
                OnGetClassListComplete.onGetClassListFailure("Failed to get class list");
            }
        });
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

    public interface OnRatingSubmitted {
        void onSuccess(Void voids);

        void onFailure(String error);
    }

    public interface OnCreateClassComplete {
        void onClassCreated(String classID);

        void onFailure(String error);
    }

    public interface OnGetRatingsComplete {
        void onGetRatingsSuccess(List<RatingModel> ratingModelList);

        void onFailure(String error);
    }
}
