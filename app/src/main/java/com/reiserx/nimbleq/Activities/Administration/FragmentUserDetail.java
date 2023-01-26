package com.reiserx.nimbleq.Activities.Administration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.SharedPreferenceClass;
import com.reiserx.nimbleq.ViewModels.AdministrationViewModel;
import com.reiserx.nimbleq.databinding.FragmentUserDetailBinding;

public class FragmentUserDetail extends Fragment {

    private FragmentUserDetailBinding binding;

    UserData userData;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

            binding.userNameTxt.setText(getString(R.string.username1).concat(userData.getUserName()));
            binding.useridTxt.setText(getString(R.string.userID1).concat(userData.getUid()));
            binding.userPhoneTxt.setText(getString(R.string.phone1).concat(userData.getPhoneNumber()));
            binding.userCreatedTxt.setText(getString(R.string.created_on_1).concat(TimeAgo.using(userData.getCreated_timestamp())));
            binding.userLastLoginTxt.setText(getString(R.string.last_login_1).concat(TimeAgo.using(userData.getLastLogin_timestamp())));

            binding.userSchoolTxt.setText(getString(R.string.school_2).concat(userDetails.getSchoolName()));
            binding.userGradeTxt.setText(getString(R.string.grade2).concat(userDetails.getGrade()));
            binding.userGenderTxt.setText(getString(R.string.gender_2).concat(userDetails.getGender()));
            binding.userStateCityTxt.setText(getString(R.string.state_city_1).concat(userDetails.getState()+", "+userDetails.getCity()));
        });

        viewModel.getClassJoinCount(userData.getUid());
        viewModel.getClassJoinCountMutableLiveData().observe(getViewLifecycleOwner(), count -> binding.userClassesTxt.setText(getString(R.string.classes_joined_1).concat(String.valueOf(count))));

        viewModel.getCreatedClassCount(userData.getUid());
        viewModel.getClassCreateCountMutableLiveData().observe(getViewLifecycleOwner(), count -> binding.userClassesCreatedTxt.setText(getString(R.string.classes_created_1).concat(String.valueOf(count))));

        binding.joinedClassBtn.setOnClickListener(view1 -> NavHostFragment.findNavController(FragmentUserDetail.this).navigate(R.id.action_FragmentUserDetails_to_FragmentJoinedClassList));
        binding.createdClassBtn.setOnClickListener(view1 -> NavHostFragment.findNavController(FragmentUserDetail.this).navigate(R.id.action_FragmentUserDetails_to_FragmentCreateClassList));
        binding.feedbacksBtn.setOnClickListener(view1 -> NavHostFragment.findNavController(FragmentUserDetail.this).navigate(R.id.action_FragmentUserDetails_to_FragmentFeedbacksForTeacher));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}