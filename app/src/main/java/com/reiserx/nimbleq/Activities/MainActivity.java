package com.reiserx.nimbleq.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.reiserx.nimbleq.Activities.Administration.AdministrationActivity;
import com.reiserx.nimbleq.Activities.Doubts.DoubtsActivity;
import com.reiserx.nimbleq.Activities.Feedbacks.FeedbackListActivity;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.FCMCREDENTIALS;
import com.reiserx.nimbleq.Models.zoomCredentials;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.SharedPreferenceClass;
import com.reiserx.nimbleq.Utils.UserTypeClass;
import com.reiserx.nimbleq.ViewModels.AdministrationViewModel;
import com.reiserx.nimbleq.ViewModels.UserDataViewModel;
import com.reiserx.nimbleq.ViewModels.slotsViewModel;
import com.reiserx.nimbleq.databinding.ActivityMainBinding;

import java.util.Locale;

import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseUser user;

    SharedPreferences save;

    UserTypeClass userTypeClass;

    private slotsViewModel viewModel;

    public static final String Default="en";

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
            //Main logic starts
            userTypeClass = new UserTypeClass(this);

            checkAdmin(user.getUid());

            getUserType();

            initializeSlot();

            initializeZoomSdk(this);
            initializeFCM();

            binding.imageView17.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                startActivity(intent);
            });

            UserDataViewModel userDataViewModel = new ViewModelProvider(this).get(UserDataViewModel.class);
            userDataViewModel.updateFCMToken(user.getUid());

            binding.viewClass.setOnClickListener(view -> {
                if (userTypeClass.isUserLearner()) {
                    Intent intent = new Intent(MainActivity.this, ClassListActivity.class);
                    intent.putExtra("dataType", 0);
                    startActivity(intent);
                } else if (!userTypeClass.isUserLearner()) {
                    Intent intent = new Intent(this, SlotsListActivity.class);
                    startActivity(intent);
                }
            });

            binding.main4thLayout.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, ClassListActivity.class);
                intent.putExtra("dataType", 1);
                startActivity(intent);
            });

            binding.main3rdLayout.setOnClickListener(view -> {
                if (userTypeClass.isUserLearner()) {
                    Intent intent = new Intent(this, ClassListActivity.class);
                    intent.putExtra("dataType", 2);
                    startActivity(intent);
                } else if (!userTypeClass.isUserLearner()) {
                    Intent intent = new Intent(this, ClassListActivity.class);
                    startActivity(intent);
                }
            });

            binding.doubtsLt.setOnClickListener(view -> {
                Intent intent = new Intent(this, DoubtsActivity.class);
                startActivity(intent);
            });
        }
    }

    void getUserType() {
        if (userTypeClass.isUserLearner()) {

            //holder 1
            binding.classTxt.setText(getString(R.string.view_class));
            binding.classDesc.setText(getString(R.string.view_class_msg));
            //holder 1

            //holder 2
            binding.textView23.setText(getString(R.string.solve_doubts));
            binding.textView24.setText(getString(R.string.solve_doubts_msg));
            //holder 2

            //holder 3
            binding.classTxt2.setText(getString(R.string.view_joined_class));
            binding.classDesc2.setText(getString(R.string.view_joined_class_msg));
            //holder 3

            //holder 4
            binding.classTxt3.setText(getString(R.string.view_your_class_requests));
            binding.classDesc3.setText(getString(R.string.view_your_class_requests_msg));
            //holder 4
            binding.feedbacksHolder.setVisibility(View.GONE);

        } else if (!userTypeClass.isUserLearner()) {

            //holder 1
            binding.classTxt.setText(getString(R.string.create_class));
            binding.classDesc.setText(getString(R.string.create_class_msg));
            //holder 1

            //holder 2
            binding.textView23.setText(getString(R.string.view_doubts));
            binding.textView24.setText(getString(R.string.view_doubts_msg));
            //holder 2

            //holder 3
            binding.classTxt2.setText(getString(R.string.view_your_class));
            binding.classDesc2.setText(getString(R.string.view_your_class_msg));
            //holder 3

            //holder 4
            binding.classTxt3.setText(getString(R.string.view_class_requests));
            binding.classDesc3.setText(getString(R.string.view_class_requests_msg));
            //holder 4

            //holder 5
            binding.feedbacksHolder.setVisibility(View.VISIBLE);
            binding.feedbacksLayout.setOnClickListener(view -> {
                Intent intent = new Intent(this, FeedbackListActivity.class);
                startActivity(intent);
            });
            //holder 5
        }
    }

    @Override
    protected void onPostResume() {
        getUserType();
        super.onPostResume();
        initializeSlot();
    }

    public void initializeZoomSdk(Context context) {
        AdministrationViewModel viewModel = new ViewModelProvider(this).get(AdministrationViewModel.class);
        viewModel.getZoomCredentials();
        viewModel.getZoomCredentialsMutableLiveData().observe(this, zoomCredentials -> {
            ZoomSDK sdk = ZoomSDK.getInstance();
            ZoomSDKInitParams params = new ZoomSDKInitParams();
            params.appKey = zoomCredentials.getSDK_KEY();
            params.appSecret = zoomCredentials.getSDK_SECRET();
            params.domain = "zoom.us";
            params.enableLog = true;
            ZoomSDKInitializeListener listener = new ZoomSDKInitializeListener() {
                @Override
                public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
                }

                @Override
                public void onZoomAuthIdentityExpired() {
                }
            };
            sdk.initialize(context, listener, params);
        });
        viewModel.getDatabaseErrorMutableLiveData().observe(this, s -> Toast.makeText(context, s, Toast.LENGTH_SHORT).show());
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
            } else {
                binding.slotTimeTxt.setVisibility(View.VISIBLE);
                binding.slotSubTxt.setVisibility(View.GONE);
                binding.slotTimeTxt.setText("Slot not available, please select a slot from settings");
            }
        });

        viewModel.getDatabaseErrorMutableLiveData().observe(this, error -> Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show());
    }
    void initializeFCM() {
        AdministrationViewModel viewModel = new ViewModelProvider(this).get(AdministrationViewModel.class);
        viewModel.getFCMCredentials();
        viewModel.getFCMCredentialsMutableLiveData().observe(this, fcmcredentials -> {
            SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(MainActivity.this);
            sharedPreferenceClass.setFCMKey(fcmcredentials);
        });
        viewModel.getDatabaseErrorMutableLiveData().observe(this, s -> Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show());
    }

    private void checkAdmin(String userID) {
        AdministrationViewModel viewModel = new ViewModelProvider(this).get(AdministrationViewModel.class);
        viewModel.getAdministrator(userID);
        viewModel.getAdminMutableLiveData().observe(this, aBoolean -> {
            Intent intent = new Intent(MainActivity.this, AdministrationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    protected void onStart() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("selectedLanguage", Context.MODE_PRIVATE);
        String pine = sharedPreferences.getString("language", Default);
        Locale locale;
        locale = new Locale(pine);
        Locale.setDefault(locale);//set new locale as default
        Configuration config = new Configuration();//get Configuration
        config.locale = locale;//set config locale as selected locale
        this.getResources().updateConfiguration(config, this.getResources().getDisplayMetrics());
        invalidateOptionsMenu();
        setTitle(R.string.app_name);
        super.onStart();
    }
}