package com.reiserx.nimbleq.Activities.Feedbacks;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.hsalf.smileyrating.SmileyRating;
import com.reiserx.nimbleq.Models.RatingModel;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.ButtonDesign;
import com.reiserx.nimbleq.ViewModels.UserDataViewModel;
import com.reiserx.nimbleq.ViewModels.classViewModel;
import com.reiserx.nimbleq.databinding.ActivityRateAndFeedbackBinding;

import java.util.Calendar;

public class RateAndFeedbackActivity extends AppCompatActivity {

    ActivityRateAndFeedbackBinding binding;

    String classID;
    String teacherID, token, className;
    String userID;
    UserData userData;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRateAndFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle(getString(R.string.rate_and_feedback));

        ButtonDesign buttonDesign = new ButtonDesign(this);
        buttonDesign.setButtonOutline(binding.button3);

        int state = getIntent().getExtras().getInt("id");
        userID = getIntent().getExtras().getString("userID");
        token = getIntent().getExtras().getString("token");

        binding.smileRating.setSmileySelectedListener(type -> {
            if (SmileyRating.Type.OKAY == type || SmileyRating.Type.BAD == type || SmileyRating.Type.TERRIBLE == type) {
                TransitionManager.beginDelayedTransition(binding.baseCardview, new AutoTransition());
                binding.hiddenView.setVisibility(View.VISIBLE);
            } else {
                TransitionManager.beginDelayedTransition(binding.baseCardview, new AutoTransition());
                binding.hiddenView.setVisibility(View.GONE);
            }
            int rating = type.getRating();

            binding.button3.setOnClickListener(view -> {
                Calendar cal = Calendar.getInstance();
                long currentTime = cal.getTimeInMillis();
                buttonDesign.buttonFill(binding.button3);
                if (binding.editTextTextPersonName3.getText().toString().trim().equals("")) {
                    RatingModel ratingModel = new RatingModel(rating, userID, currentTime);
                    updateRating(state, ratingModel);
                } else {
                    RatingModel ratingModel = new RatingModel(rating, userID, binding.editTextTextPersonName3.getText().toString().trim(), currentTime);
                    updateRating(state, ratingModel);
                }
            });
        });

        UserDataViewModel userDataViewModel = new ViewModelProvider(this).get(UserDataViewModel.class);

        userDataViewModel.getUserData(userID);
        userDataViewModel.getUserData().observe(this, userData -> {
            binding.usernameTxtRate.setText(getString(R.string.hey1) + " ".concat(userData.getUserName()));
            this.userData = userData;
        });
        if (state == 1) {
            String message = getIntent().getExtras().getString("Message");
            classID = getIntent().getExtras().getString("classID");
            className = getIntent().getExtras().getString("classname");
            binding.msgTxtRate.setText(message);
        } else if (state == 2) {
            String message = getIntent().getExtras().getString("Message");
            teacherID = getIntent().getExtras().getString("teacherID");
            userDataViewModel.getUsername(teacherID);
            userDataViewModel.getUserName().observe(this, s -> binding.msgTxtRate.setText(message.concat(" ".concat(s))));
        }
    }

    void updateRating(int state, RatingModel ratingModel) {
        classViewModel classViewModel = new ViewModelProvider(this).get(com.reiserx.nimbleq.ViewModels.classViewModel.class);
        if (state == 1) {
            classViewModel.setClassRating(classID, className, userData, ratingModel, token, this);
        } else if (state == 2) {
            classViewModel.setTeacherRating(teacherID, userData, ratingModel, token, this);
        }
        classViewModel.getRatingSubmittedMutableLiveData().observe(this, unused -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(getString(R.string.feedback_submitted));
            alert.setMessage(getString(R.string.feedback_submitted_msg));
            alert.setPositiveButton(getString(R.string.close), (dialogInterface, i) -> finish());
            alert.show();
        });
    }
}