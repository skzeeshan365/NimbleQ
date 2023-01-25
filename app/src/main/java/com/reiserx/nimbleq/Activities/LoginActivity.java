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
    private boolean isMoved_phone;
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
            binding.singinTxt.setText(getString(R.string.sign_in_with_phone));
            isMoved_phone = true;
            process();
        });
    }

    @Override
    public void onBackPressed() {
        if (isMoved_phone) {
            binding.includePhoneSignin.getRoot().startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
            binding.includePhoneSignin.getRoot().setVisibility(View.GONE);

            binding.loginMainHolder.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            binding.loginMainHolder.setVisibility(View.VISIBLE);

            binding.singinTxt.setText(getString(R.string.sign_in_with));
            isMoved_phone = false;
        } else {
            finishAffinity();
        }
    }

    private void process() {
            binding.includePhoneSignin.button.setOnClickListener(v -> {
                buttonDesign.buttonFill(binding.includePhoneSignin.button);
                Intent intent = new Intent(LoginActivity.this, PhoneAuthActivity.class);
                intent.putExtra("PhoneNumber", "+91".concat(binding.includePhoneSignin.editTextPhone2.getText().toString()));
                startActivity(intent);
                finish();
            });
        }
}