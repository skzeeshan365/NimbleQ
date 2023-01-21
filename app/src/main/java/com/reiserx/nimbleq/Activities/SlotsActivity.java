package com.reiserx.nimbleq.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.nimbleq.Adapters.slotsAdapter;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.subjectAndTimeSlot;
import com.reiserx.nimbleq.Utils.ButtonDesign;
import com.reiserx.nimbleq.Utils.UserTypeClass;
import com.reiserx.nimbleq.Utils.dialogs;
import com.reiserx.nimbleq.databinding.ActivitySlotsBinding;

import java.util.ArrayList;

public class SlotsActivity extends AppCompatActivity {

    ActivitySlotsBinding binding;

    ArrayList<subjectAndTimeSlot> data;
    slotsAdapter adapter;

    LinearLayoutManager layoutManager;

    DatabaseReference subjectReference;
    FirebaseAuth auth;
    FirebaseDatabase database;

    UserTypeClass userTypeClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySlotsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle("Slots");

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        binding.button4.setVisibility(View.GONE);

        data = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        binding.recycler.setLayoutManager(layoutManager);
        adapter = new slotsAdapter(this, data, findViewById(android.R.id.content), false, null);
        binding.recycler.setAdapter(adapter);

        userTypeClass = new UserTypeClass(this);

        getSubject(user);

        ButtonDesign buttonDesign = new ButtonDesign(this);
        buttonDesign.setButtonOutline(binding.button4);
        binding.button4.setOnClickListener(view -> {
            buttonDesign.buttonFill(binding.button4);
            dialogs dialogs = new dialogs(this, findViewById(android.R.id.content));
            if (userTypeClass.isUserLearner())
                dialogs.selectSubjectForLearnerNormal(user.getUid(), false);
            else if (!userTypeClass.isUserLearner())
                dialogs.selectSubjectForTeacherNormal(user.getUid(), false);
        });

    }

    void getSubject(FirebaseUser user) {
        if (userTypeClass.isUserLearner()) {
            subjectReference = database.getReference().child("Data").child("Main").child("SubjectList").child("subjectForStudents").child(user.getUid());
        } else if (!userTypeClass.isUserLearner())
            subjectReference = database.getReference().child("Data").child("Main").child("SubjectList").child("subjectForTeacher").child(user.getUid());

        subjectReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        subjectAndTimeSlot subjectAndTimeSlot = snapshot1.getValue(com.reiserx.nimbleq.Models.subjectAndTimeSlot.class);
                        subjectAndTimeSlot.setKey(snapshot1.getKey());
                        subjectAndTimeSlot.setReference(subjectReference);
                        data.add(subjectAndTimeSlot);
                    }
                    if (data.size() < 3)
                        binding.button4.setVisibility(View.VISIBLE);
                    else
                        binding.button4.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}