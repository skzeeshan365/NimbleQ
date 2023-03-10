package com.reiserx.nimbleq.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.reiserx.nimbleq.Models.AdminListModel;
import com.reiserx.nimbleq.Models.FCMCREDENTIALS;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.Models.mimeTypesModel;
import com.reiserx.nimbleq.Models.userDetails;
import com.reiserx.nimbleq.Models.zoomCredentials;
import com.reiserx.nimbleq.Repository.AdministrationRepository;

import java.util.List;

public class AdministrationViewModel extends ViewModel implements
        AdministrationRepository.OnGetMimetypesCompleted,
        AdministrationRepository.OnGetFileEnabledComplete,
        AdministrationRepository.OnGetUserListComplete,
        AdministrationRepository.OnGetUserDetailsComplete,
        AdministrationRepository.OnGetClassJoinCountComplete,
        AdministrationRepository.OnGetClassCreateCountComplete,
        AdministrationRepository.OnGetListStringDataCountComplete,
        AdministrationRepository.OnGetAdminModelListComplete,
        AdministrationRepository.OnUpdateModelListComplete,
        AdministrationRepository.OnGetZoomCredentialsComplete,
        AdministrationRepository.OnGetFCMCredentialsComplete,
        AdministrationRepository.OnGetAdministratorComplete,
        AdministrationRepository.OnGetSlotLimitComplete,
        AdministrationRepository.OnGetFileSizeLimitComplete,
        AdministrationRepository.OnGetLinkPrivacyPolicyComplete,
        AdministrationRepository.OnGetLinkTermsOfServiceComplete,
        AdministrationRepository.OnGetLecturesLimitComplete {

    private final MutableLiveData<List<String>> mimeTypesListMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> fileEnabledMutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<List<UserData>> getUserListMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<userDetails> userDetailsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> classJoinCountMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> classCreateCountMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> listStringMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<AdminListModel>> adminModelListMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<AdminListModel> adminListModelMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<zoomCredentials> zoomCredentialsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<FCMCREDENTIALS> FCMCredentialsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> adminMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> slotLimitMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> fileSizeLimitMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> lecturesLimitMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> linkPrivacyPolicyMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> linkTermsOfServiceMutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> databaseErrorMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> userListErrorMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> adminErrorMutableLiveData = new MutableLiveData<>();

    private final AdministrationRepository firebaseRepo;

    public MutableLiveData<List<String>> getMimeTypesListMutableLiveData() {
        return mimeTypesListMutableLiveData;
    }

    public MutableLiveData<Long> getSlotLimitMutableLiveData() {
        return slotLimitMutableLiveData;
    }

    public MutableLiveData<Long> getFileSizeLimitMutableLiveData() {
        return fileSizeLimitMutableLiveData;
    }

    public MutableLiveData<Boolean> getAdminMutableLiveData() {
        return adminMutableLiveData;
    }

    public MutableLiveData<Boolean> getFileEnabledMutableLiveData() {
        return fileEnabledMutableLiveData;
    }

    public MutableLiveData<List<UserData>> getGetUserListMutableLiveData() {
        return getUserListMutableLiveData;
    }

    public MutableLiveData<userDetails> getUserDetailsMutableLiveData() {
        return userDetailsMutableLiveData;
    }

    public MutableLiveData<Long> getClassJoinCountMutableLiveData() {
        return classJoinCountMutableLiveData;
    }

    public MutableLiveData<Long> getClassCreateCountMutableLiveData() {
        return classCreateCountMutableLiveData;
    }

    public MutableLiveData<zoomCredentials> getZoomCredentialsMutableLiveData() {
        return zoomCredentialsMutableLiveData;
    }

    public MutableLiveData<List<String>> getListStringMutableLiveData() {
        return listStringMutableLiveData;
    }

    public MutableLiveData<List<AdminListModel>> getAdminModelListMutableLiveData() {
        return adminModelListMutableLiveData;
    }

    public MutableLiveData<FCMCREDENTIALS> getFCMCredentialsMutableLiveData() {
        return FCMCredentialsMutableLiveData;
    }

    public MutableLiveData<String> getLinkPrivacyPolicyMutableLiveData() {
        return linkPrivacyPolicyMutableLiveData;
    }

    public MutableLiveData<String> getLinkTermsOfServiceMutableLiveData() {
        return linkTermsOfServiceMutableLiveData;
    }

    public MutableLiveData<Long> getLecturesLimitMutableLiveData() {
        return lecturesLimitMutableLiveData;
    }

    public MutableLiveData<String> getDatabaseErrorMutableLiveData() {
        return databaseErrorMutableLiveData;
    }

    public MutableLiveData<String> getUserListErrorMutableLiveData() {
        return userListErrorMutableLiveData;
    }

    public MutableLiveData<String> getAdminErrorMutableLiveData() {
        return adminErrorMutableLiveData;
    }

    public AdministrationViewModel() {
        firebaseRepo = new AdministrationRepository(this,this, this, this, this, this, this, this, this, this, this, this, this, this, this, this, this);
    }

    public void getMimeTypesForGroupChats() {
        firebaseRepo.getMimeTypesForGroupChats();
    }

    public void getFileEnabled() {
        firebaseRepo.getFilesEnabled();
    }

    public void getAllUserList() {
        firebaseRepo.getAllUserList();
    }

    public void getTeacherList() {
        firebaseRepo.getTeacherList();
    }

    public void getStudentList() {
        firebaseRepo.getStudentList();
    }

    public void getLearnerListForClass(String classID) {
        firebaseRepo.getLearnerListForClass(classID);
    }

    public void getUserDetails(String userID) {
        firebaseRepo.getUserDetails(userID);
    }

    public void getClassJoinCount(String userID) {
        firebaseRepo.getClassJoinCount(userID);
    }

    public void getCreatedClassCount(String userID) {
        firebaseRepo.getCreatedClassCount(userID);
    }

    public void getGradeList() {
        firebaseRepo.getGradeList();
    }

    public void getGradeModelList() {
        firebaseRepo.getGradeModelList();
    }

    public void updateGradeModelList(String grade) {
        firebaseRepo.updateGradeModelList(grade);
    }

    public void getSubjectModelList() {
        firebaseRepo.getSubjectModelList();
    }

    public void updateSubjectModelList(String grade) {
        firebaseRepo.updateSubjectModelList(grade);
    }

    public void getSlotModelList() {
        firebaseRepo.getSlotModelList();
    }

    public void getFileList() {
        firebaseRepo.getFileList();
    }

    public void updateFIleModelList(mimeTypesModel name) {
        firebaseRepo.updateFileModelList(name);
    }

    public void updateSlotModelList(String grade) {
        firebaseRepo.updateSlotModelList(grade);
    }

    public void getZoomCredentials() {
        firebaseRepo.getZoomCredentials();
    }

    public void getFCMCredentials() {
        firebaseRepo.getFCMCredentials();
    }

    public void getAdministrator(String userID) {
        firebaseRepo.getAdministrator(userID);
    }

    public void updateFilesEnabled(boolean value) {
        firebaseRepo.updateFilesEnabled(value);
    }

    public void getSlotLimit() {
        firebaseRepo.getSlotLimit();
    }

    public void getFileSizeLimit() {
        firebaseRepo.getFileSizeLimit();
    }

    public void updateFileSizeLimit(Long value) {
        firebaseRepo.updateFileSizeLimit(value);
    }

    public void updateSlotLimit(Long value) {
        firebaseRepo.updateSlotLimit(value);
    }

    public void getLinkPrivacyPolicy() {
        firebaseRepo.getLinkPrivacyPolicy();
    }

    public void updateLinkPrivacyPolicy(String link) {
        firebaseRepo.updateLinkPrivacyPolicy(link);
    }

    public void getLinkTermsOfService() {
        firebaseRepo.getLinkTermsOfService();
    }

    public void updateLinkTermsOfService(String link) {
        firebaseRepo.updateLinkTermsOfService(link);
    }

    public void getLecturesLimit() {
        firebaseRepo.getLecturesLimit();
    }

    public void updateLecturesLimit(Long value) {
        firebaseRepo.updateLecturesLimit(value);
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

    @Override
    public void onGetUserListSuccess(List<UserData> userDataList) {
        getUserListMutableLiveData.setValue(userDataList);
    }

    @Override
    public void onGetUserListFailure(String error) {
        userListErrorMutableLiveData.setValue(error);
    }

    @Override
    public void onGetUserDetailsSuccess(userDetails userDetail) {
        userDetailsMutableLiveData.setValue(userDetail);
    }

    @Override
    public void onGetClassJoinCountSuccess(long count) {
        classJoinCountMutableLiveData.setValue(count);
    }

    @Override
    public void onGetClassCreateCountSuccess(long count) {
        classCreateCountMutableLiveData.setValue(count);
    }

    @Override
    public void onGetListStringDataSuccess(List<String> data) {
        listStringMutableLiveData.setValue(data);
    }

    @Override
    public void onGetAdminModelListSuccess(List<AdminListModel> data) {
        adminModelListMutableLiveData.setValue(data);
    }

    @Override
    public void onUpdateModelListSuccess(AdminListModel adminListModel) {
        adminListModelMutableLiveData.setValue(adminListModel);
    }

    @Override
    public void onGetZoomCredentialsSuccess(zoomCredentials zoomCredentials) {
        zoomCredentialsMutableLiveData.setValue(zoomCredentials);
    }

    @Override
    public void onGetFCMCredentialsSuccess(FCMCREDENTIALS fcmcredentials) {
        FCMCredentialsMutableLiveData.setValue(fcmcredentials);
    }

    @Override
    public void onGetAdminSuccess(Boolean admin) {
        adminMutableLiveData.setValue(admin);
    }

    @Override
    public void onGetSlotLimitSuccess(Long limit) {
        slotLimitMutableLiveData.setValue(limit);
    }

    @Override
    public void onGetFileSizeLimitSuccess(Long limit) {
        fileSizeLimitMutableLiveData.setValue(limit);
    }

    @Override
    public void onGetLinkPrivacyPolicySuccess(String value) {
        linkPrivacyPolicyMutableLiveData.setValue(value);
    }

    @Override
    public void onGetLinkTermsOfServiceSuccess(String Value) {
        linkTermsOfServiceMutableLiveData.setValue(Value);
    }

    @Override
    public void onGetLecturesLimitSuccess(Long limit) {
        lecturesLimitMutableLiveData.setValue(limit);
    }

    @Override
    public void onAdminFailed(String error) {
        adminErrorMutableLiveData.setValue(error);
    }

    @Override
    public void onFailed(String error) {
        databaseErrorMutableLiveData.setValue(error);
    }
}