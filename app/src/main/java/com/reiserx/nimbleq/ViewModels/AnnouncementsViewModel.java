package com.reiserx.nimbleq.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.reiserx.nimbleq.Models.Announcements.announcementsModel;
import com.reiserx.nimbleq.Repository.AnnouncementsRepository;

import java.util.List;

public class AnnouncementsViewModel extends ViewModel implements AnnouncementsRepository.OnRealtimeDbTaskComplete {

    private final MutableLiveData<List<announcementsModel>> parentItemMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> databaseErrorMutableLiveData = new MutableLiveData<>();
    private final AnnouncementsRepository firebaseRepo;

    public MutableLiveData<List<announcementsModel>> getParentItemMutableLiveData() {
        return parentItemMutableLiveData;
    }

    public MutableLiveData<String> getDatabaseErrorMutableLiveData() {
        return databaseErrorMutableLiveData;
    }

    public AnnouncementsViewModel() {
        firebaseRepo = new AnnouncementsRepository(this);
    }

    public void getAllData(String classID) {
        firebaseRepo.getAllData(classID);
    }

    @Override
    public void onSuccess(List<announcementsModel> parentItemList) {
        parentItemMutableLiveData.setValue(parentItemList);
    }

    @Override
    public void onFailure(String error) {
        databaseErrorMutableLiveData.setValue(error);
    }
}