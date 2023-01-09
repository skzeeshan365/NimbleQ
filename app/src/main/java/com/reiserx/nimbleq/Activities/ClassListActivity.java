package com.reiserx.nimbleq.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.nimbleq.Adapters.classListAdapter;
import com.reiserx.nimbleq.Adapters.requestClassAdapter;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.ButtonDesign;
import com.reiserx.nimbleq.Utils.SnackbarTop;
import com.reiserx.nimbleq.Utils.UserTypeClass;
import com.reiserx.nimbleq.Utils.dialogs;
import com.reiserx.nimbleq.ViewModels.classViewModel;
import com.reiserx.nimbleq.ViewModels.slotsViewModel;
import com.reiserx.nimbleq.databinding.ActivityClassListBinding;

public class ClassListActivity extends AppCompatActivity {

    ActivityClassListBinding binding;

    LinearLayoutManager layoutManager;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore firestore;

    SnackbarTop snackbarTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClassListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UserTypeClass userTypeClass = new UserTypeClass(this);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        binding.recycler.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);
        binding.progButton.setVisibility(View.GONE);

        ButtonDesign buttonDesign = new ButtonDesign(this);

        snackbarTop = new SnackbarTop(findViewById(android.R.id.content));

        layoutManager = new LinearLayoutManager(this);
        binding.recycler.setLayoutManager(layoutManager);

        binding.progButton.setOnClickListener(view -> {
            buttonDesign.buttonFill(binding.progButton);
        });

        boolean isClass = getIntent().getBooleanExtra("isClass", false);

        if (!userTypeClass.isUserLearner()) {
            if (isClass) {
                setTitle("Classes");
                classListAdapter adapter = new classListAdapter(this);
                binding.recycler.setAdapter(adapter);

                classViewModel classViewModel = new ViewModelProvider(this).get(com.reiserx.nimbleq.ViewModels.classViewModel.class);
                classViewModel.getClassListForTeacher(user.getUid());
                classViewModel.getClassList().observe(this, classModelList -> {
                    adapter.setClassList(classModelList);
                    adapter.notifyDataSetChanged();
                    binding.recycler.setVisibility(View.VISIBLE);
                    binding.progHolder.setVisibility(View.GONE);
                });
                classViewModel.getDatabaseErrorMutableLiveData().observe(this, s -> {
                    binding.textView9.setText(s);
                    binding.recycler.setVisibility(View.GONE);
                    binding.progHolder.setVisibility(View.VISIBLE);
                    binding.progressBar2.setVisibility(View.GONE);
                    binding.textView9.setVisibility(View.VISIBLE);
                });
            } else {
                setTitle("Class Requests");
                requestClassAdapter requestClassAdapter = new requestClassAdapter(this, findViewById(android.R.id.content));
                binding.recycler.setAdapter(requestClassAdapter);

                slotsViewModel slotsViewModel = new ViewModelProvider(this).get(com.reiserx.nimbleq.ViewModels.slotsViewModel.class);

                slotsViewModel.getSubjectForStudents(user.getUid());
                slotsViewModel.getParentItemMutableLiveData().observe(this, subjectAndTimeSlot -> {

                    classViewModel classViewModel = new ViewModelProvider(this).get(com.reiserx.nimbleq.ViewModels.classViewModel.class);
                    classViewModel.getClassRequests(subjectAndTimeSlot);
                    classViewModel.getClassRequestMutableLiveData().observe(this, classRequestModels -> {
                        requestClassAdapter.setData(classRequestModels);
                        requestClassAdapter.notifyDataSetChanged();
                        binding.recycler.setVisibility(View.VISIBLE);
                        binding.progHolder.setVisibility(View.GONE);
                    });
                    classViewModel.getDatabaseErrorMutableLiveData().observe(this, s -> {
                        binding.textView9.setText(s);
                        binding.recycler.setVisibility(View.GONE);
                        binding.progHolder.setVisibility(View.VISIBLE);
                        binding.progressBar2.setVisibility(View.GONE);
                        binding.textView9.setVisibility(View.VISIBLE);
                        snackbarTop.showSnackBar(s, false);
                    });
                });
            }
        } else {
            if (isClass) {
                setTitle("Classes");
                classListAdapter adapter = new classListAdapter(this);
                binding.recycler.setAdapter(adapter);

                slotsViewModel slotsViewModel = new ViewModelProvider(this).get(com.reiserx.nimbleq.ViewModels.slotsViewModel.class);

                slotsViewModel.getSubjectForStudents(user.getUid());
                slotsViewModel.getParentItemMutableLiveData().observe(this, subjectAndTimeSlot -> {

                    classViewModel classViewModel = new ViewModelProvider(this).get(com.reiserx.nimbleq.ViewModels.classViewModel.class);
                    classViewModel.getClassList(subjectAndTimeSlot);
                    classViewModel.getClassList().observe(this, classModelList -> {
                        if (!classModelList.isEmpty()) {
                            adapter.setClassList(classModelList);
                            adapter.notifyDataSetChanged();
                            binding.recycler.setVisibility(View.VISIBLE);
                            binding.progHolder.setVisibility(View.GONE);
                        } else {
                            binding.textView9.setText(getString(R.string.class_not_avail));
                            binding.recycler.setVisibility(View.GONE);
                            binding.progHolder.setVisibility(View.VISIBLE);
                            binding.progressBar2.setVisibility(View.GONE);
                            binding.textView9.setVisibility(View.VISIBLE);
                            binding.progButton.setVisibility(View.VISIBLE);
                            buttonDesign.setButtonOutline(binding.progButton);
                        }
                    });

                    classViewModel.getDatabaseErrorMutableLiveData().observe(this, error -> {
                        binding.textView9.setText(getString(R.string.class_not_avail));
                        binding.recycler.setVisibility(View.GONE);
                        binding.progHolder.setVisibility(View.VISIBLE);
                        binding.progressBar2.setVisibility(View.GONE);
                        binding.textView9.setVisibility(View.VISIBLE);
                        binding.progButton.setVisibility(View.VISIBLE);
                        buttonDesign.setButtonOutline(binding.progButton);
                    });
                    requestClass(subjectAndTimeSlot.getSubject(), subjectAndTimeSlot.getTopic(), subjectAndTimeSlot.getTimeSlot());
                });
            } else {
                setTitle("Class Requests");

                requestClassAdapter requestClassAdapter = new requestClassAdapter(this, findViewById(android.R.id.content));
                binding.recycler.setAdapter(requestClassAdapter);

                slotsViewModel slotsViewModel = new ViewModelProvider(this).get(com.reiserx.nimbleq.ViewModels.slotsViewModel.class);

                slotsViewModel.getSubjectForStudents(user.getUid());
                slotsViewModel.getParentItemMutableLiveData().observe(this, subjectAndTimeSlot -> {

                    classViewModel classViewModel = new ViewModelProvider(this).get(com.reiserx.nimbleq.ViewModels.classViewModel.class);
                    classViewModel.getClassRequests(subjectAndTimeSlot);
                    classViewModel.getClassRequestMutableLiveData().observe(this, classRequestModels -> {
                        requestClassAdapter.setData(classRequestModels);
                        requestClassAdapter.notifyDataSetChanged();
                        binding.recycler.setVisibility(View.VISIBLE);
                        binding.progHolder.setVisibility(View.GONE);
                    });
                    classViewModel.getDatabaseErrorMutableLiveData().observe(this, s -> {
                        binding.textView9.setText(s);
                        binding.recycler.setVisibility(View.GONE);
                        binding.progHolder.setVisibility(View.VISIBLE);
                        binding.progressBar2.setVisibility(View.GONE);
                        binding.textView9.setVisibility(View.VISIBLE);
                        snackbarTop.showSnackBar(s, false);
                    });
                });
            }
        }
    }

    void requestClass(String subject, String topic, String timeslot) {
        binding.progButton.setOnClickListener(view -> {
            dialogs dialogs = new dialogs(this, findViewById(android.R.id.content));
            dialogs.requestClass(subject, topic, timeslot);
        });
    }
}