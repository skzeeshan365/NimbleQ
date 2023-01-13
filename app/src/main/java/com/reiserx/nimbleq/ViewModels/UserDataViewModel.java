package com.reiserx.nimbleq.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.Models.userDetails;
import com.reiserx.nimbleq.Models.userType;
import com.reiserx.nimbleq.Repository.UserDataRepository;

import java.util.List;

public class UserDataViewModel extends ViewModel implements UserDataRepository.OnRealtimeDbTaskComplete,
        UserDataRepository.getUsernameComplete,
        UserDataRepository.getUserTypeComplete,
        UserDataRepository.getUserDetailsComplete,
        UserDataRepository.getDataAsListString {

    private final MutableLiveData<UserData> userData = new MutableLiveData<>();
    private final MutableLiveData<String> username = new MutableLiveData<>();
    private final MutableLiveData<userType> userTypeMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<userDetails> userDetailsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> LisStringMutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> databaseErrorMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> userNameError = new MutableLiveData<>();
    private final UserDataRepository firebaseRepo;

    public MutableLiveData<UserData> getUserData() {
        return userData;
    }

    public MutableLiveData<String> getUserName() {
        return username;
    }

    public MutableLiveData<userType> getUserTypeMutableLiveData() {
        return userTypeMutableLiveData;
    }

    public MutableLiveData<String> getDatabaseErrorMutableLiveData() {
        return databaseErrorMutableLiveData;
    }

    public MutableLiveData<userDetails> getUserDetailsMutableLiveData() {
        return userDetailsMutableLiveData;
    }

    public MutableLiveData<List<String>> getLisStringMutableLiveData() {
        return LisStringMutableLiveData;
    }

    public UserDataViewModel() {
        firebaseRepo = new UserDataRepository(this, this, this, this, this);
    }

    public void getUserData(String userID) {
        firebaseRepo.getUserData(userID);
    }

    public void getUsername(String userID) {
        firebaseRepo.getUsername(userID);
    }

    public void getUserType(String userID) {
        firebaseRepo.getUserType(userID);
    }

    public void getUserDetails(String userID) {
        firebaseRepo.getUserDetails(userID);
    }

    public void getTeacherList() {
        firebaseRepo.getTeacherList();
    }

    public void getAllJoinedClasses(String userID) {
        firebaseRepo.getAllJoinedClasses(userID);
    }

    @Override
    public void onSuccess(UserData userDatas) {
        userData.setValue(userDatas);
    }

    @Override
    public void onSuccess(String usernames) {
        username.setValue(usernames);
    }

    @Override
    public void onSuccess(userType userType) {
        userTypeMutableLiveData.setValue(userType);
    }

    @Override
    public void onSuccess(userDetails userDetailsList) {
        userDetailsMutableLiveData.setValue(userDetailsList);
    }

    @Override
    public void onSuccess(List<String> teacherList) {
        LisStringMutableLiveData.setValue(teacherList);
    }

    @Override
    public void onFailed(String error) {
        userNameError.setValue(error);
    }

    @Override
    public void onFailure(String error) {
        databaseErrorMutableLiveData.setValue(error);
    }
}