package com.reiserx.nimbleq.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.zoomCredentials;
import com.reiserx.nimbleq.ViewModels.slotsViewModel;
import com.reiserx.nimbleq.databinding.ActivityMainBinding;

import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseUser user;

    SharedPreferences save;

    private slotsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(this);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = auth.getCurrentUser();

        save = getSharedPreferences("Utils", MODE_PRIVATE);

        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            getUserType();

            binding.viewClass.setOnClickListener(view -> {
                if (CONSTANTS.student_teacher_flag == 1) {
                    Intent intent = new Intent(MainActivity.this, ClassListActivity.class);
                    startActivity(intent);
                } else if (CONSTANTS.student_teacher_flag == 2) {
                    Intent intent = new Intent(this, SlotsListActivity.class);
                    startActivity(intent);
                }
            });

            binding.main3rdLayout.setOnClickListener(view -> {
                if (CONSTANTS.student_teacher_flag == 1) {

                } else if (CONSTANTS.student_teacher_flag == 2) {
                    Intent intent = new Intent(this, ClassListActivity.class);
                    intent.putExtra("isTeacher", true);
                    startActivity(intent);
                }
            });
            initializeSlot();
            zoomCredentials zoomCredentials = new zoomCredentials(CONSTANTS.SDK_KEY, CONSTANTS.SDK_SECRET);
            initializeZoomSdk(this, zoomCredentials);

            binding.main4thLayout.setOnClickListener(view -> {

            });

            binding.imageView17.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                startActivity(intent);
            });
        }
    }

    void getUserType() {
        CONSTANTS.student_teacher_flag = save.getInt("userType", 0);
        if (save.getInt("userType", 0) == 1) {
            binding.classTxt.setText("View class");
            binding.classDesc.setText("Click to view avaibale classes");
        } else if (save.getInt("userType", 0) == 2) {
            binding.classTxt.setText("Create class");
            binding.classDesc.setText("Click to view slots classes");
            binding.classTxt2.setText("View your classes");
            binding.classDesc2.setText("Click to classes you have created");
        }
    }

    @Override
    protected void onPostResume() {
        getUserType();
        super.onPostResume();
        initializeSlot();
    }

    public void initializeZoomSdk(Context context, zoomCredentials zoomCredentials) {
        ZoomSDK sdk = ZoomSDK.getInstance();
        ZoomSDKInitParams params = new ZoomSDKInitParams();
        params.appKey = zoomCredentials.getSDK_KEY();
        params.appSecret = zoomCredentials.getSDK_SECRET();
        params.domain = "zoom.us";
        params.enableLog = true;
        ZoomSDKInitializeListener listener = new ZoomSDKInitializeListener() {
            /**
             * @param errorCode {@link us.zoom.sdk.ZoomError#ZOOM_ERROR_SUCCESS} if the SDK has been initialized successfully.
             */
            @Override
            public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
            }

            @Override
            public void onZoomAuthIdentityExpired() {
            }
        };
        sdk.initialize(context, listener, params);
    }

    void initializeSlot() {

        viewModel = new ViewModelProvider(this).get(slotsViewModel.class);

        if (save.getInt("userType", 0) == 1) {
            viewModel.getSubjectForStudents(user.getUid());
        } else if (save.getInt("userType", 0) == 2) {
            viewModel.getSubjectForTeachers(user.getUid());
        }
        viewModel.getParentItemMutableLiveData().observe(this, subjectAndTimeSlot -> {
            if (subjectAndTimeSlot != null && subjectAndTimeSlot.isCurrent()) {
                if (subjectAndTimeSlot.getTimeSlot() != null) {
                    if (binding.slotTimeTxt.getVisibility() == View.GONE && binding.slotSubTxt.getVisibility() == View.GONE) {
                        binding.slotTimeTxt.setVisibility(View.VISIBLE);
                        binding.slotSubTxt.setVisibility(View.VISIBLE);
                    }
                    binding.slotTimeTxt.setText(subjectAndTimeSlot.getTimeSlot());
                    binding.slotSubTxt.setText(subjectAndTimeSlot.getSubject() + " • " + subjectAndTimeSlot.getTopic());
                } else {
                    if (binding.slotTimeTxt.getVisibility() == View.GONE && binding.slotSubTxt.getVisibility() == View.GONE) {
                        binding.slotTimeTxt.setVisibility(View.VISIBLE);
                        binding.slotSubTxt.setVisibility(View.VISIBLE);
                    }
                    binding.slotTimeTxt.setText(subjectAndTimeSlot.getSubject());
                    binding.slotSubTxt.setText(subjectAndTimeSlot.getTopic());
                }
            } else
                binding.slotHolder.setVisibility(View.GONE);
        });

        viewModel.getDatabaseErrorMutableLiveData().observe(this, error -> Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show());
    }
}