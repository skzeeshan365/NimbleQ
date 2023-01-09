package com.reiserx.nimbleq.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.Repository.UserDataRepository;

public class UserDataViewModel extends ViewModel implements UserDataRepository.OnRealtimeDbTaskComplete, UserDataRepository.getUsernameComplete {

    private final MutableLiveData<UserData> userData = new MutableLiveData<>();
    private final MutableLiveData<String> username = new MutableLiveData<>();
    private final MutableLiveData<String> databaseErrorMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> userNameError = new MutableLiveData<>();
    private final UserDataRepository firebaseRepo;

    public MutableLiveData<UserData> getUserData() {
        return userData;
    }

    public MutableLiveData<String> getUserName() {
        return username;
    }

    public MutableLiveData<String> getDatabaseErrorMutableLiveData() {
        return databaseErrorMutableLiveData;
    }

    public UserDataViewModel() {
        firebaseRepo = new UserDataRepository(this, this);
    }

    public void getUserData(String userID) {
        firebaseRepo.getUserData(userID);
    }

    public void getUsername(String userID) {
        firebaseRepo.getUsername(userID);
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
    public void onFailed(String error) {
        userNameError.setValue(error);
    }

    @Override
    public void onFailure(String error) {
        databaseErrorMutableLiveData.setValue(error);
    }
}