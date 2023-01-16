package com.reiserx.nimbleq.Repository;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.reiserx.nimbleq.Adapters.MessagesAdapter;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatsRepository {
    private final ChatsRepository.OnMessageSubmitted onMessageSubmitted;
    private final ChatsRepository.OnLoadMessagesComplete onLoadMessagesComplete;
    private final ChatsRepository.OnGetLatestMessageComplete onGetLatestMessageComplete;
    private final ChatsRepository.onGetAllMessagesComplete onGetAllMessagesComplete;

    private final CollectionReference reference;

    DocumentSnapshot lastVisible;

    Message message;

    public ChatsRepository(ChatsRepository.OnMessageSubmitted onMessageSubmitted, ChatsRepository.OnLoadMessagesComplete onLoadMessagesComplete, ChatsRepository.OnGetLatestMessageComplete onGetLatestMessageComplete, ChatsRepository.onGetAllMessagesComplete onGetAllMessagesComplete) {
        this.onMessageSubmitted = onMessageSubmitted;
        this.onLoadMessagesComplete = onLoadMessagesComplete;
        this.onGetLatestMessageComplete = onGetLatestMessageComplete;
        this.onGetAllMessagesComplete = onGetAllMessagesComplete;

        reference = FirebaseFirestore.getInstance().collection("Main").document("Class").collection("Message");
    }

    public void submitMessage(Message message, String classID) {
        Calendar cal = Calendar.getInstance();
        long currentTime = cal.getTimeInMillis();
        reference.document(classID).collection("Groupchat").document(String.valueOf(currentTime))
                .set(message).addOnSuccessListener(documentReference -> onMessageSubmitted.onSuccess(null)).addOnFailureListener(e -> onMessageSubmitted.onFailure(e.toString()));
    }

    public void getMessages(String classID, int limit) {
        List<Message> data = new ArrayList<>();
        Query query = reference.document(classID).collection("Groupchat").orderBy("queryStamp").limitToLast(limit);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                data.clear();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    Message message = documentSnapshot.toObject(Message.class);
                    if (message != null) {
                        message.setMessageId(documentSnapshot.getId());
                    }
                    data.add(message);
                }
                lastVisible = queryDocumentSnapshots.getDocuments().get(0);
                onLoadMessagesComplete.onSuccess(data);
            } else onLoadMessagesComplete.onFailure("No message available");
        }).addOnFailureListener(e -> onLoadMessagesComplete.onFailure(e.toString()));
    }

    public void getAllMessages(String classID, MessagesAdapter adapter) {
        if (adapter.getList() != null)
            adapter.getList().clear();
        List<Message> data = new ArrayList<>();
        Query query = reference.document(classID).collection("Groupchat").orderBy("queryStamp");
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    Message message = documentSnapshot.toObject(Message.class);
                    if (message != null) {
                        message.setMessageId(documentSnapshot.getId());
                    }
                    data.add(message);
                }
                lastVisible = queryDocumentSnapshots.getDocuments().get(0);
                onGetAllMessagesComplete.onComplete(data);
            } else onGetAllMessagesComplete.onFailure("No message available");
        }).addOnFailureListener(e -> onGetAllMessagesComplete.onFailure(e.toString()));
    }

    public void getLatestMessages(String classID) {
        Query query = reference.document(classID).collection("Groupchat").orderBy("queryStamp").limitToLast(1);
        query.addSnapshotListener((value, error) -> {
            if (value != null) {
                if (!value.isEmpty()) {
                    for (DocumentChange documentSnapshot : value.getDocumentChanges()) {
                        switch (documentSnapshot.getType()) {
                            case ADDED:
                                message = documentSnapshot.getDocument().toObject(Message.class);
                                message.setMessageId(documentSnapshot.getDocument().getId());
                                break;
                            case MODIFIED:
                                Log.d(CONSTANTS.TAG, "Modified city: " + documentSnapshot.getDocument().getData());
                                break;
                            case REMOVED:
                                Log.d(CONSTANTS.TAG, "Removed city: " + documentSnapshot.getDocument().getData());
                                break;
                        }
                    }

                    onGetLatestMessageComplete.onSuccess(message);
                } else onGetLatestMessageComplete.onFailure("No message available");
            } else if (error != null) {
                onGetLatestMessageComplete.onFailure(error.toString());
            }
        });
    }

    public void paginateMessages(String classID, MessagesAdapter list) {
        Query query = reference.document(classID).collection("Groupchat").orderBy("queryStamp", Query.Direction.DESCENDING).startAfter(lastVisible).limit(5);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    message = documentSnapshot.toObject(Message.class);
                    if (message != null) {
                        message.setMessageId(documentSnapshot.getId());
                    }
                    if (message != null) {
                        list.addDataAt0(message);
                    }
                }
                lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
            } else onGetLatestMessageComplete.onFailure("No message available");
        }).addOnFailureListener(e -> onGetLatestMessageComplete.onFailure(e.toString()));
    }

    public interface OnMessageSubmitted {
        void onSuccess(Void voids);

        void onFailure(String error);
    }

    public interface OnLoadMessagesComplete {
        void onSuccess(List<Message> messages);

        void onFailure(String error);
    }

    public interface OnGetLatestMessageComplete {
        void onSuccess(Message message);

        void onFailure(String error);
    }

    public interface onGetAllMessagesComplete {
        void onComplete(List<Message> messages);

        void onFailure(String error);
    }
}
