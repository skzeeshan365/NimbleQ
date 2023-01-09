package com.reiserx.nimbleq.Activities;

import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.hsalf.smileyrating.SmileyRating;
import com.reiserx.nimbleq.Utils.ButtonDesign;
import com.reiserx.nimbleq.ViewModels.UserDataViewModel;
import com.reiserx.nimbleq.databinding.ActivityRateAndFeedbackBinding;

public class RateAndFeedbackActivity extends AppCompatActivity {

    ActivityRateAndFeedbackBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRateAndFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle("Rate and feedback");

        ButtonDesign buttonDesign = new ButtonDesign(this);
        buttonDesign.setButtonOutline(binding.button3);

        binding.smileRating.setSmileySelectedListener(type -> {
            if (SmileyRating.Type.OKAY == type || SmileyRating.Type.BAD == type || SmileyRating.Type.TERRIBLE == type) {
                TransitionManager.beginDelayedTransition(binding.baseCardview, new AutoTransition());
                binding.hiddenView.setVisibility(View.VISIBLE);
            } else {
                TransitionManager.beginDelayedTransition(binding.baseCardview, new AutoTransition());
                binding.hiddenView.setVisibility(View.GONE);
            }
            int rating = type.getRating();
        });

        binding.button3.setOnClickListener(view -> {
            buttonDesign.buttonFill(binding.button3);
        });

        int state = getIntent().getExtras().getInt("id");
        String userID = getIntent().getExtras().getString("userID");

        UserDataViewModel userDataViewModel = new ViewModelProvider(this).get(UserDataViewModel.class);

        userDataViewModel.getUsername(userID);
        userDataViewModel.getUserName().observe(this, s -> {
            binding.usernameTxtRate.setText("hey, ".concat(s));
        });
        if (state == 1) {
            String message = getIntent().getExtras().getString("Message");
            String classID = getIntent().getExtras().getString("classID");
            binding.msgTxtRate.setText(message);
        } else if (state == 2) {
            String message = getIntent().getExtras().getString("Message");
            String teacherID = getIntent().getExtras().getString("teacherID");
            userDataViewModel.getUsername(teacherID);
            userDataViewModel.getUserName().observe(this, s -> {
                binding.msgTxtRate.setText(message.concat(" ".concat(s)));
            });
        }
    }
}