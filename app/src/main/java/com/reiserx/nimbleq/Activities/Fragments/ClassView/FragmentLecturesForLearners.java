package com.reiserx.nimbleq.Activities.Fragments.ClassView;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reiserx.nimbleq.Adapters.LearnerLectureAdapter;
import com.reiserx.nimbleq.Adapters.LecturesAdapter;
import com.reiserx.nimbleq.Models.LecturesModel;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.SharedPreferenceClass;
import com.reiserx.nimbleq.Utils.SnackbarTop;
import com.reiserx.nimbleq.ViewModels.UserDataViewModel;
import com.reiserx.nimbleq.ViewModels.classViewModel;
import com.reiserx.nimbleq.databinding.FragmentLecturesBinding;

import java.util.ArrayList;
import java.util.List;

public class FragmentLecturesForLearners extends Fragment {

    private FragmentLecturesBinding binding;

    List<LecturesModel> data;
    LearnerLectureAdapter adapter;

    SnackbarTop snackbarTop;
    SharedPreferenceClass sharedPreferenceClass;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLecturesBinding.inflate(inflater, container, false);

        snackbarTop = new SnackbarTop(binding.getRoot());

        sharedPreferenceClass = new SharedPreferenceClass(requireContext());

        data = new ArrayList<>();
        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LearnerLectureAdapter(getContext(), snackbarTop, sharedPreferenceClass.getClassID());
        binding.recycler.setAdapter(adapter);

        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UserDataViewModel userDataViewModel = new ViewModelProvider(this).get(UserDataViewModel.class);

        userDataViewModel.getClassLectures(sharedPreferenceClass.getClassID(), sharedPreferenceClass.getUserID());
        userDataViewModel.getLecturesMutableLiveData().observe(getViewLifecycleOwner(), lecturesModelList -> {
            adapter.setData(lecturesModelList);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}