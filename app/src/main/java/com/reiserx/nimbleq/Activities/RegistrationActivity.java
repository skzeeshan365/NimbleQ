package com.reiserx.nimbleq.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.userDetails;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.ButtonDesign;
import com.reiserx.nimbleq.Utils.SnackbarTop;
import com.reiserx.nimbleq.Utils.dialogs;
import com.reiserx.nimbleq.ViewModels.AdministrationViewModel;
import com.reiserx.nimbleq.ViewModels.UserDataViewModel;
import com.reiserx.nimbleq.databinding.ActivityRegistrationBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity {

    ActivityRegistrationBinding binding;

    List<String> gradeList;
    ArrayList<String> stateList;
    ArrayList<String> cityList;

    ButtonDesign buttonDesign;

    String jsonString;

    FirebaseAuth auth;
    String gender;

    SnackbarTop snackbarTop;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        buttonDesign = new ButtonDesign(this);
        buttonDesign.setButtonOutline(binding.button5);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        setTitle(getString(R.string.registration));

        snackbarTop = new SnackbarTop(findViewById(android.R.id.content));

        dialogs dialogs = new dialogs(this, findViewById(android.R.id.content));
        if (user != null)
            dialogs.selectStudentOrTeacherNormal(user.getUid());

        initializeViews();

        binding.maleBtn.setOnCheckedChangeListener((compoundButton, b) -> {
            if (binding.maleBtn.isChecked())
                gender = "Male";
        });
        binding.femaleBtn.setOnCheckedChangeListener((compoundButton, b) -> {
            if (binding.femaleBtn.isChecked())
                gender = "Female";
        });

        binding.button5.setOnClickListener(view -> {
            if (binding.editTextTextPersonName2.getText().toString().trim().equals(""))
                binding.editTextTextPersonName2.setError(getString(R.string.enter_user_name));
            else if (binding.gradeSpinner.getSelectedItemPosition() == 0) {
                snackbarTop.showSnackBar(getString(R.string.select_grade), false);
            } else if (binding.schoolNameEdittext.getText().toString().trim().equals("")) {
                binding.schoolNameEdittext.setError(getString(R.string.enter_school_name));
            } else if (binding.statesSpinner.getSelectedItemPosition() == 0) {
                snackbarTop.showSnackBar(getString(R.string.select_state), false);
            } else if (binding.citiesSpinner.getSelectedItemPosition() == 0) {
                snackbarTop.showSnackBar(getString(R.string.select_city), false);
            } else if (gender == null) {
                snackbarTop.showSnackBar(getString(R.string.select_gender), false);
            } else {
                buttonDesign.buttonFill(binding.button5);
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                if (user != null) {
                    DocumentReference documentReference = firestore.collection("UserData").document(user.getUid());

                    userDetails userDetails = new userDetails(binding.gradeSpinner.getSelectedItem().toString(), binding.schoolNameEdittext.getText().toString(), binding.statesSpinner.getSelectedItem().toString(), binding.citiesSpinner.getSelectedItem().toString(), gender);

                    UserDataViewModel userDataViewModel = new ViewModelProvider(this).get(UserDataViewModel.class);

                    userDataViewModel.updateUsername(user.getUid(), binding.editTextTextPersonName2.getText().toString().trim());
                    userDataViewModel.getUpdateUsernameMutableLiveData().observe(this, unused -> documentReference.set(userDetails).addOnSuccessListener(unuseds -> {
                        if (CONSTANTS.student_teacher_flag == 1)
                            dialogs.selectSubjectForLearnerRegistration(user.getUid());
                        else if (CONSTANTS.student_teacher_flag == 2)
                            dialogs.selectSubjectForTeacherRegistration(user.getUid());
                    }).addOnFailureListener(e -> {
                        Log.d(CONSTANTS.TAG, e.toString());
                        snackbarTop.showSnackBar("Faled: " + e, false);
                    }));

                    userDataViewModel.getDatabaseErrorMutableLiveData().observe(this, s -> snackbarTop.showSnackBar("Faled: " + s, false));
                }
            }
        });
    }

    private class gradesClass implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class stateClass implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (i != 0) {
                getCities(jsonString, stateList.get(i));
                binding.citiesSpinner.setEnabled(true);
            } else {
                binding.citiesSpinner.setSelection(0);
                binding.citiesSpinner.setEnabled(false);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class citiesClass implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public String loadJSONFile() {
        String json = null;
        try {
            InputStream inputStream = getAssets().open("cities.json");
            int size = inputStream.available();
            byte[] byteArray = new byte[size];
            inputStream.read(byteArray);
            inputStream.close();
            json = new String(byteArray, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(CONSTANTS.TAG, e.toString());
            return null;
        }
        return json;
    }

    void getStates(String jsonString) {

        JSONObject jsonArray = null;
        try {
            jsonArray = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Iterator iterator = jsonArray.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            stateList.add(key);
        }
    }

    void getCities(String jsonString, String state) {
        try {
            cityList.clear();
            cityList.add("Select city");
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = null;
            jsonArray = jsonObject.getJSONArray(state);
            for (int i = 0; i < jsonArray.length(); i++) {
                cityList.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(CONSTANTS.TAG, e.toString());
        }
    }

    private void initializeViews() {
        stateList = new ArrayList<>();
        stateList.add("Select state");
        cityList = new ArrayList<>();
        cityList.add("Select city");

        binding.citiesSpinner.setEnabled(false);

        jsonString = loadJSONFile();

        AdministrationViewModel viewModel = new ViewModelProvider(this).get(AdministrationViewModel.class);

        viewModel.getGradeList();
        viewModel.getListStringMutableLiveData().observe(this, models -> {

            gradeList = models;
            ArrayAdapter<String> gradesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gradeList);
            gradesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.gradeSpinner.setAdapter(gradesAdapter);
            binding.gradeSpinner.setOnItemSelectedListener(new gradesClass());

        });

        getStates(jsonString);

        if (stateList != null) {
            ArrayAdapter<String> statesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stateList);
            statesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.statesSpinner.setAdapter(statesAdapter);
            binding.statesSpinner.setOnItemSelectedListener(new stateClass());
        }
        if (cityList != null) {
            ArrayAdapter<String> citiesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityList);
            citiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.citiesSpinner.setAdapter(citiesAdapter);
            binding.citiesSpinner.setOnItemSelectedListener(new citiesClass());
        }
    }
}