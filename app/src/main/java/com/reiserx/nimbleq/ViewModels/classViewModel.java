package com.reiserx.nimbleq.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.reiserx.nimbleq.Models.ClassRequestModel;
import com.reiserx.nimbleq.Models.classModel;
import com.reiserx.nimbleq.Models.subjectAndTimeSlot;
import com.reiserx.nimbleq.Repository.ClassRepository;

import java.util.List;

public class classViewModel extends ViewModel implements ClassRepository.OnRealtimeDbTaskComplete,
        ClassRepository.OnClassJoinStateChanged,
        ClassRepository.OnGetClassListComplete,
        ClassRepository.onGetClassRequestComplete {

    private final MutableLiveData<classModel> classData = new MutableLiveData<>();
    private final MutableLiveData<Integer> classState = new MutableLiveData<>();
    private final MutableLiveData<List<classModel>> classList = new MutableLiveData<>();
    private final MutableLiveData<List<ClassRequestModel>> classRequestMutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> databaseErrorMutableLiveData = new MutableLiveData<>();

    private final ClassRepository firebaseRepo;

    public MutableLiveData<classModel> getClassData() {
        return classData;
    }

    public MutableLiveData<Integer> getClassState() {
        return classState;
    }

    public MutableLiveData<List<classModel>> getClassList() {
        return classList;
    }

    public MutableLiveData<List<ClassRequestModel>> getClassRequestMutableLiveData() {
        return classRequestMutableLiveData;
    }


    public MutableLiveData<String> getDatabaseErrorMutableLiveData() {
        return databaseErrorMutableLiveData;
    }

    public classViewModel() {
        firebaseRepo = new ClassRepository(this, this, this, this);
    }

    public void getClassData(String classID) {
        firebaseRepo.getClassData(classID);
    }

    public void setClassState(String userID, String classID, boolean join) {
        firebaseRepo.setClassJoinState(userID, classID, join);
    }

    public void getClassState(String userID, String classID) {
        firebaseRepo.getClassJoinState(userID, classID);
    }

    public void getClassList(subjectAndTimeSlot subjectAndTimeSlot) {
        firebaseRepo.getClassList(subjectAndTimeSlot);
    }

    public void getClassListForTeacher(String userID) {
        firebaseRepo.getClassListForTeacher(userID);
    }

    public void getClassRequests(subjectAndTimeSlot subjectAndTimeSlot) {
        firebaseRepo.getClassRequests(subjectAndTimeSlot);
    }

    @Override
    public void onSuccess(classModel classModel) {
        classData.setValue(classModel);
    }

    @Override
    public void onFailure(String error) {
        databaseErrorMutableLiveData.setValue(error);
    }

    @Override
    public void onSuccess(int state) {
        classState.setValue(state);
    }

    @Override
    public void onGetClassStateFailure(String error) {
        databaseErrorMutableLiveData.setValue(error);
    }

    @Override
    public void onSuccess(List<classModel> classModelList) {
        classList.setValue(classModelList);
    }


    @Override
    public void onGetClassRequestsSuccess(List<ClassRequestModel> classModelList) {
        classRequestMutableLiveData.setValue(classModelList);
    }

    @Override
    public void onGetClassListFailure(String error) {
        databaseErrorMutableLiveData.setValue(error);
    }
}