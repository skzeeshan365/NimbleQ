package com.reiserx.nimbleq.ViewModels;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.reiserx.nimbleq.Models.fileTypeModel;
import com.reiserx.nimbleq.Models.remoteFileModel;
import com.reiserx.nimbleq.Models.uploadProgressModel;
import com.reiserx.nimbleq.Repository.FirebaseStorageRepository;

import java.util.List;

public class FirebaseStorageViewModel extends ViewModel implements FirebaseStorageRepository.OnFileUploaded, FirebaseStorageRepository.OnSingleFileUploaded {

    private final MutableLiveData<remoteFileModel> remoteFileModelMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<remoteFileModel> remoteFileModelSingleMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<uploadProgressModel> uploadProgressMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<fileTypeModel> uploadStartMutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> databaseErrorMutableLiveData = new MutableLiveData<>();

    private final FirebaseStorageRepository firebaseStorage;

    public MutableLiveData<remoteFileModel> getRemoteFileModelMutableLiveData() {
        return remoteFileModelMutableLiveData;
    }

    public MutableLiveData<remoteFileModel> getRemoteFileModelSingleMutableLiveData() {
        return remoteFileModelSingleMutableLiveData;
    }

    public MutableLiveData<uploadProgressModel> getUploadProgressMutableLiveData() {
        return uploadProgressMutableLiveData;
    }

    public MutableLiveData<fileTypeModel> getUploadStartMutableLiveData() {
        return uploadStartMutableLiveData;
    }

    public MutableLiveData<String> getDatabaseErrorMutableLiveData() {
        return databaseErrorMutableLiveData;
    }

    public void uploadMultipleImages(Context context, String userID, List<Uri> list) {
        firebaseStorage.uploadMultipleImages(context, userID, list);
    }

    public void uploadSingleFile(Context context, String userID, Uri uri) {
        firebaseStorage.uploadSingleFile(context, userID, uri);
    }

    public FirebaseStorageViewModel() {
        firebaseStorage = new FirebaseStorageRepository(this, this);
    }

    @Override
    public void onSuccess(remoteFileModel remoteFileModel) {
        remoteFileModelMutableLiveData.setValue(remoteFileModel);
    }

    @Override
    public void onUploadSuccess(remoteFileModel remoteFileModel) {
        remoteFileModelSingleMutableLiveData.setValue(remoteFileModel);
    }

    @Override
    public void onPreUpload(fileTypeModel fileTypeModel) {
        uploadStartMutableLiveData.setValue(fileTypeModel);
    }

    @Override
    public void onProgress(uploadProgressModel uploadProgressModel) {
        uploadProgressMutableLiveData.setValue(uploadProgressModel);
    }

    @Override
    public void onFailure(String error) {
        databaseErrorMutableLiveData.setValue(error);
    }
}