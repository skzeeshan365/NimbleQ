package com.reiserx.nimbleq.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.reiserx.nimbleq.Adapters.Doubts.DoubtsAdapter;
import com.reiserx.nimbleq.Models.Announcements.linkModel;
import com.reiserx.nimbleq.Models.Doubts.AnswerModel;
import com.reiserx.nimbleq.Models.Doubts.DoubtsModel;
import com.reiserx.nimbleq.Repository.DoubtsRepository;

import java.util.List;

public class DoubtsViewModel extends ViewModel implements DoubtsRepository.OnDoubtSubmitted,
        DoubtsRepository.OnGetDoubtsComplete,
        DoubtsRepository.OnAnswerSubmitted,
        DoubtsRepository.OnGetAnswersComplete,
        DoubtsRepository.OnPaginationComplete {

    private final MutableLiveData<Void> fileSubmittedModelMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Void> answerSubmittedModelMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<DoubtsModel>> doubtListModelMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<AnswerModel>> answerListModelMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<DoubtsModel>> doubtPageListModelMutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> databaseErrorMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> paginateErrorMutableLiveData = new MutableLiveData<>();

    private final DoubtsRepository doubtsRepository;

    public MutableLiveData<Void> getFileSubmittedModelMutableLiveData() {
        return fileSubmittedModelMutableLiveData;
    }

    public MutableLiveData<Void> getAnswerSubmittedModelMutableLiveData() {
        return answerSubmittedModelMutableLiveData;
    }

    public MutableLiveData<List<DoubtsModel>> getDoubtListModelMutableLiveData() {
        return doubtListModelMutableLiveData;
    }

    public MutableLiveData<List<AnswerModel>> getAnswerListModelMutableLiveData() {
        return answerListModelMutableLiveData;
    }

    public MutableLiveData<List<DoubtsModel>> getDoubtPageListModelMutableLiveData() {
        return doubtPageListModelMutableLiveData;
    }

    public MutableLiveData<String> getDatabaseErrorMutableLiveData() {
        return databaseErrorMutableLiveData;
    }

    public DoubtsViewModel() {
        doubtsRepository = new DoubtsRepository(this, this, this, this, this);
    }

    public void submitDoubt(DoubtsModel doubtsModel, List<linkModel> linkModels) {
        doubtsRepository.submitDoubt(doubtsModel, linkModels);
    }

    public void submitAnswer(AnswerModel answerModel, List<linkModel> linkModels) {
        doubtsRepository.submitAnswer(answerModel, linkModels);
    }

    public void getDoubtsForTeachers(String subeject) {
        doubtsRepository.getDoubtsForTeacher(subeject);
    }

    public void getDoubtsForStudents(String userID) {
        doubtsRepository.getDoubtsForStudent(userID);
    }

    public void paginateDoubts(String subject, DoubtsAdapter adapter) {
        doubtsRepository.paginateMessages(subject, adapter);
    }

    public void getAnswers(String ClassID) {
        doubtsRepository.getAnswers(ClassID);
    }

    @Override
    public void onSuccess(Void voids) {
        fileSubmittedModelMutableLiveData.setValue(voids);
    }

    @Override
    public void onGetDoubtsSuccess(List<DoubtsModel> doubtsModelList) {
        doubtListModelMutableLiveData.setValue(doubtsModelList);
    }

    @Override
    public void onAnswerSubmitted(Void voids) {
        answerSubmittedModelMutableLiveData.setValue(voids);
    }

    @Override
    public void onSuccess(List<AnswerModel> answerModelList) {
        answerListModelMutableLiveData.setValue(answerModelList);
    }

    @Override
    public void onPaginateSuccess(List<DoubtsModel> doubtsModelList) {
        doubtPageListModelMutableLiveData.setValue(doubtsModelList);
    }

    @Override
    public void onPaginateFailure(String error) {
        paginateErrorMutableLiveData.setValue(error);
    }

    @Override
    public void onFailure(String error) {
        databaseErrorMutableLiveData.setValue(error);
    }
}