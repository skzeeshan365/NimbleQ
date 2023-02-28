package com.reiserx.nimbleq.Activities.Administration;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reiserx.nimbleq.Adapters.classListAdapter;
import com.reiserx.nimbleq.Adapters.requestClassAdapter;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.ClassRequestModel;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.SharedPreferenceClass;
import com.reiserx.nimbleq.ViewModels.SharedViewModel;
import com.reiserx.nimbleq.ViewModels.classViewModel;
import com.reiserx.nimbleq.databinding.FragmentClassListBinding;

import java.util.List;

public class RequestedClassList extends Fragment {

    private FragmentClassListBinding binding;

    requestClassAdapter adapter;

    SharedPreferenceClass sharedPreferenceClass;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentClassListBinding.inflate(inflater, container, false);

        sharedPreferenceClass = new SharedPreferenceClass(requireContext());

        binding.recycler.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recycler.setLayoutManager(layoutManager);
        adapter = new requestClassAdapter(getContext(), binding.getRoot(), sharedPreferenceClass.getUserID());
        binding.recycler.setAdapter(adapter);

        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        classViewModel viewModel = new ViewModelProvider(this).get(classViewModel.class);

        viewModel.getClassRequestsForStudents(sharedPreferenceClass.getUserID());
        viewModel.getClassRequestMutableLiveData().observe(getViewLifecycleOwner(), classRequestModels -> {
                Log.d(CONSTANTS.TAG2, String.valueOf(classRequestModels.size()));
                adapter.setData(classRequestModels);
                adapter.notifyDataSetChanged();
                binding.recycler.setVisibility(View.VISIBLE);
                binding.progHolder.setVisibility(View.GONE);
        });
        viewModel.getClassListErrorMutableLiveData().observe(getViewLifecycleOwner(), s -> {
                binding.textView9.setText(s);
                binding.recycler.setVisibility(View.GONE);
                binding.progHolder.setVisibility(View.VISIBLE);
                binding.progressBar2.setVisibility(View.GONE);
                binding.textView9.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}