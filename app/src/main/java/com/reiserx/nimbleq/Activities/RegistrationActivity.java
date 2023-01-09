package com.reiserx.nimbleq.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.userDetails;
import com.reiserx.nimbleq.Utils.ButtonDesign;
import com.reiserx.nimbleq.Utils.SnackbarTop;
import com.reiserx.nimbleq.Utils.dialogs;
import com.reiserx.nimbleq.databinding.ActivityRegistrationBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

public class RegistrationActivity extends AppCompatActivity {

    ActivityRegistrationBinding binding;

    String[] gradeList;
    ArrayList<String> stateList;
    ArrayList<String> cityList;

    ButtonDesign buttonDesign;

    String jsonString;

    FirebaseAuth auth;
    String gender;

    SnackbarTop snackbarTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        buttonDesign = new ButtonDesign(this);
        buttonDesign.setButtonOutline(binding.button5);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        setTitle("Registration");

        snackbarTop = new SnackbarTop(findViewById(android.R.id.content));

        dialogs dialogs = new dialogs(this, findViewById(android.R.id.content));
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
            if (binding.gradeSpinner.getSelectedItemPosition() == 0) {
                snackbarTop.showSnackBar("Please select your grade", false);
            } else if (binding.schoolNameEdittext.getText().toString().trim().equals("")) {
                binding.schoolNameEdittext.setError("Please enter your school");
            } else if (binding.statesSpinner.getSelectedItemPosition() == 0) {
                snackbarTop.showSnackBar("Please select your state", false);
            } else if (binding.citiesSpinner.getSelectedItemPosition() == 0) {
                snackbarTop.showSnackBar("Please select your city", false);
            } else if (gender == null) {
                snackbarTop.showSnackBar("Please select your gender", false);
            } else {
                buttonDesign.buttonFill(binding.button5);
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                DocumentReference documentReference = firestore.collection("UserData").document(user.getUid());

                userDetails userDetails = new userDetails(binding.gradeSpinner.getSelectedItem().toString(), binding.schoolNameEdittext.getText().toString(), binding.statesSpinner.getSelectedItem().toString(), binding.citiesSpinner.getSelectedItem().toString(), gender);
                documentReference.set(userDetails).addOnSuccessListener(unused -> {
                    if (CONSTANTS.student_teacher_flag == 1)
                        dialogs.selectSubjectForLearnerRegistration(user.getUid());
                    else if (CONSTANTS.student_teacher_flag == 2)
                        dialogs.selectSubjectForTeacherRegistration(user.getUid());
                }).addOnFailureListener(e -> {
                    Log.d(CONSTANTS.TAG, e.toString());
                    snackbarTop.showSnackBar("Faled: " + e, false);
                });
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

        gradeList = new String[]{"Select grade", "Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5",
                "Grade 6", "Grade 7", "Grade 8", "Grade 9", "Grade 10"};

        getStates(jsonString);

        ArrayAdapter<String> gradesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gradeList);
        gradesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.gradeSpinner.setAdapter(gradesAdapter);
        binding.gradeSpinner.setOnItemSelectedListener(new gradesClass());

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