package com.reiserx.nimbleq.ViewModels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.reiserx.nimbleq.Models.subjectAndTimeSlot;
import com.reiserx.nimbleq.Repository.SlotListRepository;

import java.util.ArrayList;

public class SlotListViewModel extends ViewModel implements SlotListRepository.OnRealtimeDbTaskComplete {

    private final MutableLiveData<ArrayList<subjectAndTimeSlot>> slotList = new MutableLiveData<>();
    private final MutableLiveData<String> databaseErrorMutableLiveData = new MutableLiveData<>();
    private final SlotListRepository firebaseRepo;

    public MutableLiveData<ArrayList<subjectAndTimeSlot>> getSlotListMutableData() {
        return slotList;
    }

    public MutableLiveData<String> getDatabaseErrorMutableLiveData() {
        return databaseErrorMutableLiveData;
    }

    public SlotListViewModel() {
        firebaseRepo = new SlotListRepository(this);
    }

    public void getSlotList(Context context) {
        firebaseRepo.getSlotList(context);
    }

    @Override
    public void onSuccess(ArrayList<subjectAndTimeSlot> slotLists) {
        slotList.setValue(slotLists);
    }

    @Override
    public void onFailure(String error) {
        databaseErrorMutableLiveData.setValue(error);
    }
}