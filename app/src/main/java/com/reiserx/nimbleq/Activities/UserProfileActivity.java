package com.reiserx.nimbleq.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.nimbleq.Models.userType;
import com.reiserx.nimbleq.Utils.dialogs;
import com.reiserx.nimbleq.ViewModels.UserDataViewModel;
import com.reiserx.nimbleq.ViewModels.slotsViewModel;
import com.reiserx.nimbleq.databinding.ActivityUserProfileBinding;

public class UserProfileActivity extends AppCompatActivity {

    ActivityUserProfileBinding binding;

    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference userTypeReference, subjectReference;

    ValueEventListener userTypeListener, subjectListener;

    int studentOrTecher = 0;

    dialogs dialogs;

    UserDataViewModel userDataViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = auth.getCurrentUser();

        userDataViewModel = new ViewModelProvider(this).get(UserDataViewModel.class);

        setTitle("Settings");

        dialogs = new dialogs(this, findViewById(android.R.id.content));

        binding.settingsLogoutLt.setOnClickListener(view -> {
            if (user != null) {
                auth.signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        binding.subjectslotHolder.setOnClickListener(view -> {
            Intent intent = new Intent(this, SlotsActivity.class);
            intent.putExtra("flag", studentOrTecher);
            startActivity(intent);
        });

        binding.usertypeHolder.setOnClickListener(view -> {
            dialogs.selectStudentOrTeacherNormal(user.getUid());
        });

        getUserInfo();
        getUserType();
    }

    void getUserInfo() {
        userDataViewModel.getUserData(user.getUid());
        userDataViewModel.getUserData().observe(this, userData -> {
            binding.usernameTxt.setText(userData.getUserName());
            binding.phoneNumberTxt.setText(userData.getPhoneNumber());
        });
    }

    void getUserType() {
        userDataViewModel.getUserType(user.getUid());
        userDataViewModel.getUserTypeMutableLiveData().observe(this, new Observer<userType>() {
            @Override
            public void onChanged(userType userType) {
                studentOrTecher = userType.getCurrentStatus();
                if (userType.getCurrentStatus() == 1) {
                    binding.usertypeTxt.setText("Logged in as learner");
                    binding.textView17.setText("Tap to switch for teacher");
                } else {
                    binding.usertypeTxt.setText("Logged in as teacher");
                    binding.textView17.setText("Tap to switch for learner");
                }
                getSubject();
            }
        });
    }

    void getSubject() {
        slotsViewModel slotsViewModel = new ViewModelProvider(this).get(com.reiserx.nimbleq.ViewModels.slotsViewModel.class);
        if (studentOrTecher == 1) {
            slotsViewModel.getSubjectForStudents(user.getUid());
        } else if (studentOrTecher == 2)
            slotsViewModel.getSubjectForTeachers(user.getUid());

        slotsViewModel.getParentItemMutableLiveData().observe(this, subjectAndTimeSlot -> {
            if (subjectAndTimeSlot.getTimeSlot() != null)
                binding.subjectAndTimeSlotTxt.setText(subjectAndTimeSlot.getTimeSlot().concat(" • ").concat(subjectAndTimeSlot.getSubject().concat(" • ").concat(subjectAndTimeSlot.getTopic())));
            else
                binding.subjectAndTimeSlotTxt.setText(subjectAndTimeSlot.getSubject().concat(" • ").concat(subjectAndTimeSlot.getTopic()));
        });
        slotsViewModel.getDatabaseErrorMutableLiveData().observe(this, databaseError -> {
            if (studentOrTecher == 1) {
                dialogs.selectSubjectForLearnerNormal(user.getUid(), false);
            } else if (studentOrTecher == 2)
                dialogs.selectSubjectForTeacherNormal(user.getUid(), false);
        });
    }

    @Override
    protected void onDestroy() {
        if (userTypeListener != null)
            userTypeReference.removeEventListener(userTypeListener);
        else if (subjectListener != null)
            subjectReference.removeEventListener(subjectListener);
        super.onDestroy();
    }
}