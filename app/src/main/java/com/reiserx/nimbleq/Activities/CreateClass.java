package com.reiserx.nimbleq.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.reiserx.nimbleq.Models.ClassRequestModel;
import com.reiserx.nimbleq.Models.classModel;
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

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseUser user;

    ClassRequestModel requestMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle("Create class");

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
                binding.classNameEdittext.setError("Please enter class name");
            else if (binding.topicNameEdittext.getText().toString().trim().equals(""))
                binding.topicNameEdittext.setError("Please enter topic");
            else if (binding.topicInfoEdittext.getText().toString().trim().equals(""))
                binding.topicInfoEdittext.setError("Field required");
            else if (binding.gradeSpinnerCreateClass.getSelectedItemPosition() == 0)
                snackbarTop.showSnackBar("Please select grade", false);
            else if (binding.zoomMeetingId.getText().toString().trim().equals(""))
                binding.zoomMeetingId.setError("Field required");
            else if (binding.zoomMeetingPass.getText().toString().trim().equals(""))
                binding.zoomMeetingPass.setError("Field required");
            else {
                snackbarTop.showSnackBar("Processing", true);

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
                        classViewModel.createClass(CreateClass.this, classModel, s, requestMode);
                    else
                        classViewModel.createClass(CreateClass.this, classModel, s);
                });

                classViewModel.getCreateClassMutableLiveData().observe(this, s -> {
                    dialogs dialogs = new dialogs(CreateClass.this, findViewById(android.R.id.content));
                    dialogs.meetingCreated(CreateClass.this, s);
                });
            }
        });
    }

    void start_meeting() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Create zoom meeting");
        alert.setMessage("Please open zoom and create a recurring meeting for this class\nAnd paste the meeting info below");
        alert.setPositiveButton("open", (dialogInterface, i) -> {
            PackageManager pm = getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage("us.zoom.videomeetings");
            if (intent != null) {
                startActivity(intent);
            }
        });
        alert.setNegativeButton("cancel", null);
        alert.show();
    }
}