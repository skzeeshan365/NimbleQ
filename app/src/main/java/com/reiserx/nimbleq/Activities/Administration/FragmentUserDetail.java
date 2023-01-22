package com.reiserx.nimbleq.Activities.Administration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reiserx.nimbleq.Adapters.Administration.HomeAdapter;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.SharedPreferenceClass;
import com.reiserx.nimbleq.ViewModels.AdministrationViewModel;
import com.reiserx.nimbleq.databinding.FragmentAdminHomeBinding;
import com.reiserx.nimbleq.databinding.FragmentUserDetailBinding;

import java.util.ArrayList;
import java.util.List;

public class FragmentUserDetail extends Fragment {

    private FragmentUserDetailBinding binding;

    UserData userData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserDetailBinding.inflate(inflater, container, false);

        SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(requireContext());
        userData = sharedPreferenceClass.getUserInfo();

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AdministrationViewModel viewModel = new ViewModelProvider(this).get(AdministrationViewModel.class);

        viewModel.getUserDetails(userData.getUid());
        viewModel.getUserDetailsMutableLiveData().observe(getViewLifecycleOwner(), userDetails -> {

            binding.userNameTxt.setText("Username: "+userData.getUserName());
            binding.useridTxt.setText("UserID: "+userData.getUid());
            binding.userPhoneTxt.setText("Phone: "+userData.getPhoneNumber());

            binding.userSchoolTxt.setText("School: "+userDetails.getSchoolName());
            binding.userGradeTxt.setText("Grade: "+userDetails.getGrade());
            binding.userGenderTxt.setText("Gender: "+userDetails.getGender());
            binding.userStateCityTxt.setText("State/city: "+userDetails.getState()+", "+userDetails.getCity());
        });

        viewModel.getClassJoinCount(userData.getUid());
        viewModel.getClassJoinCountMutableLiveData().observe(getViewLifecycleOwner(), count -> binding.userClassesTxt.setText("Classes joined: "+count));

        viewModel.getCreatedClassCount(userData.getUid());
        viewModel.getClassCreateCountMutableLiveData().observe(getViewLifecycleOwner(), count -> binding.userClassesCreatedTxt.setText("Classes created: "+count));

        binding.joinedClassBtn.setOnClickListener(view1 -> NavHostFragment.findNavController(FragmentUserDetail.this).navigate(R.id.action_FragmentUserDetails_to_FragmentJoinedClassList));
        binding.createdClassBtn.setOnClickListener(view1 -> NavHostFragment.findNavController(FragmentUserDetail.this).navigate(R.id.action_FragmentUserDetails_to_FragmentCreateClassList));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}