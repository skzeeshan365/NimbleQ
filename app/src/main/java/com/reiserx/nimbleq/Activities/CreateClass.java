package com.reiserx.nimbleq.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.reiserx.nimbleq.Models.ClassRequestModel;
import com.reiserx.nimbleq.Models.classModel;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.ButtonDesign;
import com.reiserx.nimbleq.Utils.SnackbarTop;
import com.reiserx.nimbleq.Utils.dialogs;
import com.reiserx.nimbleq.ViewModels.AdministrationViewModel;
import com.reiserx.nimbleq.ViewModels.UserDataViewModel;
import com.reiserx.nimbleq.ViewModels.classViewModel;
import com.reiserx.nimbleq.databinding.ActivityCreateClassBinding;

import java.util.ArrayList;
import java.util.List;

public class CreateClass extends AppCompatActivity {

    ActivityCreateClassBinding binding;

    List<String> gradeList;
    List<String> lectureList;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseUser user;

    ClassRequestModel requestMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle(getString(R.string.create_class));

        if (getIntent().getBooleanExtra("requestMode", false)) {
            Gson gson = new Gson();
            requestMode = gson.fromJson(getIntent().getStringExtra("request"), ClassRequestModel.class);
        }

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        String slot = getIntent().getExtras().getString("slot");
        String subject = getIntent().getExtras().getString("subject");
        String topic = getIntent().getExtras().getString("topic");

        SnackbarTop snackbarTop = new SnackbarTop(findViewById(android.R.id.content));

        binding.subjectNameTxt.setText(subject);
        binding.topicNameEdittext.setText(topic);

        AdministrationViewModel viewModel = new ViewModelProvider(this).get(AdministrationViewModel.class);

        viewModel.getGradeList();
        viewModel.getListStringMutableLiveData().observe(this, stringList -> {
            gradeList = stringList;

            ArrayAdapter<String> gradesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gradeList);
            gradesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.gradeSpinnerCreateClass.setAdapter(gradesAdapter);
        });

        viewModel.getLecturesLimit();
        viewModel.getLecturesLimitMutableLiveData().observe(this, lectures -> {
            lectureList = new ArrayList<>();
            lectureList.add("Select lectures");
            for (int i = 1; i <= lectures; i++) {
                lectureList.add(String.valueOf(i));
            }

            ArrayAdapter<String> lecturesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lectureList);
            lecturesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.lecturesSpinner.setAdapter(lecturesAdapter);
        });

        binding.topicNameEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.topicNameEdittext.getText().toString().trim().equals(topic))
                    binding.textView22.setVisibility(View.GONE);
                else
                    binding.textView22.setVisibility(View.VISIBLE);
            }
        });

        ButtonDesign buttonDesign = new ButtonDesign(this);
        buttonDesign.setButtonOutline(binding.button6);
        buttonDesign.setButtonOutline(binding.button7);

        binding.button7.setOnClickListener(view -> {
            start_meeting();
            buttonDesign.buttonFill(binding.button7);
        });

        binding.button6.setOnClickListener(view -> {
            buttonDesign.buttonFill(binding.button6);

            if (binding.classNameEdittext.getText().toString().trim().equals(""))
                binding.classNameEdittext.setError(getString(R.string.enter_class_name));
            else if (binding.topicNameEdittext.getText().toString().trim().equals(""))
                binding.topicNameEdittext.setError(getString(R.string.please_enter_topic));
            else if (binding.topicInfoEdittext.getText().toString().trim().equals(""))
                binding.topicInfoEdittext.setError(getString(R.string.field_required));
            else if (binding.lecturesSpinner.getSelectedItemPosition() == 0)
                snackbarTop.showSnackBar(getString(R.string.please_select_lectures), false);
            else if (binding.gradeSpinnerCreateClass.getSelectedItemPosition() == 0)
                snackbarTop.showSnackBar(getString(R.string.please_select_grade), false);
            else if (binding.zoomMeetingId.getText().toString().trim().equals(""))
                binding.zoomMeetingId.setError(getString(R.string.field_required));
            else if (binding.zoomMeetingPass.getText().toString().trim().equals(""))
                binding.zoomMeetingPass.setError(getString(R.string.field_required));
            else {
                snackbarTop.showSnackBar(getString(R.string.processing), true);

                binding.button6.setEnabled(false);
                binding.button7.setEnabled(false);
                binding.gradeSpinnerCreateClass.setEnabled(false);
                binding.topicNameEdittext.setEnabled(false);
                binding.topicInfoEdittext.setEnabled(false);
                binding.classNameEdittext.setEnabled(false);

                classModel classModel = new classModel();
                classModel.setClassName(binding.classNameEdittext.getText().toString());
                classModel.setSubject(binding.subjectNameTxt.getText().toString());
                classModel.setTopic(binding.topicNameEdittext.getText().toString());
                classModel.setClassInfo(binding.topicInfoEdittext.getText().toString());
                classModel.setGrade(binding.gradeSpinnerCreateClass.getSelectedItem().toString());
                classModel.setMeetingID(binding.zoomMeetingId.getText().toString());
                classModel.setMeetingPassword(binding.zoomMeetingPass.getText().toString());
                classModel.setTeacher_info(user.getUid());
                classModel.setTime_slot(slot);

                UserDataViewModel userDataViewModel = new ViewModelProvider(this).get(UserDataViewModel.class);
                classViewModel classViewModel = new ViewModelProvider(CreateClass.this).get(com.reiserx.nimbleq.ViewModels.classViewModel.class);
                userDataViewModel.getUsername(user.getUid());
                userDataViewModel.getUserName().observe(this, s -> {
                    if (requestMode != null)
                        classViewModel.createClass(CreateClass.this, classModel, s, Integer.parseInt(binding.lecturesSpinner.getSelectedItem().toString()), requestMode);
                    else
                        classViewModel.createClass(CreateClass.this, classModel, s, Integer.parseInt(binding.lecturesSpinner.getSelectedItem().toString()));
                });

                classViewModel.getCreateClassMutableLiveData().observe(this, s -> {
                    @SuppressLint("CutPasteId") dialogs dialogs = new dialogs(CreateClass.this, findViewById(android.R.id.content));
                    dialogs.meetingCreated(CreateClass.this, s);
                });
            }
        });
    }

    void start_meeting() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.create_zoom_meeting));
        alert.setMessage(getString(R.string.create_zoom_meeting_msg));
        alert.setPositiveButton(getString(R.string.open), (dialogInterface, i) -> {
            PackageManager pm = getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage("us.zoom.videomeetings");
            if (intent != null) {
                startActivity(intent);
            }
        });
        alert.setNegativeButton(getString(R.string.cancel), null);
        alert.show();
    }
}