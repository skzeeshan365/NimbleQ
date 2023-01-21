package com.reiserx.nimbleq.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.reiserx.nimbleq.Repository.AdministrationRepository;

import java.util.List;

public class AdministrationViewModel extends ViewModel implements AdministrationRepository.OnGetMimetypesCompleted, AdministrationRepository.OnGetFileEnabledComplete {

    private final MutableLiveData<List<String>> mimeTypesListMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> fileEnabledMutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> databaseErrorMutableLiveData = new MutableLiveData<>();

    private final AdministrationRepository firebaseRepo;

    public MutableLiveData<List<String>> getMimeTypesListMutableLiveData() {
        return mimeTypesListMutableLiveData;
    }

    public MutableLiveData<Boolean> getFileEnabledMutableLiveData() {
        return fileEnabledMutableLiveData;
    }

    public MutableLiveData<String> getDatabaseErrorMutableLiveData() {
        return databaseErrorMutableLiveData;
    }

    public AdministrationViewModel() {
        firebaseRepo = new AdministrationRepository(this, this);
    }

    public void getMimeTypesForGroupChats() {
        firebaseRepo.getMimeTypesForGroupChats();
    }

    public void getFileEnabled() {
        firebaseRepo.getFilesEnabled();
    }

    @Override
    public void onSuccess(List<String> mimetypes) {
        mimeTypesListMutableLiveData.setValue(mimetypes);
    }

    @Override
    public void onSuccess(Boolean enabled) {
        fileEnabledMutableLiveData.setValue(enabled);
    }

    @Override
    public void onFailure(String error) {
        databaseErrorMutableLiveData.setValue(error);
    }
}