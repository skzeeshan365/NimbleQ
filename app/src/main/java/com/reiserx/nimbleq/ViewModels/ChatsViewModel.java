package com.reiserx.nimbleq.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.reiserx.nimbleq.Adapters.MessagesAdapter;
import com.reiserx.nimbleq.Models.Message;
import com.reiserx.nimbleq.Repository.ChatsRepository;

import java.util.List;

public class ChatsViewModel extends ViewModel implements ChatsRepository.OnMessageSubmitted, ChatsRepository.OnLoadMessagesComplete, ChatsRepository.OnGetLatestMessageComplete, ChatsRepository.onGetAllMessagesComplete {

    private final MutableLiveData<Void> messageMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Message>> messageListMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Message> latestMessageListMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Message>> allMessagesListMutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> databaseErrorMutableLiveData = new MutableLiveData<>();

    private final ChatsRepository firebaseRepo;

    public MutableLiveData<Void> getMessageMutableLiveData() {
        return messageMutableLiveData;
    }

    public MutableLiveData<List<Message>> getMessageListMutableLiveData() {
        return messageListMutableLiveData;
    }

    public MutableLiveData<Message> getLatestMessageListMutableLiveData() {
        return latestMessageListMutableLiveData;
    }

    public MutableLiveData<List<Message>> getAllMessagesListMutableLiveData() {
        return allMessagesListMutableLiveData;
    }

    public MutableLiveData<String> getDatabaseErrorMutableLiveData() {
        return databaseErrorMutableLiveData;
    }

    public void submitMessage(Message message, String classID) {
        firebaseRepo.submitMessage(message, classID);
    }

    public void getMessages(String classID, int limit) {
        firebaseRepo.getMessages(classID, limit);
    }

    public void getLatestMessages(String classID) {
        firebaseRepo.getLatestMessages(classID);
    }

    public void paginateMessages(String classID, MessagesAdapter list) {
        firebaseRepo.paginateMessages(classID, list);
    }

    public void getAllMessages(String classID, MessagesAdapter adapter) {
        firebaseRepo.getAllMessages(classID, adapter);
    }

    public ChatsViewModel() {
        firebaseRepo = new ChatsRepository(this, this, this, this);
    }

    @Override
    public void onSuccess(Void voids) {
        messageMutableLiveData.setValue(voids);
    }

    @Override
    public void onSuccess(List<Message> messages) {
        messageListMutableLiveData.setValue(messages);
    }

    @Override
    public void onSuccess(Message message) {
        latestMessageListMutableLiveData.setValue(message);
    }

    @Override
    public void onComplete(List<Message> messages) {
        allMessagesListMutableLiveData.setValue(messages);
    }

    @Override
    public void onFailure(String error) {
        databaseErrorMutableLiveData.setValue(error);
    }
}