package com.reiserx.nimbleq.Activities.Administration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reiserx.nimbleq.Adapters.Administration.UserListAdapter;
import com.reiserx.nimbleq.Adapters.classListAdapter;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.SharedPreferenceClass;
import com.reiserx.nimbleq.ViewModels.AdministrationViewModel;
import com.reiserx.nimbleq.ViewModels.classViewModel;
import com.reiserx.nimbleq.databinding.FragmentUserlistAdminBinding;

public class FragmentjoinedClassList extends Fragment {

    private FragmentUserlistAdminBinding binding;

    classListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserlistAdminBinding.inflate(inflater, container, false);

        binding.recycler.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recycler.setLayoutManager(layoutManager);
        adapter = new classListAdapter(getContext());

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        classViewModel viewModel = new ViewModelProvider(this).get(classViewModel.class);

        SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(requireContext());
        UserData userData = sharedPreferenceClass.getUserInfo();
        viewModel.getAllJoinedClasses(userData.getUid());
        viewModel.getClassList().observe(getViewLifecycleOwner(), userDataList -> {
            Log.d(CONSTANTS.TAG2, String.valueOf(userDataList.size()));
            adapter.setClassList(userDataList);
            binding.recycler.setAdapter(adapter);
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