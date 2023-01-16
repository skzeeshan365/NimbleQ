package com.reiserx.nimbleq.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.reiserx.nimbleq.Repository.AdministrationRepository;

import java.util.List;

public class AdministrationViewModel extends ViewModel implements AdministrationRepository.OnGetMimetypesCompleted {

    private final MutableLiveData<List<String>> mimeTypesListMutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> databaseErrorMutableLiveData = new MutableLiveData<>();

    private final AdministrationRepository firebaseRepo;

    public MutableLiveData<List<String>> getMimeTypesListMutableLiveData() {
        return mimeTypesListMutableLiveData;
    }

    public MutableLiveData<String> getDatabaseErrorMutableLiveData() {
        return databaseErrorMutableLiveData;
    }

    public AdministrationViewModel() {
        firebaseRepo = new AdministrationRepository(this);
    }

    public void getMimeTypes() {
        firebaseRepo.getMimeTypes();
    }

    @Override
    public void onSuccess(List<String> mimetypes) {
        mimeTypesListMutableLiveData.setValue(mimetypes);
    }

    @Override
    public void onFailure(String error) {
        databaseErrorMutableLiveData.setValue(error);
    }
}