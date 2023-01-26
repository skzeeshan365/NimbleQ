package com.reiserx.nimbleq.Activities.Feedbacks;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.reiserx.nimbleq.Adapters.RatingsAdapter;
import com.reiserx.nimbleq.Utils.SharedPreferenceClass;
import com.reiserx.nimbleq.ViewModels.classViewModel;
import com.reiserx.nimbleq.databinding.FragmentClassListBinding;

public class FragmentFeedbacks extends Fragment {

    private FragmentClassListBinding binding;

    RatingsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentClassListBinding.inflate(inflater, container, false);

        binding.recycler.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recycler.setLayoutManager(layoutManager);
        adapter = new RatingsAdapter(getContext());

        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        classViewModel viewModel = new ViewModelProvider(this).get(classViewModel.class);

        SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(requireContext());

        viewModel.getClassRatings(sharedPreferenceClass.getClassID());
        viewModel.getRatingModelListMutableLiveData().observe(getViewLifecycleOwner(), ratingModelList -> {
            adapter.setData(ratingModelList);
            binding.recycler.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            binding.recycler.setVisibility(View.VISIBLE);
            binding.progHolder.setVisibility(View.GONE);
        });
        viewModel.getDatabaseErrorMutableLiveData().observe(getViewLifecycleOwner(), s -> {
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