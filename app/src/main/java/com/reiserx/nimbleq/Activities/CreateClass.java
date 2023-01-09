package com.reiserx.nimbleq.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.nimbleq.Models.classModel;
import com.reiserx.nimbleq.Utils.ButtonDesign;
import com.reiserx.nimbleq.Utils.SnackbarTop;
import com.reiserx.nimbleq.Utils.dialogs;
import com.reiserx.nimbleq.databinding.ActivityCreateClassBinding;

public class CreateClass extends AppCompatActivity {

    ActivityCreateClassBinding binding;

    String[] gradeList;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle("Create class");

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        String slot = getIntent().getExtras().getString("slot");
        String subject = getIntent().getExtras().getString("subject");
        String topic = getIntent().getExtras().getString("topic");

        SnackbarTop snackbarTop = new SnackbarTop(findViewById(android.R.id.content));

        binding.subjectNameTxt.setText(subject);
        binding.topicNameEdittext.setText(topic);

        gradeList = new String[]{"Select grade", "Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5",
                "Grade 6", "Grade 7", "Grade 8", "Grade 9", "Grade 10"};

        ArrayAdapter<String> gradesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gradeList);
        gradesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.gradeSpinnerCreateClass.setAdapter(gradesAdapter);

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

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
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

                CollectionReference collection = firestore.collection("Main").document("Class").collection("ClassInfo");
                collection.add(classModel).addOnSuccessListener(documentReference1 -> {
                    dialogs dialogs = new dialogs(CreateClass.this, findViewById(android.R.id.content));
                    dialogs.meetingCreated(CreateClass.this, documentReference1.getId());
                }).addOnFailureListener(e -> {


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