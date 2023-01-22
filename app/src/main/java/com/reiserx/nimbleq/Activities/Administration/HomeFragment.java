package com.reiserx.nimbleq.Activities.Administration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.reiserx.nimbleq.Activities.Doubts.FirstFragment;
import com.reiserx.nimbleq.Adapters.Administration.HomeAdapter;
import com.reiserx.nimbleq.Adapters.slotsAdapter;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.databinding.FragmentAdminHomeBinding;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private FragmentAdminHomeBinding binding;

    HomeAdapter homeAdapter;
    List<String> data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminHomeBinding.inflate(inflater, container, false);

        data = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recycler.setLayoutManager(layoutManager);
        homeAdapter = new HomeAdapter(getContext(), data, NavHostFragment.findNavController(HomeFragment.this));
        binding.recycler.setAdapter(homeAdapter);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        data.add("User list");
        data.add("Student list");
        data.add("Teacher list");
        data.add("Grade list");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}