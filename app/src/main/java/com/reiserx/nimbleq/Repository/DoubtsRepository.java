package com.reiserx.nimbleq.Repository;

import static com.reiserx.nimbleq.Constants.CONSTANTS.TAG;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.reiserx.nimbleq.Adapters.Doubts.DoubtsAdapter;
import com.reiserx.nimbleq.Adapters.MessagesAdapter;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.Announcements.linkModel;
import com.reiserx.nimbleq.Models.Doubts.AnswerModel;
import com.reiserx.nimbleq.Models.Doubts.DoubtsModel;
import com.reiserx.nimbleq.Models.Message;
import com.reiserx.nimbleq.Models.remoteLinks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DoubtsRepository {
    private final DoubtsRepository.OnDoubtSubmitted onDoubtSubmitted;
    private final DoubtsRepository.OnAnswerSubmitted onAnswerSubmitted;
    private final DoubtsRepository.OnGetDoubtsComplete onGetDoubtsComplete;
    private final DoubtsRepository.OnGetAnswersComplete onGetAnswerComplete;
    private final DoubtsRepository.OnPaginationComplete onPaginationComplete;

    private final DocumentReference reference;
    private final DatabaseReference databaseReference;

    DocumentSnapshot lastVisible;

    public DoubtsRepository(DoubtsRepository.OnDoubtSubmitted onDoubtSubmitted,
                            DoubtsRepository.OnGetDoubtsComplete onGetDoubtsComplete,
                            DoubtsRepository.OnAnswerSubmitted onAnswerSubmitted,
                            DoubtsRepository.OnGetAnswersComplete onGetAnswerComplete,
                            DoubtsRepository.OnPaginationComplete onPaginationComplete) {
        this.onGetDoubtsComplete = onGetDoubtsComplete;
        this.onDoubtSubmitted = onDoubtSubmitted;
        this.onAnswerSubmitted = onAnswerSubmitted;
        this.onGetAnswerComplete = onGetAnswerComplete;
        this.onPaginationComplete = onPaginationComplete;

        reference = FirebaseFirestore.getInstance().collection("Main").document("Doubts");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Data").child("UserData");
    }

    public void submitDoubt(DoubtsModel doubtsModel, List<linkModel> links) {
        Calendar cal = Calendar.getInstance();
        long currentTime = cal.getTimeInMillis();

        doubtsModel.setTimeStamp(currentTime);

        reference.collection("DoubtList").document(String.valueOf(currentTime)).set(doubtsModel).addOnSuccessListener(references -> {
            if (!links.isEmpty()) {
                for (linkModel linkModel : links) {
                    reference.collection("DoubtList").document(String.valueOf(currentTime)).collection("linkModels").add(linkModel);
                }
            }
            onDoubtSubmitted.onSuccess(null);
        }).addOnFailureListener(e -> onDoubtSubmitted.onFailure(e.toString()));
    }

    public void submitAnswer(AnswerModel answerModel, List<linkModel> links) {
        Calendar cal = Calendar.getInstance();
        long currentTime = cal.getTimeInMillis();

        answerModel.setTimeStamp(currentTime);

        reference.collection("AnswerList").document(String.valueOf(currentTime)).set(answerModel).addOnSuccessListener(references -> {
            if (!links.isEmpty()) {
                for (linkModel linkModel : links) {
                    reference.collection("AnswerList").document(String.valueOf(currentTime)).collection("linkModels").add(linkModel);
                }
            }
            onAnswerSubmitted.onAnswerSubmitted(null);
        }).addOnFailureListener(e -> onAnswerSubmitted.onFailure(e.toString()));
    }

    public void getDoubtsForTeacher(String subject) {
        List<DoubtsModel> doubtsModelList = new ArrayList<>();
        Query query = reference.collection("DoubtList")
                .whereEqualTo("subject", subject)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .limit(5);
        query.get().addOnCompleteListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isSuccessful() && !queryDocumentSnapshots.getResult().isEmpty()) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getResult()) {
                    DoubtsModel doubtsModel = documentSnapshot.toObject(DoubtsModel.class);
                    if (doubtsModel != null) {
                        reference.collection("DoubtList").document(documentSnapshot.getId()).collection("linkModels").get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot snapshot = task.getResult();
                                if (snapshot != null) {
                                    List<linkModel> linkModels = snapshot.toObjects(linkModel.class);

                                    Query query1 = reference.collection("AnswerList").whereEqualTo("doubt_ID", documentSnapshot.getId()).orderBy("timeStamp", Query.Direction.DESCENDING);
                                    AggregateQuery countQuery = query1.count();
                                    countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            AggregateQuerySnapshot snapshot1 = task1.getResult();
                                            doubtsModel.setAnswerCount(snapshot1.getCount());
                                            doubtsModel.setLinkModels(linkModels);
                                            doubtsModel.setId(documentSnapshot.getId());
                                            doubtsModelList.add(doubtsModel);

                                            onGetDoubtsComplete.onGetDoubtsSuccess(doubtsModelList);
                                        } else {
                                            onGetDoubtsComplete.onFailure(task1.getException().toString());
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
                lastVisible = queryDocumentSnapshots.getResult().getDocuments().get(queryDocumentSnapshots.getResult().size() - 1);
            } else
                onGetDoubtsComplete.onFailure("Doubts not available");
        }).addOnFailureListener(e -> {
            onGetDoubtsComplete.onFailure(e.toString());
        });
    }

    public void getDoubtsForStudent(String userID) {
        List<DoubtsModel> doubtsModelList = new ArrayList<>();
        Query query = reference.collection("DoubtList")
                .whereEqualTo("userID", userID)
                .orderBy("timeStamp", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getResult()) {
                    DoubtsModel doubtsModel = documentSnapshot.toObject(DoubtsModel.class);
                    if (doubtsModel != null) {
                        reference.collection("DoubtList").document(documentSnapshot.getId()).collection("linkModels").get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot snapshot = task.getResult();
                                if (snapshot != null) {
                                    List<linkModel> linkModels = snapshot.toObjects(linkModel.class);

                                    Query query1 = reference.collection("AnswerList").whereEqualTo("doubt_ID", documentSnapshot.getId()).orderBy("timeStamp", Query.Direction.DESCENDING);
                                    AggregateQuery countQuery = query1.count();
                                    countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            AggregateQuerySnapshot snapshot1 = task1.getResult();
                                            doubtsModel.setAnswerCount(snapshot1.getCount());
                                            doubtsModel.setLinkModels(linkModels);
                                            doubtsModel.setId(documentSnapshot.getId());
                                            doubtsModelList.add(doubtsModel);

                                            onGetDoubtsComplete.onGetDoubtsSuccess(doubtsModelList);
                                        } else {
                                            onGetDoubtsComplete.onFailure(task1.getException().toString());
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            } else
                onGetDoubtsComplete.onFailure("Doubts not available");
        }).addOnFailureListener(e -> {
            onGetDoubtsComplete.onFailure(e.toString());
        });
    }

    public void getAnswers(String DOUBT_ID) {
        List<AnswerModel> answerModelList = new ArrayList<>();
        Query query = reference.collection("AnswerList").whereEqualTo("doubt_ID", DOUBT_ID).orderBy("timeStamp", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isSuccessful() && !queryDocumentSnapshots.getResult().getDocuments().isEmpty()) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getResult()) {
                    AnswerModel answerModel = documentSnapshot.toObject(AnswerModel.class);
                    if (answerModel != null) {
                        reference.collection("AnswerList").document(documentSnapshot.getId()).collection("linkModels").get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot snapshot = task.getResult();
                                if (snapshot != null) {
                                    List<linkModel> linkModels = snapshot.toObjects(linkModel.class);
                                    answerModel.setLinkModels(linkModels);
                                    answerModel.setId(documentSnapshot.getId());
                                        databaseReference.child(answerModel.getTEACHER_UID()).child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    String username = snapshot.getValue(String.class);
                                                    if (username != null) {
                                                        answerModel.setTeacherName(username);
                                                        answerModelList.add(answerModel);
                                                        Log.d(CONSTANTS.TAG2, "exist");
                                                    }
                                                }
                                                onGetAnswerComplete.onSuccess(answerModelList);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                onGetAnswerComplete.onFailure(error.toString());
                                            }
                                        });
                                } else
                                    onGetAnswerComplete.onFailure("Answers not available");
                            } else
                                onGetAnswerComplete.onFailure("Answers not available");
                        }).addOnFailureListener(e -> onGetAnswerComplete.onFailure("Answers not available"));
                    } else
                        onGetAnswerComplete.onFailure("Answers not available");
                }
            } else
                onGetAnswerComplete.onFailure("Answers not available");
        }).addOnFailureListener(e -> {
            onGetAnswerComplete.onFailure(e.toString());
        });
    }

    public void paginateMessages(String subject, DoubtsAdapter adapter) {
        if (lastVisible != null) {
            Log.d(CONSTANTS.TAG2, String.valueOf(lastVisible));
        List<DoubtsModel> doubtsModelList = new ArrayList<>();
        Query query = reference.collection("DoubtList")
                .whereEqualTo("subject", subject)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(5);
            query.get().addOnCompleteListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots.isSuccessful() && !queryDocumentSnapshots.getResult().isEmpty()) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getResult()) {
                        DoubtsModel doubtsModel = documentSnapshot.toObject(DoubtsModel.class);
                        if (doubtsModel != null) {
                            reference.collection("DoubtList").document(documentSnapshot.getId()).collection("linkModels").get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    QuerySnapshot snapshot = task.getResult();
                                    if (snapshot != null) {
                                        List<linkModel> linkModels = snapshot.toObjects(linkModel.class);

                                        Query query1 = reference.collection("AnswerList").whereEqualTo("doubt_ID", documentSnapshot.getId()).orderBy("timeStamp", Query.Direction.DESCENDING);
                                        AggregateQuery countQuery = query1.count();
                                        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                AggregateQuerySnapshot snapshot1 = task1.getResult();
                                                doubtsModel.setAnswerCount(snapshot1.getCount());
                                                doubtsModel.setLinkModels(linkModels);
                                                doubtsModel.setId(documentSnapshot.getId());
                                                adapter.addData(doubtsModel);
                                            } else {
                                                onPaginationComplete.onPaginateFailure(task1.getException().toString());
                                            }
                                            onPaginationComplete.onPaginateSuccess(doubtsModelList);
                                        });
                                    }
                                }
                            });
                        }
                    }
                    lastVisible = queryDocumentSnapshots.getResult().getDocuments().get(queryDocumentSnapshots.getResult().size() - 1);
                } else
                    onPaginationComplete.onPaginateFailure("Loaded all doubts");
            }).addOnFailureListener(e -> {
                onPaginationComplete.onPaginateFailure(e.toString());
            });
        }
    }

    public interface OnDoubtSubmitted {
        void onSuccess(Void voids);

        void onFailure(String error);
    }

    public interface OnGetDoubtsComplete {
        void onGetDoubtsSuccess(List<DoubtsModel> doubtsModelList);

        void onFailure(String error);
    }

    public interface OnAnswerSubmitted {
        void onAnswerSubmitted(Void voids);

        void onFailure(String error);
    }

    public interface OnGetAnswersComplete {
        void onSuccess(List<AnswerModel> answerModelList);

        void onFailure(String error);
    }

    public interface OnPaginationComplete {
        void onPaginateSuccess(List<DoubtsModel> doubtsModelList);

        void onPaginateFailure(String error);
    }
}
