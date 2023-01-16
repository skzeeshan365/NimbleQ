package com.reiserx.nimbleq.ViewModels;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.reiserx.nimbleq.Adapters.MessagesAdapter;
import com.reiserx.nimbleq.Models.Message;
import com.reiserx.nimbleq.Models.remoteFileModel;
import com.reiserx.nimbleq.Repository.ChatsRepository;
import com.reiserx.nimbleq.Repository.FirebaseStorageRepository;

import java.util.List;

public class FirebaseStorageViewModel extends ViewModel implements FirebaseStorageRepository.OnFileUploaded {

    private final MutableLiveData<remoteFileModel> remoteFileModelMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> databaseErrorMutableLiveData = new MutableLiveData<>();

    private final FirebaseStorageRepository firebaseStorage;

    public MutableLiveData<remoteFileModel> getRemoteFileModelMutableLiveData() {
        return remoteFileModelMutableLiveData;
    }

    public MutableLiveData<String> getDatabaseErrorMutableLiveData() {
        return databaseErrorMutableLiveData;
    }

    public void uploadMultipleImages(Context context, String userID, List<Uri> list) {
        firebaseStorage.uploadMultipleImages(context, userID, list);
    }
    public FirebaseStorageViewModel() {
        firebaseStorage = new FirebaseStorageRepository(this);
    }

    @Override
    public void onSuccess(remoteFileModel remoteFileModel) {
        remoteFileModelMutableLiveData.setValue(remoteFileModel);
    }

    @Override
    public void onFailure(String error) {
        databaseErrorMutableLiveData.setValue(error);
    }
}