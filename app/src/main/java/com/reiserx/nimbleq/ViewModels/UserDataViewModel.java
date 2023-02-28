package com.reiserx.nimbleq.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.reiserx.nimbleq.Models.LecturesModel;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.Models.userDetails;
import com.reiserx.nimbleq.Models.userType;
import com.reiserx.nimbleq.Repository.UserDataRepository;

import java.util.List;

public class UserDataViewModel extends ViewModel implements
        UserDataRepository.OnRealtimeDbTaskComplete,
        UserDataRepository.getUsernameComplete,
        UserDataRepository.getUserTypeComplete,
        UserDataRepository.getUserDetailsComplete,
        UserDataRepository.getDataAsListString,
        UserDataRepository.OnUpdateUsernameComplete,
        UserDataRepository.OnGetLecturesComplete,
        UserDataRepository.OnGetCompletedClassesComplete,
        UserDataRepository.OnGetCompletedClasses1Complete{

    private final MutableLiveData<UserData> userData = new MutableLiveData<>();
    private final MutableLiveData<String> username = new MutableLiveData<>();
    private final MutableLiveData<userType> userTypeMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<userDetails> userDetailsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> LisStringMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Void> updateUsernameMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<LecturesModel>> lecturesMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> completedClassesMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> completedClasses1MutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> databaseErrorMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> userNameError = new MutableLiveData<>();
    private final MutableLiveData<String> task1Error = new MutableLiveData<>();
    private final MutableLiveData<String> task2Error = new MutableLiveData<>();
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

    public MutableLiveData<Void> getUpdateUsernameMutableLiveData() {
        return updateUsernameMutableLiveData;
    }

    public MutableLiveData<List<LecturesModel>> getLecturesMutableLiveData() {
        return lecturesMutableLiveData;
    }

    public MutableLiveData<List<String>> getCompletedClassesMutableLiveData() {
        return completedClassesMutableLiveData;
    }

    public MutableLiveData<List<String>> getCompletedClasses1MutableLiveData() {
        return completedClasses1MutableLiveData;
    }

    public MutableLiveData<String> getTask1Error() {
        return task1Error;
    }

    public MutableLiveData<String> getTask2Error() {
        return task2Error;
    }

    public UserDataViewModel() {
        firebaseRepo = new UserDataRepository(this,this,this,this, this, this, this, this, this);
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

    public void updateFCMToken(String userID) {
        firebaseRepo.updateFCMToken(userID);
    }

    public void updateUsername(String userID, String username) {
        firebaseRepo.updateUsername(userID, username);
    }

    public void getClassLectures(String classID, String userID) {
        firebaseRepo.getClassLectures(classID, userID);
    }

    public void getClassCompleteCount(String userID) {
        firebaseRepo.getClassCompleteCount(userID);
    }

    public void getClassComplete1Count(String userID) {
        firebaseRepo.getClassCompleteCount1(userID);
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
    public void onSuccess(Void voids) {
        updateUsernameMutableLiveData.setValue(voids);
    }

    @Override
    public void onFailed(String error) {
        userNameError.setValue(error);
    }

    @Override
    public void onGetLecturesSuccess(List<LecturesModel> lecturesModelList) {
        lecturesMutableLiveData.setValue(lecturesModelList);
    }

    @Override
    public void onGetCompletedClassesSuccess(List<String> classIDList) {
        completedClassesMutableLiveData.setValue(classIDList);
    }

    @Override
    public void onTask1Failure(String error) {
        task1Error.setValue(error);
    }

    @Override
    public void onGetCompletedClasses1Success(List<String> classIDList) {
        completedClasses1MutableLiveData.setValue(classIDList);
    }

    @Override
    public void onTask2Failure(String error) {
        task2Error.setValue(error);
    }

    @Override
    public void onFailure(String error) {
        databaseErrorMutableLiveData.setValue(error);
    }
}