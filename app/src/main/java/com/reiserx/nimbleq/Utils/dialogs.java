package com.reiserx.nimbleq.Utils;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.reiserx.nimbleq.Activities.ClassActivity;
import com.reiserx.nimbleq.Activities.MainActivity;
import com.reiserx.nimbleq.Adapters.TeacherListSpinnerAdapter;
import com.reiserx.nimbleq.BuildConfig;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.ClassRequestModel;
import com.reiserx.nimbleq.Models.RatingModel;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.Models.subjectAndTimeSlot;
import com.reiserx.nimbleq.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class dialogs {
    private final Context context;
    private List<String> subjectList, timeSlotsList;
    SnackbarTop snackbarTop;
    DatabaseReference reference;
    subjectAndTimeSlot models;
    ArrayList<String> teacherList;
    ArrayList<UserData> userData;
    UserData requestTeacherData;
    List<subjectAndTimeSlot> list;
    float rating1, rating2, rating3, rating4, rating5;
    SharedPreferenceClass sharedPreferenceClass;

    public dialogs(Context context, View view) {
        this.context = context;
        snackbarTop = new SnackbarTop(view);
        sharedPreferenceClass = new SharedPreferenceClass(context);
    }

    public void selectStudentOrTeacherForLogin(String uid) {
        AlertDialog alert = new AlertDialog.Builder(context).create();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.student_teacher_selection_layout, null);
        final Button student_btn = mView.findViewById(R.id.student_btn);
        final Button teacher_btn = mView.findViewById(R.id.teacher_btn);

        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ButtonDesign buttonDesign = new ButtonDesign(context);
        buttonDesign.setButtonOutline(student_btn);
        buttonDesign.setButtonOutline(teacher_btn);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Data").child("Main").child("SubjectList");
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        list = new ArrayList<>();

        student_btn.setOnClickListener(view -> {
            CONSTANTS.student_teacher_flag = 1;
            buttonDesign.buttonFill(student_btn);
            buttonDesign.setButtonOutline(teacher_btn);
            snackbarTop.showSnackBar(context.getString(R.string.logged_in_as_learner), true);

            FirebaseMessaging fcm = FirebaseMessaging.getInstance();

            SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(context);

            databaseReference.child("subjectForStudents").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.getChildrenCount() == 3) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                subjectAndTimeSlot subjectAndTimeSlot = snapshot1.getValue(com.reiserx.nimbleq.Models.subjectAndTimeSlot.class);
                                if (subjectAndTimeSlot != null) {
                                    subjectAndTimeSlot.setKey(snapshot1.getKey());
                                    list.add(subjectAndTimeSlot);
                                    fcm.subscribeToTopic(TopicSubscription.getTopicForSlot(subjectAndTimeSlot));
                                }
                            }
                            if (!checkSlots()) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("current", true);
                                subjectAndTimeSlot model = list.get(0);
                                databaseReference.child("subjectForStudents").child(uid).child(model.getKey()).updateChildren(map);
                            }
                            context.startActivity(intent);
                        } else if (snapshot.getChildrenCount() < sharedPreferenceClass.getSlotLimit()) {
                            selectSubjectForLearnerNormal(uid, true);
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                subjectAndTimeSlot subjectAndTimeSlot = snapshot1.getValue(com.reiserx.nimbleq.Models.subjectAndTimeSlot.class);
                                if (subjectAndTimeSlot != null)
                                    fcm.subscribeToTopic(TopicSubscription.getTopicForSlot(subjectAndTimeSlot));
                            }
                        }
                    } else
                        selectSubjectForLearnerNormal(uid, true);
                    alert.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            updateUserType(uid, CONSTANTS.student_teacher_flag);
        });

        teacher_btn.setOnClickListener(view -> {
            CONSTANTS.student_teacher_flag = 2;
            buttonDesign.buttonFill(teacher_btn);
            buttonDesign.setButtonOutline(student_btn);
            snackbarTop.showSnackBar(context.getString(R.string.logged_in_as_teacher), true);

            databaseReference.child("subjectForTeacher").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.getChildrenCount() == 3) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                subjectAndTimeSlot subjectAndTimeSlot = snapshot1.getValue(com.reiserx.nimbleq.Models.subjectAndTimeSlot.class);
                                if (subjectAndTimeSlot != null) {
                                    subjectAndTimeSlot.setKey(snapshot1.getKey());
                                    list.add(subjectAndTimeSlot);
                                }
                            }
                            if (!checkSlots()) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("current", true);
                                subjectAndTimeSlot model = list.get(0);
                                databaseReference.child("subjectForTeacher").child(uid).child(model.getKey()).updateChildren(map);
                            }
                            context.startActivity(intent);
                        } else if (snapshot.getChildrenCount() < sharedPreferenceClass.getSlotLimit()) {
                            selectSubjectForTeacherNormal(uid, true);
                        }
                    } else
                        selectSubjectForTeacherNormal(uid, true);
                    alert.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            updateUserType(uid, CONSTANTS.student_teacher_flag);
        });
        alert.setView(mView);
        alert.setCancelable(false);
        alert.show();
    }

    boolean checkSlots() {
        for (subjectAndTimeSlot subjectAndTimeSlot : list) {
            if (subjectAndTimeSlot.isCurrent())
                return true;
        }
        return false;
    }

    public void selectStudentOrTeacherNormal(String uid) {
        AlertDialog alert = new AlertDialog.Builder(context).create();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.student_teacher_selection_layout, null);
        final Button student_btn = mView.findViewById(R.id.student_btn);
        final Button teacher_btn = mView.findViewById(R.id.teacher_btn);

        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ButtonDesign buttonDesign = new ButtonDesign(context);
        buttonDesign.setButtonOutline(student_btn);
        buttonDesign.setButtonOutline(teacher_btn);

        student_btn.setOnClickListener(view -> {
            CONSTANTS.student_teacher_flag = 1;
            buttonDesign.buttonFill(student_btn);
            buttonDesign.setButtonOutline(teacher_btn);
            updateUserType(uid, CONSTANTS.student_teacher_flag);
            snackbarTop.showSnackBar(context.getString(R.string.logged_in_as_learner), true);
            alert.dismiss();
        });

        teacher_btn.setOnClickListener(view -> {
            CONSTANTS.student_teacher_flag = 2;
            buttonDesign.buttonFill(teacher_btn);
            buttonDesign.setButtonOutline(student_btn);
            updateUserType(uid, CONSTANTS.student_teacher_flag);
            snackbarTop.showSnackBar(context.getString(R.string.logged_in_as_teacher), true);
            alert.dismiss();
        });
        alert.setView(mView);
        alert.setCancelable(false);
        alert.show();
    }

    public void selectSubjectForLearnerNormal(String uid, boolean login) {

        AlertDialog alert = new AlertDialog.Builder(context).create();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.subjects_layout, null);

        final Button next_btn = mView.findViewById(R.id.next_btn);
        final EditText topic_edittext = mView.findViewById(R.id.topic_edittext);
        final Spinner time_spinner = mView.findViewById(R.id.time_spinner);
        final Spinner subject_spinner = mView.findViewById(R.id.subject_spinner);

        final TextView sub_txt = mView.findViewById(R.id.sub_txt);
        sub_txt.setText(context.getString(R.string.select_slot_hint));

        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ButtonDesign buttonDesign = new ButtonDesign(context);
        buttonDesign.setButtonOutline(next_btn);

        subjectList = new ArrayList<>();
        subjectList.add("Select subject");
        FirebaseDatabase.getInstance().getReference().child("Data").child("Administration").child("Lists").child("SubjectList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String value = snapshot1.getValue(String.class);
                        if (value != null)
                            subjectList.add(value);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                snackbarTop.showSnackBar(error.toString(), false);
            }
        });

        timeSlotsList = new ArrayList<>();
        timeSlotsList.add("Select time slot");
        FirebaseDatabase.getInstance().getReference().child("Data").child("Administration").child("Lists").child("SlotList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String value = snapshot1.getValue(String.class);
                        if (value != null)
                            timeSlotsList.add(value);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                snackbarTop.showSnackBar(error.toString(), false);
            }
        });

        ArrayAdapter<String> subjectsAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, subjectList);
        subjectsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subject_spinner.setAdapter(subjectsAdapter);
        subject_spinner.setOnItemSelectedListener(new subjectList());

        ArrayAdapter<String> timeSlotsAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, timeSlotsList);
        timeSlotsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        time_spinner.setAdapter(timeSlotsAdapter);
        time_spinner.setOnItemSelectedListener(new timeSlotList());

        next_btn.setOnClickListener(view -> {
            CONSTANTS.student_teacher_flag = 2;
            buttonDesign.buttonFill(next_btn);
            if (subject_spinner.getSelectedItemPosition() == 0) {
                Toast.makeText(context, "Please select your subject", Toast.LENGTH_SHORT).show();
            } else if (topic_edittext.getText().toString().trim().equals("")) {
                topic_edittext.setError("Please enter your topic");
            } else if (time_spinner.getSelectedItemPosition() == 0) {
                Toast.makeText(context, "Please select your time slot", Toast.LENGTH_SHORT).show();
            } else {
                subjectAndTimeSlot subjectAndTimeSlot = new subjectAndTimeSlot(subject_spinner.getSelectedItem().toString(), topic_edittext.getText().toString().trim(), time_spinner.getSelectedItem().toString(), true);
                updateSubjectForStudents(subjectAndTimeSlot, uid, 1);
                alert.dismiss();
            }
            if (login) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        });
        alert.setView(mView);
        if (login)
            alert.setCancelable(false);
        alert.show();
    }

    public void selectSubjectForLearnerRegistration(String uid) {

        AlertDialog alert = new AlertDialog.Builder(context).create();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.subjects_layout, null);

        final Button next_btn = mView.findViewById(R.id.next_btn);
        final EditText topic_edittext = mView.findViewById(R.id.topic_edittext);
        final Spinner time_spinner = mView.findViewById(R.id.time_spinner);
        final Spinner subject_spinner = mView.findViewById(R.id.subject_spinner);

        final TextView sub_txt = mView.findViewById(R.id.sub_txt);
        sub_txt.setText(context.getString(R.string.select_slot_hint));

        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ButtonDesign buttonDesign = new ButtonDesign(context);
        buttonDesign.setButtonOutline(next_btn);

        subjectList = new ArrayList<>();
        subjectList.add("Select subject");
        FirebaseDatabase.getInstance().getReference().child("Data").child("Administration").child("Lists").child("SubjectList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String value = snapshot1.getValue(String.class);
                        if (value != null)
                            subjectList.add(value);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                snackbarTop.showSnackBar(error.toString(), false);
            }
        });

        timeSlotsList = new ArrayList<>();
        timeSlotsList.add("Select time slot");
        FirebaseDatabase.getInstance().getReference().child("Data").child("Administration").child("Lists").child("SlotList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String value = snapshot1.getValue(String.class);
                        if (value != null)
                            timeSlotsList.add(value);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                snackbarTop.showSnackBar(error.toString(), false);
            }
        });

        ArrayAdapter<String> subjectsAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, subjectList);
        subjectsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subject_spinner.setAdapter(subjectsAdapter);
        subject_spinner.setOnItemSelectedListener(new subjectList());

        ArrayAdapter<String> timeSlotsAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, timeSlotsList);
        timeSlotsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        time_spinner.setAdapter(timeSlotsAdapter);
        time_spinner.setOnItemSelectedListener(new timeSlotList());

        next_btn.setOnClickListener(view -> {
            CONSTANTS.student_teacher_flag = 2;
            buttonDesign.buttonFill(next_btn);
            if (subject_spinner.getSelectedItemPosition() == 0) {
                Toast.makeText(context, "Please select your subject", Toast.LENGTH_SHORT).show();
            } else if (topic_edittext.getText().toString().trim().equals("")) {
                topic_edittext.setError("Please enter your topic");
            } else if (time_spinner.getSelectedItemPosition() == 0) {
                Toast.makeText(context, "Please select your time slot", Toast.LENGTH_SHORT).show();
            } else {
                subjectAndTimeSlot subjectAndTimeSlot = new subjectAndTimeSlot(subject_spinner.getSelectedItem().toString(), topic_edittext.getText().toString().trim(), time_spinner.getSelectedItem().toString(), true);
                updateSubjectForStudents(subjectAndTimeSlot, uid, 1);
                alert.dismiss();
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        });
        alert.setView(mView);
        alert.setCancelable(false);
        alert.show();
    }

    public void selectSubjectForTeacherNormal(String uid, boolean login) {

        AlertDialog alert = new AlertDialog.Builder(context).create();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.subjects_layout, null);

        final Button next_btn = mView.findViewById(R.id.next_btn);
        final EditText topic_edittext = mView.findViewById(R.id.topic_edittext);
        final Spinner time_spinner = mView.findViewById(R.id.time_spinner);
        final Spinner subject_spinner = mView.findViewById(R.id.subject_spinner);

        time_spinner.setEnabled(false);
        time_spinner.setVisibility(View.GONE);

        final TextView sub_txt = mView.findViewById(R.id.sub_txt);
        sub_txt.setText(context.getString(R.string.select_sub_and_topic));

        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ButtonDesign buttonDesign = new ButtonDesign(context);
        buttonDesign.setButtonOutline(next_btn);

        subjectList = new ArrayList<>();
        subjectList.add(context.getString(R.string.select_subject));
        FirebaseDatabase.getInstance().getReference().child("Data").child("Administration").child("Lists").child("SubjectList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String value = snapshot1.getValue(String.class);
                        if (value != null)
                            subjectList.add(value);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                snackbarTop.showSnackBar(error.toString(), false);
            }
        });

        ArrayAdapter<String> subjectsAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, subjectList);
        subjectsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subject_spinner.setAdapter(subjectsAdapter);
        subject_spinner.setOnItemSelectedListener(new subjectList());

        next_btn.setOnClickListener(view -> {
            CONSTANTS.student_teacher_flag = 2;
            buttonDesign.buttonFill(next_btn);
            if (subject_spinner.getSelectedItemPosition() == 0) {
                Toast.makeText(context, context.getString(R.string.select_your_subject), Toast.LENGTH_SHORT).show();
            } else if (topic_edittext.getText().toString().trim().equals("")) {
                topic_edittext.setError(context.getString(R.string.please_enter_topic));
            } else {
                subjectAndTimeSlot subjectAndTimeSlot = new subjectAndTimeSlot(subject_spinner.getSelectedItem().toString(), topic_edittext.getText().toString().trim());
                updateSubjectForStudents(subjectAndTimeSlot, uid, 2);
                alert.dismiss();
            }
            if (login) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        });
        alert.setView(mView);
        if (login)
            alert.setCancelable(false);
        alert.show();
    }

    public void selectSubjectForTeacherRegistration(String uid) {

        AlertDialog alert = new AlertDialog.Builder(context).create();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.subjects_layout, null);

        final Button next_btn = mView.findViewById(R.id.next_btn);
        final EditText topic_edittext = mView.findViewById(R.id.topic_edittext);
        final Spinner time_spinner = mView.findViewById(R.id.time_spinner);
        final Spinner subject_spinner = mView.findViewById(R.id.subject_spinner);

        time_spinner.setEnabled(false);
        time_spinner.setVisibility(View.GONE);

        final TextView sub_txt = mView.findViewById(R.id.sub_txt);
        sub_txt.setText(context.getString(R.string.select_sub_and_topic));

        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ButtonDesign buttonDesign = new ButtonDesign(context);
        buttonDesign.setButtonOutline(next_btn);

        subjectList = new ArrayList<>();
        subjectList.add("Select subject");
        FirebaseDatabase.getInstance().getReference().child("Data").child("Administration").child("Lists").child("SubjectList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String value = snapshot1.getValue(String.class);
                        if (value != null)
                            subjectList.add(value);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                snackbarTop.showSnackBar(error.toString(), false);
            }
        });

        ArrayAdapter<String> subjectsAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, subjectList);
        subjectsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subject_spinner.setAdapter(subjectsAdapter);
        subject_spinner.setOnItemSelectedListener(new subjectList());

        next_btn.setOnClickListener(view -> {
            CONSTANTS.student_teacher_flag = 2;
            buttonDesign.buttonFill(next_btn);
            if (subject_spinner.getSelectedItemPosition() == 0) {
                Toast.makeText(context, "Please select your subject", Toast.LENGTH_SHORT).show();
            } else if (topic_edittext.getText().toString().trim().equals("")) {
                topic_edittext.setError("Please enter your topic");
            } else {
                subjectAndTimeSlot subjectAndTimeSlot = new subjectAndTimeSlot(subject_spinner.getSelectedItem().toString(), topic_edittext.getText().toString().trim());
                updateSubjectForStudents(subjectAndTimeSlot, uid, 2);
                alert.dismiss();
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        });
        alert.setView(mView);
        alert.setCancelable(false);
        alert.show();
    }

    private class timeSlotList implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class subjectList implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private void updateUserType(String uid, int userTypes) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        SharedPreferences save = context.getSharedPreferences("Utils", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = save.edit();

        myEdit.putInt("userType", userTypes);
        myEdit.apply();

        if (userTypes == 1)
            database.getReference().child("Data").child("Main").child("UserType").child(uid).child("learner").setValue(true);
        else if (userTypes == 2)
            database.getReference().child("Data").child("Main").child("UserType").child(uid).child("teacher").setValue(true);
        database.getReference().child("Data").child("Main").child("UserType").child(uid).child("currentStatus").setValue(userTypes);
    }

    private void updateSubjectForStudents(subjectAndTimeSlot subjectAndTimeSlot, String uid, int userType) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        subjectAndTimeSlot.setCurrent(true);
        if (userType == 1)
            reference = database.getReference().child("Data").child("Main").child("SubjectList").child("subjectForStudents").child(uid);
        else if (userType == 2) {
            reference = database.getReference().child("Data").child("Main").child("SubjectList").child("subjectForTeacher").child(uid);
            SharedPreferences save = context.getSharedPreferences("subjectSlots", MODE_PRIVATE);
            SharedPreferences.Editor myEdit = save.edit();

            myEdit.putString("subject", subjectAndTimeSlot.getSubject());
            myEdit.putString("topic", subjectAndTimeSlot.getTopic());
            myEdit.apply();
        }
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.getChildrenCount() < sharedPreferenceClass.getSlotLimit()) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("current", false);
                            if (snapshot1.getKey() != null)
                                reference.child(snapshot1.getKey()).updateChildren(map);
                        }
                        FirebaseMessaging fcm = FirebaseMessaging.getInstance();
                        fcm.subscribeToTopic(TopicSubscription.getTopicForSlot(subjectAndTimeSlot)).addOnFailureListener(e -> snackbarTop.showSnackBar(context.getString(R.string.failed_to_subscribe_for_notification).concat(e.toString()), false));
                        reference.push().setValue(subjectAndTimeSlot);
                        snackbarTop.showSnackBar(context.getString(R.string.slot_saved), true);
                    } else {
                        snackbarTop.showSnackBar(context.getString(R.string.your_slots_are_full), false);
                    }
                } else {
                    reference.push().setValue(subjectAndTimeSlot);
                    snackbarTop.showSnackBar(context.getString(R.string.slot_saved), true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void meetingCreated(Activity activity, String classID) {

        AlertDialog alert = new AlertDialog.Builder(context).create();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.success_dialog_layout, null);

        final Button close_btn = mView.findViewById(R.id.swuccess_close_btn);
        final Button open_class = mView.findViewById(R.id.open_class_btn);

        final TextView success_txt = mView.findViewById(R.id.success_txt);
        success_txt.setText(context.getString(R.string.class_created_with_id).concat(classID));

        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ButtonDesign buttonDesign = new ButtonDesign(context);
        buttonDesign.setButtonOutline(close_btn);
        buttonDesign.setButtonOutline(open_class);

        close_btn.setOnClickListener(view -> activity.finish());

        open_class.setOnClickListener(view -> {
            Intent intent = new Intent(context, ClassActivity.class);
            intent.putExtra("classID", classID);
            context.startActivity(intent);
        });
        alert.setView(mView);
        alert.setCancelable(false);
        alert.show();
    }

    public void updateSubjectForLearner(subjectAndTimeSlot model, DatabaseReference reference) {

        AlertDialog alert = new AlertDialog.Builder(context).create();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.subjects_layout, null);

        final Button next_btn = mView.findViewById(R.id.next_btn);
        final EditText topic_edittext = mView.findViewById(R.id.topic_edittext);
        final Spinner time_spinner = mView.findViewById(R.id.time_spinner);
        final Spinner subject_spinner = mView.findViewById(R.id.subject_spinner);

        final TextView sub_txt = mView.findViewById(R.id.sub_txt);
        sub_txt.setText(context.getString(R.string.select_slot_hint));

        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ButtonDesign buttonDesign = new ButtonDesign(context);
        buttonDesign.setButtonOutline(next_btn);

        subjectList = new ArrayList<>();
        subjectList.add(context.getString(R.string.select_subject));
        FirebaseDatabase.getInstance().getReference().child("Data").child("Administration").child("Lists").child("SubjectList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String value = snapshot1.getValue(String.class);
                        if (value != null)
                            subjectList.add(value);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                snackbarTop.showSnackBar(error.toString(), false);
            }
        });

        timeSlotsList = new ArrayList<>();
        timeSlotsList.add(context.getString(R.string.select_time_slot));
        FirebaseDatabase.getInstance().getReference().child("Data").child("Administration").child("Lists").child("SlotList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String value = snapshot1.getValue(String.class);
                        if (value != null)
                            timeSlotsList.add(value);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                snackbarTop.showSnackBar(error.toString(), false);
            }
        });

        ArrayAdapter<String> subjectsAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, subjectList);
        subjectsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subject_spinner.setAdapter(subjectsAdapter);
        subject_spinner.setOnItemSelectedListener(new subjectList());

        ArrayAdapter<String> timeSlotsAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, timeSlotsList);
        timeSlotsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        time_spinner.setAdapter(timeSlotsAdapter);
        time_spinner.setOnItemSelectedListener(new timeSlotList());

        topic_edittext.setText(model.getTopic());
        subject_spinner.setSelection(getIndex(subject_spinner, model.getSubject()));
        time_spinner.setSelection(getIndex(time_spinner, model.getTimeSlot()));

        next_btn.setOnClickListener(view -> {
            CONSTANTS.student_teacher_flag = 2;
            buttonDesign.buttonFill(next_btn);
            if (subject_spinner.getSelectedItemPosition() == 0) {
                Toast.makeText(context, context.getString(R.string.select_your_subject), Toast.LENGTH_SHORT).show();
            } else if (topic_edittext.getText().toString().trim().equals("")) {
                topic_edittext.setError(context.getString(R.string.please_enter_topic));
            } else if (time_spinner.getSelectedItemPosition() == 0) {
                Toast.makeText(context, context.getString(R.string.please_select_time_slot), Toast.LENGTH_SHORT).show();
            } else {
                FirebaseMessaging fcm = FirebaseMessaging.getInstance();
                fcm.unsubscribeFromTopic(TopicSubscription.getTopicForSlot(model));
                if (model.isCurrent())
                    models = new subjectAndTimeSlot(subject_spinner.getSelectedItem().toString(), topic_edittext.getText().toString().trim(), time_spinner.getSelectedItem().toString(), true);
                else if (!model.isCurrent())
                    models = new subjectAndTimeSlot(subject_spinner.getSelectedItem().toString(), topic_edittext.getText().toString().trim(), time_spinner.getSelectedItem().toString(), false);
                reference.setValue(models);
                fcm.subscribeToTopic(TopicSubscription.getTopicForSlot(models)).addOnSuccessListener(unused -> alert.dismiss()).addOnFailureListener(e -> {
                    snackbarTop.showSnackBar(context.getString(R.string.failed_to_subscribe_for_notification).concat(e.toString()), false);
                    alert.dismiss();
                });
            }
        });
        alert.setView(mView);
        alert.show();
    }

    public void updateSubjectForTeacher(subjectAndTimeSlot model, DatabaseReference reference) {

        AlertDialog alert = new AlertDialog.Builder(context).create();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.subjects_layout, null);

        final Button next_btn = mView.findViewById(R.id.next_btn);
        final EditText topic_edittext = mView.findViewById(R.id.topic_edittext);
        final Spinner time_spinner = mView.findViewById(R.id.time_spinner);
        final Spinner subject_spinner = mView.findViewById(R.id.subject_spinner);

        final TextView sub_txt = mView.findViewById(R.id.sub_txt);
        sub_txt.setText(context.getString(R.string.select_sub_and_topic));

        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ButtonDesign buttonDesign = new ButtonDesign(context);
        buttonDesign.setButtonOutline(next_btn);

        subjectList = new ArrayList<>();
        subjectList.add(context.getString(R.string.select_subject));
        FirebaseDatabase.getInstance().getReference().child("Data").child("Administration").child("Lists").child("SubjectList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String value = snapshot1.getValue(String.class);
                        if (value != null)
                            subjectList.add(value);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                snackbarTop.showSnackBar(error.toString(), false);
            }
        });

        ArrayAdapter<String> subjectsAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, subjectList);
        subjectsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subject_spinner.setAdapter(subjectsAdapter);
        subject_spinner.setOnItemSelectedListener(new subjectList());

        topic_edittext.setText(model.getTopic());
        subject_spinner.setSelection(getIndex(subject_spinner, model.getSubject()));

        time_spinner.setVisibility(View.GONE);

        next_btn.setOnClickListener(view -> {
            CONSTANTS.student_teacher_flag = 2;
            buttonDesign.buttonFill(next_btn);
            if (subject_spinner.getSelectedItemPosition() == 0) {
                Toast.makeText(context, context.getString(R.string.select_your_subject), Toast.LENGTH_SHORT).show();
            } else if (topic_edittext.getText().toString().trim().equals("")) {
                topic_edittext.setError(context.getString(R.string.please_enter_topic));
            } else {
                if (model.isCurrent())
                    models = new subjectAndTimeSlot(subject_spinner.getSelectedItem().toString(), topic_edittext.getText().toString().trim(), true);
                else if (!model.isCurrent())
                    models = new subjectAndTimeSlot(subject_spinner.getSelectedItem().toString(), topic_edittext.getText().toString().trim(), false);
                reference.setValue(models);
                alert.dismiss();
            }
        });
        alert.setView(mView);
        alert.show();
    }

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 0;
    }

    public void requestClass(String subject, String topic, String time_slot, String userID) {

        AlertDialog alert = new AlertDialog.Builder(context).create();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.request_class_layout, null);

        final Button send_btn = mView.findViewById(R.id.req_send_btn);
        final TextView subject_txt = mView.findViewById(R.id.req_subject_txt);
        final TextView notify_txt = mView.findViewById(R.id.textView29);
        final EditText topic_edittext = mView.findViewById(R.id.req_topic_edittext);
        final CheckBox checkBox = mView.findViewById(R.id.req_teacher_pref_checkbox);
        final Spinner spinner = mView.findViewById(R.id.req_spinner);

        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ButtonDesign buttonDesign = new ButtonDesign(context);
        buttonDesign.setButtonOutline(send_btn);

        subject_txt.setText(subject);
        topic_edittext.setText(topic);

        subject_txt.setEnabled(false);

        spinner.setEnabled(false);
        notify_txt.setVisibility(View.GONE);

        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (checkBox.isChecked()) {
                notify_txt.setVisibility(View.VISIBLE);

                teacherList = new ArrayList<>();
                userData = new ArrayList<>();

                teacherList.add(context.getString(R.string.select_teacher));
                userData.add(null);

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference().child("Data").child("Main").child("UserType");
                Query query = reference.orderByChild("teacher").equalTo(true);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                if (snapshot1.getKey() != null)
                                    database.getReference().child("Data").child("UserData").child(snapshot1.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                UserData UserData = snapshot.getValue(UserData.class);
                                                if (UserData != null) {
                                                    firestore.collection("Main").document("Class").collection("Ratings").document("TeacherRating").collection(UserData.getUid()).get().addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            QuerySnapshot ratingSnapshot = task1.getResult();
                                                            if (ratingSnapshot != null) {
                                                                UserData.setRating(calculateRating(ratingSnapshot.toObjects(RatingModel.class)));
                                                            } else
                                                                UserData.setRating(0f);
                                                            teacherList.add(UserData.getUserName());
                                                            userData.add(UserData);
                                                            Collections.sort(userData, (lhs, rhs) -> {
                                                                if (rhs != null && lhs != null)
                                                                    return Double.compare(lhs.getRating(), rhs.getRating());
                                                                else
                                                                    return 0;
                                                            });
                                                        }
                                                    });
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                            }
                            TeacherListSpinnerAdapter adapter = new TeacherListSpinnerAdapter(context, teacherList, userData);
                            spinner.setAdapter(adapter);
                            spinner.setOnItemSelectedListener(new teacherListListener());
                            spinner.setEnabled(true);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                spinner.setEnabled(false);
                notify_txt.setVisibility(View.GONE);
            }
        });

        checkBox.setChecked(false);

        CollectionReference reference = FirebaseFirestore.getInstance().collection("Main").document("Class").collection("ClassRequests");

        send_btn.setOnClickListener(view -> {
            buttonDesign.buttonFill(send_btn);
            if (topic_edittext.getText().toString().trim().equals("")) {
                topic_edittext.setError(context.getString(R.string.please_enter_topic));
            } else if (checkBox.isChecked()) {
                if (spinner.getSelectedItemPosition() == 0)
                    snackbarTop.showSnackBar(context.getString(R.string.please_select_teacher), false);
                else {
                    ClassRequestModel classRequestModel = new ClassRequestModel(subject, topic_edittext.getText().toString(), time_slot, requestTeacherData.getUid(), userID);
                    Notify notify = new Notify(context);
                    String sb = context.getString(R.string.subject1) + subject + "\n";
                    String tp = context.getString(R.string.topic1) + topic_edittext.getText().toString().trim() + "\n";
                    String slot = context.getString(R.string.time1) + time_slot;
                    notify.classRequestPayload(context.getString(R.string.a_student_has_requested_a_class_from_you), sb + tp + slot, requestTeacherData.getFCM_TOKEN());
                    reference.add(classRequestModel).addOnSuccessListener(documentReference -> {
                        snackbarTop.showSnackBar(context.getString(R.string.request_submitted), true);
                        alert.dismiss();
                    }).addOnFailureListener(e -> {
                        snackbarTop.showSnackBar(e.toString(), false);
                        alert.dismiss();
                    });
                }
            } else {
                ClassRequestModel classRequestModel = new ClassRequestModel(subject, topic_edittext.getText().toString(), time_slot, userID);
                reference.add(classRequestModel).addOnSuccessListener(documentReference -> {
                    snackbarTop.showSnackBar(context.getString(R.string.request_submitted), true);
                    alert.dismiss();
                }).addOnFailureListener(e -> {
                    snackbarTop.showSnackBar(e.toString(), false);
                    alert.dismiss();
                });
            }
        });
        alert.setView(mView);
        alert.show();
    }

    public class teacherListListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            requestTeacherData = userData.get(i);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    float calculateRating(List<RatingModel> ratingModelList) {
        rating1 = rating2 = rating3 = rating4 = rating5 = 0;
        for (RatingModel ratingModel : ratingModelList) {
            switch (ratingModel.getRating()) {
                case 1:
                    rating1++;
                    break;
                case 2:
                    rating2++;
                    break;
                case 3:
                    rating3++;
                    break;
                case 4:
                    rating4++;
                    break;
                case 5:
                    rating5++;
                    break;
            }
        }
        return (5 * rating5 + 4 * rating4 + 3 * rating3 + 2 * rating2 + 1 * rating1) / (rating5 + rating4 + rating3 + rating2 + rating1);
    }

    public void setLanguage(Activity activity) {

        AlertDialog alert = new AlertDialog.Builder(context).create();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.change_lang_layout, null);

        final RadioButton btn1 = mView.findViewById(R.id.radioButton1);
        final RadioButton btn2 = mView.findViewById(R.id.radioBtn2);
        final RadioButton btn3 = mView.findViewById(R.id.radioBtn3);

        alert.setTitle(context.getString(R.string.app_language_title));
        alert.setMessage(context.getString(R.string.app_lang_message));
        alert.setView(mView);

        SharedPreferences sharedPreferences = context.getSharedPreferences("selectedLanguage", Context.MODE_PRIVATE);

        if (sharedPreferences.getString("language", null) == null)
            btn1.setChecked(true);
        else if (sharedPreferences.getString("language", null).equals("en"))
            btn2.setChecked(true);
        else if (sharedPreferences.getString("language", null).equals("hi"))
            btn3.setChecked(true);

        btn1.setText(context.getString(R.string.app_lang_default));
        btn1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn1.isChecked()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear().apply();
                alert.dismiss();
                snackbarTop.showSnackBar("Settings applied", true);
                restart(activity);
            }
        });

        btn2.setText(context.getString(R.string.app_lang_english));
        btn2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn2.isChecked()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("language", "en");
                editor.apply();
                alert.dismiss();
                snackbarTop.showSnackBar("Settings applied", true);
                restart(activity);
            }
        });

        btn3.setText(context.getString(R.string.app_lang_hindi));
        btn3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn3.isChecked()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("language", "hi");
                editor.apply();
                alert.dismiss();
                snackbarTop.showSnackBar("Settings applied", true);
                restart(activity);
            }
        });
        alert.show();
    }

    public void restart(Activity activity) {
        Intent intent = new Intent(context, MainActivity.class);
        activity.startActivity(intent);
        activity.finishAffinity();
    }

    public void sendNotification() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.send_notification_dialog_lt, null);
        final EditText title = mView.findViewById(R.id.title_text);
        final EditText message = mView.findViewById(R.id.msg_text);

        alert.setTitle(context.getString(R.string.menu_send_notification));
        alert.setMessage(context.getString(R.string.send_notification_to_all));
        alert.setView(mView);

        alert.setPositiveButton(context.getString(R.string.send), (dialogInterface, i) -> {
            if (title.getText().toString().trim().equals(""))
                snackbarTop.showSnackBar(context.getString(R.string.please_enter_title), false);
            else if (message.getText().toString().trim().equals(""))
                snackbarTop.showSnackBar(context.getString(R.string.please_enter_message), false);
            else {
                Notify notify = new Notify(context);
                notify.messageToAll(title.getText().toString().trim(), message.getText().toString().trim(), snackbarTop);
                snackbarTop.showSnackBar(context.getString(R.string.sending), true);
            }
        });

        alert.setNegativeButton(context.getString(R.string.cancel), null);

        alert.show();
    }

    public void sendNotification(String token, String username) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.send_notification_dialog_lt, null);
        final EditText title = mView.findViewById(R.id.title_text);
        final EditText message = mView.findViewById(R.id.msg_text);

        alert.setTitle(context.getString(R.string.menu_send_notification));
        alert.setMessage(context.getString(R.string.send_notification_to) + " ".concat(username));
        alert.setView(mView);

        alert.setPositiveButton(context.getString(R.string.send), (dialogInterface, i) -> {
            if (title.getText().toString().trim().equals(""))
                snackbarTop.showSnackBar(context.getString(R.string.please_enter_title), false);
            else if (message.getText().toString().trim().equals(""))
                snackbarTop.showSnackBar(context.getString(R.string.please_enter_message), false);
            else {
                Notify notify = new Notify(context);
                notify.messageToUser(title.getText().toString().trim(), message.getText().toString().trim(), snackbarTop, token);
                snackbarTop.showSnackBar(context.getString(R.string.sending), true);
            }
        });

        alert.setNegativeButton(context.getString(R.string.cancel), null);

        alert.show();
    }
}