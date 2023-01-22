package com.reiserx.nimbleq.Activities.Administration;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.reiserx.nimbleq.Adapters.Administration.HomeAdapter;
import com.reiserx.nimbleq.Adapters.Administration.UserListAdapter;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.ViewModels.AdministrationViewModel;
import com.reiserx.nimbleq.databinding.FragmentUserlistAdminBinding;

import java.util.ArrayList;
import java.util.List;

public class FragmentUserList extends Fragment {

    private FragmentUserlistAdminBinding binding;

    UserListAdapter userListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserlistAdminBinding.inflate(inflater, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recycler.setLayoutManager(layoutManager);
        userListAdapter = new UserListAdapter(getContext(), NavHostFragment.findNavController(FragmentUserList.this));
        userListAdapter.setActionCode(R.id.action_FragmentUserList_to_FragmentUserDetails);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AdministrationViewModel administrationViewModel = new ViewModelProvider(this).get(AdministrationViewModel.class);

        administrationViewModel.getAllUserList();
        administrationViewModel.getGetUserListMutableLiveData().observe(getViewLifecycleOwner(), userDataList -> {
            userListAdapter.setData(userDataList);
            binding.recycler.setAdapter(userListAdapter);
            userListAdapter.notifyDataSetChanged();
        });
        administrationViewModel.getUserListErrorMutableLiveData().observe(getViewLifecycleOwner(), s -> Log.d(CONSTANTS.TAG2, s));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}