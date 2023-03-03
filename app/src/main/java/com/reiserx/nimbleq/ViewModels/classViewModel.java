package com.reiserx.nimbleq.ViewModels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.reiserx.nimbleq.Models.ClassRequestModel;
import com.reiserx.nimbleq.Models.LecturesModel;
import com.reiserx.nimbleq.Models.RatingModel;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.Models.classModel;
import com.reiserx.nimbleq.Models.subjectAndTimeSlot;
import com.reiserx.nimbleq.Repository.ClassRepository;

import java.util.List;

public class classViewModel extends ViewModel implements ClassRepository.OnRealtimeDbTaskComplete,
        ClassRepository.OnClassJoinStateChanged,
        ClassRepository.OnGetClassListComplete,
        ClassRepository.onGetClassRequestComplete,
        ClassRepository.OnRatingSubmitted,
        ClassRepository.OnCreateClassComplete,
        ClassRepository.OnGetRatingsComplete,
        ClassRepository.OnGetLecturesComplete,
        ClassRepository.OnGetLecturesCountComplete,
        ClassRepository.OnGetClassRequestsCount,
        ClassRepository.OnGetClassAcceptCountComplete {

    private final MutableLiveData<classModel> classData = new MutableLiveData<>();
    private final MutableLiveData<Integer> classState = new MutableLiveData<>();
    private final MutableLiveData<List<classModel>> classList = new MutableLiveData<>();
    private final MutableLiveData<List<ClassRequestModel>> classRequestMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Void> ratingSubmittedMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> createClassMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<RatingModel>> ratingModelListMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<LecturesModel>> lecturesModelListMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> lecturesCountModelListMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> requestsCountModelListMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> acceptedCountModelListMutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> databaseErrorMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> classListErrorMutableLiveData = new MutableLiveData<>();

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

    public MutableLiveData<Void> getRatingSubmittedMutableLiveData() {
        return ratingSubmittedMutableLiveData;
    }

    public MutableLiveData<String> getDatabaseErrorMutableLiveData() {
        return databaseErrorMutableLiveData;
    }

    public MutableLiveData<String> getClassListErrorMutableLiveData() {
        return classListErrorMutableLiveData;
    }

    public MutableLiveData<String> getCreateClassMutableLiveData() {
        return createClassMutableLiveData;
    }

    public MutableLiveData<List<RatingModel>> getRatingModelListMutableLiveData() {
        return ratingModelListMutableLiveData;
    }

    public MutableLiveData<List<LecturesModel>> getLecturesModelListMutableLiveData() {
        return lecturesModelListMutableLiveData;
    }

    public MutableLiveData<Long> getLecturesCountModelListMutableLiveData() {
        return lecturesCountModelListMutableLiveData;
    }

    public MutableLiveData<Long> getRequestsCountModelListMutableLiveData() {
        return requestsCountModelListMutableLiveData;
    }

    public MutableLiveData<Long> getAcceptedCountModelListMutableLiveData() {
        return acceptedCountModelListMutableLiveData;
    }

    public classViewModel() {
        firebaseRepo = new ClassRepository(this,this,this,this, this, this, this, this, this, this, this);
    }

    public void getClassData(String classID) {
        firebaseRepo.getClassData(classID);
    }

    public void setClassState(String userID, String classID, String token, boolean join, Context context, Long lectures) {
        firebaseRepo.setClassJoinState(userID, classID, token, join, context, lectures);
    }

    public void getClassState(String userID, String classID) {
        firebaseRepo.getClassJoinState(userID, classID);
    }

    public void getClassList(subjectAndTimeSlot subjectAndTimeSlot, String userID) {
        firebaseRepo.getClassList(subjectAndTimeSlot, userID);
    }

    public void getClassListForTeacher(String userID) {
        firebaseRepo.getClassListForTeacher(userID);
    }

    public void getClassRequestsForTeachers(subjectAndTimeSlot subjectAndTimeSlot, String userID) {
        firebaseRepo.getClassRequestsForTeachers(subjectAndTimeSlot, userID);
    }

    public void getClassRequestsForStudents(subjectAndTimeSlot subjectAndTimeSlot, String userID) {
        firebaseRepo.getClassRequestsForStudents(subjectAndTimeSlot, userID);
    }

    public void getClassRequestsByDemand() {
        firebaseRepo.getClassListByDemand();
    }

    public void getClassRequestsByRating() {
        firebaseRepo.getClassListByRating();
    }

    public void setClassRating(String classID, String className, UserData userID, RatingModel ratingModel, String token, Context context) {
        firebaseRepo.setClassRating(classID, className, userID, ratingModel, token, context);
    }

    public void setTeacherRating(String teacherID, UserData userID, RatingModel ratingModel, String token, Context context) {
        firebaseRepo.setTeacherRating(teacherID, userID, ratingModel, token, context);
    }

    public void createClass(Context context, classModel classModel, String teacherName, int lectures, ClassRequestModel request, String acceptorUID) {
        firebaseRepo.createClass(context, classModel, teacherName, lectures, request, acceptorUID);
    }

    public void createClass(Context context, classModel classModel, String teacherName, int lectures) {
        firebaseRepo.createClass(context, classModel, teacherName, lectures);
    }

    public void getAllJoinedClasses(String userID) {
        firebaseRepo.getAllJoinedClasses(userID);
    }

    public void getClassRatings(String classID) {
        firebaseRepo.getClassRating(classID);
    }

    public void getTeacherRatings(String userID) {
        firebaseRepo.getTeacherRating(userID);
    }

    public void getClassLectures(String classID) {
        firebaseRepo.getClassLectures(classID);
    }

    public void getLecturesCount(String classID) {
        firebaseRepo.getLecturesCount(classID);
    }

    public void getClassListFromIDs(List<String> list) {
        firebaseRepo.getClassListFromIDs(list);
    }

    public void getClassRequestsForStudents(String userID) {
        firebaseRepo.getClassRequestsForStudents(userID);
    }

    public void getClassRequestsCountForStudents(String userID) {
        firebaseRepo.getClassRequestsCountForStudents(userID);
    }

    public void getClassAcceptedForUsers(String userID) {
        firebaseRepo.getClassAcceptedForUsers(userID);
    }

    public void getClassAcceptedCountForUsers(String userID) {
        firebaseRepo.getClassAcceptedCountForUsers(userID);
    }

    @Override
    public void onSuccess(classModel classModel) {
        classData.setValue(classModel);
    }

    @Override
    public void onSuccess(Void voids) {
        ratingSubmittedMutableLiveData.setValue(voids);
    }

    @Override
    public void onClassCreated(String classID) {
        createClassMutableLiveData.setValue(classID);
    }

    @Override
    public void onGetRatingsSuccess(List<RatingModel> ratingModelList) {
        ratingModelListMutableLiveData.setValue(ratingModelList);
    }

    @Override
    public void onGetLecturesSuccess(List<LecturesModel> lecturesModelList) {
        lecturesModelListMutableLiveData.setValue(lecturesModelList);
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
        classListErrorMutableLiveData.setValue(error);
    }

    @Override
    public void onGetLecturesCountSuccess(Long count) {
        lecturesCountModelListMutableLiveData.setValue(count);
    }

    @Override
    public void onGetRequestsCountSuccess(Long count) {
        requestsCountModelListMutableLiveData.setValue(count);
    }

    @Override
    public void onGetAcceptCountSuccess(Long count) {
        acceptedCountModelListMutableLiveData.setValue(count);
    }

    @Override
    public void onFailed(String error) {
        databaseErrorMutableLiveData.setValue(error);
    }
}