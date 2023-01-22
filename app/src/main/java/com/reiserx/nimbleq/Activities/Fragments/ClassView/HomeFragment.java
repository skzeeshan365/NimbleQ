package com.reiserx.nimbleq.Activities.Fragments.ClassView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.nimbleq.Activities.RateAndFeedbackActivity;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.Models.userDetails;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.ButtonDesign;
import com.reiserx.nimbleq.Utils.SnackbarTop;
import com.reiserx.nimbleq.Utils.UserTypeClass;
import com.reiserx.nimbleq.ViewModels.UserDataViewModel;
import com.reiserx.nimbleq.ViewModels.classViewModel;
import com.reiserx.nimbleq.databinding.FragmentHomeBinding;

import java.util.Objects;

import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.ZoomSDK;

public class HomeFragment extends Fragment implements MenuProvider {

    private FragmentHomeBinding binding;

    FirebaseFirestore firestore;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseUser user;

    ButtonDesign buttonDesign;
    classViewModel classViewModel;
    UserDataViewModel userDataViewModel;

    String id;

    SnackbarTop snackbarTop;

    UserData teacherData;

    private static String MEETING_ID, MEETING_PASSWORD;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        classViewModel = new ViewModelProvider(this).get(classViewModel.class);
        userDataViewModel = new ViewModelProvider(this).get(UserDataViewModel.class);

        binding.scrollView.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);
        binding.progButton.setVisibility(View.GONE);

        buttonDesign = new ButtonDesign(getContext());
        buttonDesign.setButtonOutline(binding.button8);

        buttonDesign.setButtonOutline(binding.rateClassBtn);
        binding.rateClassBtn.setVisibility(View.GONE);

        userDataViewModel.getUserData().observe(getViewLifecycleOwner(), userData -> {
            binding.classTeacher.setText(userData.getUserName());

            this.teacherData = userData;

            if (user.getUid().equals(userData.getUid())) {

                binding.rateClassBtn.setVisibility(View.GONE);

                binding.button8.setText("Join meeting");
                buttonDesign.setButtonOutline(binding.button8);
                binding.button8.setOnClickListener(view -> {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Join class as Host");
                    alert.setMessage("Joining class as host is not supported in this app\nYou have to join it from zoom app\nMEETING ID: "+MEETING_ID+"\nMEETING PASSWORD: "+MEETING_PASSWORD);
                    alert.setPositiveButton("open", (dialogInterface, i) -> {
                        PackageManager pm = requireContext().getPackageManager();
                        Intent intent = pm.getLaunchIntentForPackage("us.zoom.videomeetings");
                        if (intent != null) {
                            startActivity(intent);
                        }
                    });
                    alert.setNegativeButton("cancel", null);
                    alert.show();
                });
            } else {
                classViewModel.getClassState(user.getUid(), id);
                classViewModel.getClassState().observe(getViewLifecycleOwner(), state -> {
                    if (state == 2) {
                        binding.button8.setText("Join meeting");
                        buttonDesign.setButtonOutline(binding.button8);
                        setJoinMeeting();
                        binding.rateClassBtn.setVisibility(View.VISIBLE);
                    } else if (state == 3) {
                        binding.button8.setText("Join class");
                        buttonDesign.setButtonOutline(binding.button8);
                        binding.button8.setOnClickListener(view -> {
                            buttonDesign.buttonFill(binding.button8);
                            classViewModel.setClassState(user.getUid(), id, userData.getFCM_TOKEN(), true, getContext());
                        });
                        binding.rateClassBtn.setVisibility(View.GONE);
                    } else if (state == 1) {
                        snackbarTop.showSnackBar("Class joined", true);
                        binding.button8.setText("Join meeting");
                        buttonDesign.setButtonOutline(binding.button8);
                        setJoinMeeting();
                        binding.rateClassBtn.setVisibility(View.VISIBLE);
                    }
                });
                binding.rateClassBtn.setOnClickListener(view -> {
                    buttonDesign.buttonFill(binding.rateClassBtn);
                    rateClass();
                });
            }
        });

        userDataViewModel.getDatabaseErrorMutableLiveData().observe(getViewLifecycleOwner(), error -> {
            snackbarTop.showSnackBar(error, false);
        });

        snackbarTop = new SnackbarTop(binding.getRoot());

        requireActivity().removeMenuProvider(this);
        requireActivity().addMenuProvider(this, getViewLifecycleOwner());

        firestore = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        id = getActivity().getIntent().getExtras().getString("classID");

        fetchClass();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void rateClass() {
        Intent intent = new Intent(getContext(), RateAndFeedbackActivity.class);
        intent.putExtra("id", 1);
        intent.putExtra("classID", id);
        intent.putExtra("Message", "How was your experience in class ".concat(Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).getTitle().toString()));
        intent.putExtra("userID", user.getUid());
        intent.putExtra("token", teacherData.getFCM_TOKEN());
        intent.putExtra("classname", Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).getTitle().toString());
        requireContext().startActivity(intent);
    }

    void setJoinMeeting() {
        Log.d(CONSTANTS.TAG, "called");
        UserDataViewModel userDataViewModel = new ViewModelProvider(this).get(UserDataViewModel.class);

        userDataViewModel.getUsername(user.getUid());
        userDataViewModel.getUserName().observe(getViewLifecycleOwner(), username -> {
            binding.button8.setOnClickListener(view -> {
                if (username != null) {
                    buttonDesign.buttonFill(binding.button8);
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Join meeting");
                    alert.setMessage("Are you sure you want to join this meeting");
                    alert.setPositiveButton("join", (dialogInterface, i) -> joinMeeting(username, MEETING_ID, MEETING_PASSWORD));
                    alert.setNegativeButton("cancel", (dialogInterface, i) -> buttonDesign.setButtonOutline(binding.button8));
                    alert.show();
                }
            });
        });

        userDataViewModel.getDatabaseErrorMutableLiveData().observe(getViewLifecycleOwner(), error -> {
            snackbarTop.showSnackBar(error, false);
            binding.textView9.setText(error);
            binding.scrollView.setVisibility(View.GONE);
            binding.progHolder.setVisibility(View.VISIBLE);
            binding.progressBar2.setVisibility(View.GONE);
            binding.textView9.setVisibility(View.VISIBLE);
            snackbarTop.showSnackBar(error, false);
        });
    }

    void fetchClass() {

        classViewModel.getClassData(id);
        classViewModel.getClassData().observe(getViewLifecycleOwner(), classModel -> {
            if (classModel != null) {

                MEETING_ID = classModel.getMeetingID();
                MEETING_PASSWORD = classModel.getMeetingPassword();

                Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(classModel.getClassName());
                binding.classSubject.setText(classModel.getSubject());
                binding.classTopic.setText(classModel.getTopic());
                binding.classInfo.setText(classModel.getClassInfo());
                binding.timeSlot.setText(classModel.getTime_slot());
                binding.gradeTxt.setText(classModel.getGrade());
                userDataViewModel.getUserData(classModel.getTeacher_info());
                if (classModel.getRating() > 0) {
                    String rating = String.format("%.1f", classModel.getRating());
                    binding.ratingRxt.setText(rating);
                    binding.ratingBar.setRating(Float.parseFloat(rating) / 5);
                } else  {
                    binding.ratingRxt.setText("0");
                    binding.ratingBar.setRating(0);
                }
                setJoinMeeting();
                binding.scrollView.setVisibility(View.VISIBLE);
                binding.progHolder.setVisibility(View.GONE);
            }
        });

        classViewModel.getDatabaseErrorMutableLiveData().observe(getViewLifecycleOwner(), error -> {
            binding.textView9.setText(error);
            binding.scrollView.setVisibility(View.GONE);
            binding.progHolder.setVisibility(View.VISIBLE);
            binding.progressBar2.setVisibility(View.GONE);
            binding.textView9.setVisibility(View.VISIBLE);
            snackbarTop.showSnackBar(error, false);
        });
    }

    void joinMeeting(String name, String meetingID, String meeting_password) {
        if (meetingID != null && meeting_password != null) {
            MeetingService meetingService = ZoomSDK.getInstance().getMeetingService();
            JoinMeetingOptions joinMeetingOptions = new JoinMeetingOptions();
            JoinMeetingParams joinMeetingParams = new JoinMeetingParams();
            joinMeetingParams.displayName = name;
            joinMeetingParams.meetingNo = meetingID;
            joinMeetingParams.password = meeting_password;
            meetingService.joinMeetingWithParams(getContext(), joinMeetingParams, joinMeetingOptions);
        }
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menu.clear();
        UserTypeClass userTypeClass = new UserTypeClass(requireContext());
        if (userTypeClass.isUserLearner())
            menuInflater.inflate(R.menu.class_menu, menu);
        else
            menuInflater.inflate(R.menu.class_menu_teacher, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.Rate_menu_item) {
            rateTeacher();
        } else if (menuItem.getItemId() == R.id.Teacher_info_menuitem) {
            snackbarTop.showSnackBar("Fetching details...", true);
            userDataViewModel.getUserDetails(teacherData.getUid());
            userDataViewModel.getUserDetailsMutableLiveData().observe(getViewLifecycleOwner(), userDetails -> {
                Log.d(CONSTANTS.TAG2, String.valueOf(userDetails));
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle(teacherData.getUserName());
                String grade = "Grade: "+userDetails.getGrade();
                String stateCity = "\nLives in: "+userDetails.getState()+", "+userDetails.getCity();
                String gender = "\nGender: "+userDetails.getGender();
                String schoolname = "\nSchool: "+userDetails.getSchoolName();
                alert.setMessage(grade+schoolname+stateCity+gender);
                alert.setPositiveButton("Rate", (dialogInterface, i) -> {
                    rateTeacher();
                });
                alert.setNegativeButton("cancel", null);
                alert.show();
            });
            userDataViewModel.getDatabaseErrorMutableLiveData().observe(getViewLifecycleOwner(), s -> snackbarTop.showSnackBar(s, false));
        } else if (menuItem.getItemId() == R.id.leave_class_menu_item) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("Leave class");
            alert.setMessage("Are you sure you want to leave this class\nAfter leaving you will not receive any updates about this class");
            alert.setPositiveButton("leave", (dialogInterface, i) -> classViewModel.setClassState(user.getUid(), id, teacherData.getFCM_TOKEN(), false, getContext()));
            alert.setNegativeButton("cancel", null);
            alert.show();
        }
        return false;
    }

    void rateTeacher() {
        Intent intent = new Intent(getContext(), RateAndFeedbackActivity.class);
        intent.putExtra("id", 2);
        intent.putExtra("teacherID", teacherData.getUid());
        intent.putExtra("Message", "How was your experience with");
        intent.putExtra("userID", user.getUid());
        intent.putExtra("token", teacherData.getFCM_TOKEN());
        requireContext().startActivity(intent);
    }
}