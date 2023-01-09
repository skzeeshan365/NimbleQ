package com.reiserx.nimbleq.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.ButtonDesign;
import com.reiserx.nimbleq.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    private boolean isMoved_phone, isMoved_register;
    ButtonDesign buttonDesign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        buttonDesign = new ButtonDesign(this);

        binding.signinPhoneBtn.setOnClickListener(view -> {
            binding.loginMainHolder.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
            binding.loginMainHolder.setVisibility(View.GONE);

            buttonDesign.setButtonOutline(binding.includePhoneSignin.button);
            binding.includePhoneSignin.getRoot().setVisibility(View.VISIBLE);
            binding.includePhoneSignin.getRoot().startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            binding.singinTxt.setText("Sign in with phone");
            isMoved_phone = true;
            isMoved_register = false;
            process();
        });

        binding.createAccBtn.setOnClickListener(view -> {
            binding.loginMainHolder.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
            binding.loginMainHolder.setVisibility(View.GONE);

            buttonDesign.setButtonOutline(binding.includeRegisterLayout.button2);
            binding.includeRegisterLayout.getRoot().setVisibility(View.VISIBLE);
            binding.includeRegisterLayout.getRoot().startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            binding.singinTxt.setText("Registration");
            isMoved_register = true;
            isMoved_phone = false;
            process();
        });
    }

    @Override
    public void onBackPressed() {
        if (isMoved_register) {
            binding.includeRegisterLayout.getRoot().startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
            binding.includeRegisterLayout.getRoot().setVisibility(View.GONE);

            binding.loginMainHolder.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            binding.loginMainHolder.setVisibility(View.VISIBLE);

            binding.singinTxt.setText("Sign in with");
            isMoved_register = false;
        } else if (isMoved_phone) {
            binding.includePhoneSignin.getRoot().startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
            binding.includePhoneSignin.getRoot().setVisibility(View.GONE);

            binding.loginMainHolder.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            binding.loginMainHolder.setVisibility(View.VISIBLE);

            binding.singinTxt.setText("Sign in with");
            isMoved_phone = false;
        } else {
            finishAffinity();
        }
    }

    private void process() {
        if (isMoved_register) {
            binding.includeRegisterLayout.button2.setOnClickListener(v -> {
                buttonDesign.buttonFill(binding.includeRegisterLayout.button2);
                Intent intent = new Intent(LoginActivity.this, PhoneAuthActivity.class);
                intent.putExtra("PhoneNumber", "+91".concat(binding.includeRegisterLayout.editTextPhone.getText().toString()));
                intent.putExtra("Name", binding.includeRegisterLayout.editTextTextPersonName.getText().toString());
                intent.putExtra("isRegister", true);
                startActivity(intent);
            });
        } else if (isMoved_phone) {
            binding.includePhoneSignin.button.setOnClickListener(v -> {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                buttonDesign.buttonFill(binding.includePhoneSignin.button);
                Intent intent = new Intent(LoginActivity.this, PhoneAuthActivity.class);
                intent.putExtra("PhoneNumber", "+91".concat(binding.includePhoneSignin.editTextPhone2.getText().toString()));
                intent.putExtra("isRegister", false);
                startActivity(intent);
            });
        }
    }
}