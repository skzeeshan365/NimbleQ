package com.reiserx.nimbleq.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.dialogs;
import com.reiserx.nimbleq.ViewModels.AdministrationViewModel;
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

        setTitle(getString(R.string.settings));

        dialogs = new dialogs(this, findViewById(android.R.id.content));

        binding.settingsLogoutLt.setOnClickListener(view -> {
            if (user != null) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(getString(R.string.logout));
                alert.setMessage(getString(R.string.logout_msg));
                alert.setPositiveButton(getString(R.string.logout), (dialogInterface, i) -> {
                    auth.signOut();
                    Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                });
                alert.setNegativeButton(getString(R.string.cancel), null);
                alert.show();
            }
        });

        binding.subjectslotHolder.setOnClickListener(view -> {
            Intent intent = new Intent(this, SlotsActivity.class);
            startActivity(intent);
        });

        binding.usertypeHolder.setOnClickListener(view -> {
            dialogs.selectStudentOrTeacherNormal(user.getUid());
            getSubject();
        });

        getUserInfo();
        getUserType();

        binding.langHolder.setOnClickListener(view -> dialogs.setLanguage(UserProfileActivity.this));

        SharedPreferences sharedPreferences = getSharedPreferences("selectedLanguage", Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            if (sharedPreferences.getString("language", null) == null)
                binding.langTxt.setText(getString(R.string.app_lang_default));
            else if (sharedPreferences.getString("language", null).equals("en"))
                binding.langTxt.setText(getString(R.string.app_lang_english));
            else if (sharedPreferences.getString("language", null).equals("hi"))
                binding.langTxt.setText(getString(R.string.app_lang_hindi));
            else
                binding.langTxt.setText(getString(R.string.app_lang_default));
        }

        binding.licenseHolder.setOnClickListener(view -> startActivity(new Intent(this, OssLicensesMenuActivity.class)));

        updateLinks();
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
        userDataViewModel.getUserTypeMutableLiveData().observe(this, userType -> {
            studentOrTecher = userType.getCurrentStatus();
            if (userType.getCurrentStatus() == 1) {
                binding.usertypeTxt.setText(getString(R.string.logged_in_as_learner));
                binding.textView17.setText(getString(R.string.tap_to_switch_for_teacher));
            } else {
                binding.usertypeTxt.setText(getString(R.string.logged_in_as_teacher));
                binding.textView17.setText(getString(R.string.tap_to_switch_for_learner));
            }
            getSubject();
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

    @Override
    protected void onPostResume() {
        super.onPostResume();

        getSubject();
    }

    void updateLinks() {
        AdministrationViewModel viewModel = new ViewModelProvider(this).get(AdministrationViewModel.class);

        viewModel.getLinkPrivacyPolicy();
        viewModel.getLinkTermsOfService();

        viewModel.getLinkPrivacyPolicyMutableLiveData().observe(this, s -> binding.privacyHolder.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
            startActivity(browserIntent);
        }));

        viewModel.getLinkTermsOfServiceMutableLiveData().observe(this, s -> binding.licenseHolder.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
            startActivity(browserIntent);
        }));

        binding.aboutHolder.setOnClickListener(view ->  FragmentAbout.display(getSupportFragmentManager()));
    }
}