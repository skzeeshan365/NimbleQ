package com.reiserx.nimbleq.Activities.Fragments.ClassView;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;

import com.reiserx.nimbleq.Adapters.LearnerListAdapter;
import com.reiserx.nimbleq.Adapters.LecturesAdapter;
import com.reiserx.nimbleq.Adapters.slotsAdapter;
import com.reiserx.nimbleq.Models.LecturesModel;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.SharedPreferenceClass;
import com.reiserx.nimbleq.Utils.SnackbarTop;
import com.reiserx.nimbleq.Utils.StateCityData;
import com.reiserx.nimbleq.Utils.dialogs;
import com.reiserx.nimbleq.ViewModels.AdministrationViewModel;
import com.reiserx.nimbleq.ViewModels.classViewModel;
import com.reiserx.nimbleq.databinding.FragmentLecturesBinding;
import com.reiserx.nimbleq.databinding.FragmentUserlistAdminBinding;

import java.util.ArrayList;
import java.util.List;

public class LecturesFragment extends Fragment {

    private FragmentLecturesBinding binding;

    List<LecturesModel> data;
    LecturesAdapter adapter;

    SnackbarTop snackbarTop;
    SharedPreferenceClass sharedPreferenceClass;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLecturesBinding.inflate(inflater, container, false);

        snackbarTop = new SnackbarTop(binding.getRoot());

        sharedPreferenceClass = new SharedPreferenceClass(requireContext());

        data = new ArrayList<>();
        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LecturesAdapter(getContext(), snackbarTop, sharedPreferenceClass.getClassID());
        binding.recycler.setAdapter(adapter);

        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        classViewModel classViewModel = new ViewModelProvider(this).get(com.reiserx.nimbleq.ViewModels.classViewModel.class);

        classViewModel.getClassLectures(sharedPreferenceClass.getClassID());
        classViewModel.getLecturesModelListMutableLiveData().observe(getViewLifecycleOwner(), lecturesModelList -> {
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