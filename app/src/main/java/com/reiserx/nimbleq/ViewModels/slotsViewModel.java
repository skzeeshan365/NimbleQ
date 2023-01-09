package com.reiserx.nimbleq.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DatabaseError;
import com.reiserx.nimbleq.Models.subjectAndTimeSlot;
import com.reiserx.nimbleq.Repository.SlotsRepository;

public class slotsViewModel extends ViewModel implements SlotsRepository.OnRealtimeDbTaskComplete {

    private final MutableLiveData<subjectAndTimeSlot> subjectAndTimeSlotMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<DatabaseError> databaseErrorMutableLiveData = new MutableLiveData<>();
    private final SlotsRepository firebaseRepo;

    public MutableLiveData<subjectAndTimeSlot> getParentItemMutableLiveData() {
        return subjectAndTimeSlotMutableLiveData;
    }

    public MutableLiveData<DatabaseError> getDatabaseErrorMutableLiveData() {
        return databaseErrorMutableLiveData;
    }

    public slotsViewModel() {
        firebaseRepo = new SlotsRepository(this);
    }

    public void getSubjectForStudents(String uid) {
        firebaseRepo.getSubjectForStudents(uid);
    }

    public void getSubjectForTeachers(String uid) {
        firebaseRepo.getSubjectForTeachers(uid);
    }

    @Override
    public void onSuccess(subjectAndTimeSlot subjectAndTimeSlot) {
        subjectAndTimeSlotMutableLiveData.setValue(subjectAndTimeSlot);
    }

    @Override
    public void onFailure(DatabaseError error) {
        databaseErrorMutableLiveData.setValue(error);
    }
}