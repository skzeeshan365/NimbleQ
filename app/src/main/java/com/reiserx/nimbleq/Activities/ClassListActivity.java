package com.reiserx.nimbleq.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.nimbleq.Adapters.classListAdapter;
import com.reiserx.nimbleq.Adapters.requestClassAdapter;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.subjectAndTimeSlot;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.ButtonDesign;
import com.reiserx.nimbleq.Utils.SnackbarTop;
import com.reiserx.nimbleq.Utils.UserTypeClass;
import com.reiserx.nimbleq.Utils.dialogs;
import com.reiserx.nimbleq.ViewModels.classViewModel;
import com.reiserx.nimbleq.ViewModels.slotsViewModel;
import com.reiserx.nimbleq.databinding.ActivityClassListBinding;

public class ClassListActivity extends AppCompatActivity implements MenuProvider {

    ActivityClassListBinding binding;

    LinearLayoutManager layoutManager;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore firestore;

    SnackbarTop snackbarTop;

    classViewModel classViewModel;

    subjectAndTimeSlot subjectAndTimeSlot;

    @SuppressLint({"CutPasteId", "NotifyDataSetChanged"})
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

        binding.progButton.setOnClickListener(view -> buttonDesign.buttonFill(binding.progButton));

        int dataType = getIntent().getIntExtra("dataType", 0);

        if (!userTypeClass.isUserLearner()) {
            if (dataType == 0) {
                setTitle(getString(R.string.your_classes));
                classListAdapter adapter = new classListAdapter(this);
                binding.recycler.setAdapter(adapter);

                classViewModel = new ViewModelProvider(this).get(com.reiserx.nimbleq.ViewModels.classViewModel.class);
                classViewModel.getClassListForTeacher(user.getUid());
                classViewModel.getClassList().observe(this, classModelList -> {
                    adapter.setClassList(classModelList);
                    adapter.notifyDataSetChanged();
                    binding.recycler.setVisibility(View.VISIBLE);
                    binding.progHolder.setVisibility(View.GONE);
                });
                classViewModel.getClassListErrorMutableLiveData().observe(this, s -> {
                    binding.textView9.setText(s);
                    binding.recycler.setVisibility(View.GONE);
                    binding.progHolder.setVisibility(View.VISIBLE);
                    binding.progressBar2.setVisibility(View.GONE);
                    binding.textView9.setVisibility(View.VISIBLE);
                });
            } else if (dataType == 1) {
                setTitle(getString(R.string.class_requests));
                requestClassAdapter requestClassAdapter = new requestClassAdapter(this, findViewById(android.R.id.content), user.getUid());
                binding.recycler.setAdapter(requestClassAdapter);

                slotsViewModel slotsViewModel = new ViewModelProvider(this).get(com.reiserx.nimbleq.ViewModels.slotsViewModel.class);

                slotsViewModel.getSubjectForTeachers(user.getUid());
                slotsViewModel.getParentItemMutableLiveData().observe(this, subjectAndTimeSlot -> {

                    classViewModel = new ViewModelProvider(this).get(com.reiserx.nimbleq.ViewModels.classViewModel.class);
                    classViewModel.getClassRequestsForTeachers(subjectAndTimeSlot, user.getUid());
                    classViewModel.getClassRequestMutableLiveData().observe(this, classRequestModels -> {
                        requestClassAdapter.setData(classRequestModels);
                        requestClassAdapter.notifyDataSetChanged();
                        binding.recycler.setVisibility(View.VISIBLE);
                        binding.progHolder.setVisibility(View.GONE);
                    });
                    classViewModel.getClassListErrorMutableLiveData().observe(this, s -> {
                        binding.textView9.setText(s);
                        binding.recycler.setVisibility(View.GONE);
                        binding.progHolder.setVisibility(View.VISIBLE);
                        binding.progressBar2.setVisibility(View.GONE);
                        binding.textView9.setVisibility(View.VISIBLE);
                    });
                });
            }
        } else {
            if (dataType == 0) {
                setTitle(getString(R.string.classes));
                classListAdapter adapter = new classListAdapter(this);
                binding.recycler.setAdapter(adapter);

                slotsViewModel slotsViewModel = new ViewModelProvider(this).get(com.reiserx.nimbleq.ViewModels.slotsViewModel.class);

                slotsViewModel.getSubjectForStudents(user.getUid());
                slotsViewModel.getParentItemMutableLiveData().observe(this, subjectAndTimeSlot -> {
                    this.subjectAndTimeSlot = subjectAndTimeSlot;
                    classViewModel = new ViewModelProvider(this).get(com.reiserx.nimbleq.ViewModels.classViewModel.class);
                    classViewModel.getClassList(subjectAndTimeSlot, user.getUid());
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

                    classViewModel.getClassListErrorMutableLiveData().observe(this, error -> {
                        binding.textView9.setText(getString(R.string.class_not_avail));
                        binding.recycler.setVisibility(View.GONE);
                        binding.progHolder.setVisibility(View.VISIBLE);
                        binding.progressBar2.setVisibility(View.GONE);
                        binding.textView9.setVisibility(View.VISIBLE);
                        binding.progButton.setVisibility(View.VISIBLE);
                        buttonDesign.setButtonOutline(binding.progButton);
                    });
                    removeMenuProvider(this);
                    addMenuProvider(this, this);
                    requestClass(subjectAndTimeSlot.getSubject(), subjectAndTimeSlot.getTopic(), subjectAndTimeSlot.getTimeSlot());
                });
            } else if (dataType == 1) {
                setTitle(getString(R.string.class_requests));

                requestClassAdapter requestClassAdapter = new requestClassAdapter(this, findViewById(android.R.id.content), user.getUid());
                binding.recycler.setAdapter(requestClassAdapter);

                slotsViewModel slotsViewModel = new ViewModelProvider(this).get(com.reiserx.nimbleq.ViewModels.slotsViewModel.class);

                slotsViewModel.getSubjectForStudents(user.getUid());
                slotsViewModel.getParentItemMutableLiveData().observe(this, subjectAndTimeSlot -> {

                    classViewModel = new ViewModelProvider(this).get(com.reiserx.nimbleq.ViewModels.classViewModel.class);
                    classViewModel.getClassRequestsForStudents(subjectAndTimeSlot, user.getUid());
                    classViewModel.getClassRequestMutableLiveData().observe(this, classRequestModels -> {
                        requestClassAdapter.setData(classRequestModels);
                        requestClassAdapter.notifyDataSetChanged();
                        binding.recycler.setVisibility(View.VISIBLE);
                        binding.progHolder.setVisibility(View.GONE);
                    });
                    classViewModel.getClassListErrorMutableLiveData().observe(this, s -> {
                        binding.textView9.setText(s);
                        binding.recycler.setVisibility(View.GONE);
                        binding.progHolder.setVisibility(View.VISIBLE);
                        binding.progressBar2.setVisibility(View.GONE);
                        binding.textView9.setVisibility(View.VISIBLE);
                    });
                });
            } else if (dataType == 2) {

                setTitle(getString(R.string.joined_classes));
                classListAdapter adapter = new classListAdapter(this);
                binding.recycler.setAdapter(adapter);

                classViewModel = new ViewModelProvider(this).get(com.reiserx.nimbleq.ViewModels.classViewModel.class);

                classViewModel.getAllJoinedClasses(user.getUid());
                classViewModel.getClassList().observe(this, classModelList -> {
                    adapter.setClassList(classModelList);
                    adapter.notifyDataSetChanged();
                    if (binding.recycler.getVisibility() == View.GONE && binding.progHolder.getVisibility() == View.VISIBLE) {
                        binding.recycler.setVisibility(View.VISIBLE);
                        binding.progHolder.setVisibility(View.GONE);
                    }
                });
                classViewModel.getClassListErrorMutableLiveData().observe(this, s -> {
                    binding.textView9.setText(s);
                    binding.recycler.setVisibility(View.GONE);
                    binding.progHolder.setVisibility(View.VISIBLE);
                    binding.progressBar2.setVisibility(View.GONE);
                    binding.textView9.setVisibility(View.VISIBLE);
                });
            }
        }
    }

    void requestClass(String subject, String topic, String timeslot) {
        binding.progButton.setOnClickListener(view -> {
            dialogs dialogs = new dialogs(this, findViewById(android.R.id.content));
            dialogs.requestClass(subject, topic, timeslot, user.getUid());
        });
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.request_class_menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.request_class_menuitem) {
            if (subjectAndTimeSlot != null) {
                dialogs dialogs = new dialogs(this, findViewById(android.R.id.content));
                dialogs.requestClass(subjectAndTimeSlot.getSubject(), subjectAndTimeSlot.getTopic(), subjectAndTimeSlot.getTimeSlot(), user.getUid());
            } else
                Log.d(CONSTANTS.TAG2, "null");
        }
        return false;
    }
}