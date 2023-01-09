package com.reiserx.nimbleq.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.Models.subjectAndTimeSlot;
import com.reiserx.nimbleq.Models.userType;
import com.reiserx.nimbleq.Utils.dialogs;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = auth.getCurrentUser();

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
        database.getReference().child("Data").child("UserData").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserData userData = snapshot.getValue(UserData.class);
                    if (userData != null) {
                        binding.usernameTxt.setText(userData.getUserName());
                        binding.phoneNumberTxt.setText(userData.getPhoneNumber());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void getUserType() {
        userTypeReference = database.getReference().child("Data").child("Main").child("UserType").child(user.getUid());
        userTypeListener = userTypeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userType userType = snapshot.getValue(com.reiserx.nimbleq.Models.userType.class);
                    if (userType != null) {
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void getSubject() {
        Log.d(CONSTANTS.TAG, String.valueOf(studentOrTecher));
        if (studentOrTecher == 1) {
            subjectReference = database.getReference().child("Data").child("Main").child("SubjectList").child("subjectForStudents").child(user.getUid());
        } else if (studentOrTecher == 2)
            subjectReference = database.getReference().child("Data").child("Main").child("SubjectList").child("subjectForTeacher").child(user.getUid());

        subjectReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        subjectAndTimeSlot subjectAndTimeSlot = snapshot1.getValue(com.reiserx.nimbleq.Models.subjectAndTimeSlot.class);
                        if (subjectAndTimeSlot != null && subjectAndTimeSlot.isCurrent()) {
                            if (subjectAndTimeSlot.getTimeSlot() != null)
                                binding.subjectAndTimeSlotTxt.setText(subjectAndTimeSlot.getTimeSlot().concat(" • ").concat(subjectAndTimeSlot.getSubject().concat(" • ").concat(subjectAndTimeSlot.getTopic())));
                            else
                                binding.subjectAndTimeSlotTxt.setText(subjectAndTimeSlot.getSubject().concat(" • ").concat(subjectAndTimeSlot.getTopic()));
                        }
                    }
                } else {
                    if (studentOrTecher == 1) {
                        dialogs.selectSubjectForLearnerNormal(user.getUid(), false);
                    } else if (studentOrTecher == 2)
                        dialogs.selectSubjectForTeacherNormal(user.getUid(), false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
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