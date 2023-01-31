package com.reiserx.nimbleq.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.reiserx.nimbleq.Activities.Administration.AdministrationActivity;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.ButtonDesign;
import com.reiserx.nimbleq.Utils.SnackbarTop;
import com.reiserx.nimbleq.Utils.dialogs;
import com.reiserx.nimbleq.ViewModels.AdministrationViewModel;
import com.reiserx.nimbleq.ViewModels.UserDataViewModel;
import com.reiserx.nimbleq.databinding.ActivityPhoneAuthBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {

    ActivityPhoneAuthBinding binding;

    FirebaseAuth auth;

    ProgressDialog dialog;

    private String verifyID;
    String phone;

    ButtonDesign buttonDesign;

    DatabaseReference reference;

    SnackbarTop snackbarTop;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        buttonDesign = new ButtonDesign(this);
        snackbarTop = new SnackbarTop(findViewById(android.R.id.content));

        buttonDesign.setButtonOutline(binding.continueBtn);


        phone = getIntent().getStringExtra("PhoneNumber");

        auth = FirebaseAuth.getInstance();

        if (Objects.requireNonNull(getSupportActionBar()).isShowing()) {
            getSupportActionBar().hide();
        }

        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.sending_otp));
        dialog.setCancelable(false);
        dialog.show();

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(PhoneAuthActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        dialog.dismiss();
                        Toast.makeText(PhoneAuthActivity.this, "failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verifyId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyId, forceResendingToken);
                        dialog.dismiss();
                        verifyID = verifyId;
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        binding.pinview.setOnPinCompletionListener(entirePin -> {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifyID, entirePin);
            auth.signInWithCredential(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (Objects.requireNonNull(task.getResult().getAdditionalUserInfo()).isNewUser()) {
                        snackbarTop.showSnackBar(getString(R.string.account_created), true);
                        process();
                    } else {
                        snackbarTop.showSnackBar(getString(R.string.login_successful), true);
                        FirebaseUser user = auth.getCurrentUser();
                            UserDataViewModel userDataViewModel = new ViewModelProvider(PhoneAuthActivity.this).get(UserDataViewModel.class);
                            if (user != null) {
                                userDataViewModel.updateFCMToken(user.getUid());
                                binding.continueBtn.setOnClickListener(view -> {
                                    Map<String, Object> map = new HashMap<>();
                                    if (user.getMetadata() != null)
                                        map.put("lastLogin_timestamp", user.getMetadata().getLastSignInTimestamp());
                                    FirebaseDatabase.getInstance().getReference().child("Data").child("UserData").child(user.getUid()).updateChildren(map);
                                    buttonDesign.buttonFill(binding.continueBtn);
                                    dialogs dialogs = new dialogs(PhoneAuthActivity.this, findViewById(android.R.id.content));
                                    dialogs.selectStudentOrTeacherForLogin(auth.getUid());
                                });
                            }
                    }
                    TransitionManager.beginDelayedTransition(binding.cardHolder, new AutoTransition());
                    binding.continueBtn.setVisibility(View.VISIBLE);
                } else {
                    snackbarTop.showSnackBar("Failed ".concat(task.getException().toString()), false);
                    Toast.makeText(PhoneAuthActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    TransitionManager.beginDelayedTransition(binding.cardHolder, new AutoTransition());
                    binding.continueBtn.setVisibility(View.GONE);
                }
            });
        });
    }

    private void process() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseMessaging fcm = FirebaseMessaging.getInstance();

        fcm.getToken().addOnSuccessListener(s -> {
            if (auth.getUid() != null) {
                reference = database.getReference().child("Data").child("UserData").child(auth.getUid());
                FirebaseUser user = auth.getCurrentUser();
                if (user != null && user.getMetadata() != null) {
                    UserData userData = new UserData(user.getUid(), user.getPhoneNumber(), "null", s, user.getMetadata().getCreationTimestamp(), user.getMetadata().getLastSignInTimestamp());
                    reference.setValue(userData).addOnSuccessListener(unused -> binding.continueBtn.setOnClickListener(view -> {
                        Intent intent = new Intent(PhoneAuthActivity.this, RegistrationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }));
                }
            }
        });
    }
}