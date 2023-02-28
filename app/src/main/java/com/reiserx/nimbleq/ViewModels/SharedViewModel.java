package com.reiserx.nimbleq.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<String>> selected = new MutableLiveData<>();

    public void select(List<String> item) {
        selected.setValue(item);
    }

    public LiveData<List<String>> getSelected() {
        return selected;
    }
}