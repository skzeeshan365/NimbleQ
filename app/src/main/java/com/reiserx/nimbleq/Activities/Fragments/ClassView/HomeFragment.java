package com.reiserx.nimbleq.Activities.Fragments.ClassView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.reiserx.nimbleq.Activities.Feedbacks.RateAndFeedbackActivity;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.ButtonDesign;
import com.reiserx.nimbleq.Utils.SharedPreferenceClass;
import com.reiserx.nimbleq.Utils.SnackbarTop;
import com.reiserx.nimbleq.Utils.UserTypeClass;
import com.reiserx.nimbleq.ViewModels.AdministrationViewModel;
import com.reiserx.nimbleq.ViewModels.UserDataViewModel;
import com.reiserx.nimbleq.ViewModels.classViewModel;
import com.reiserx.nimbleq.databinding.FragmentHomeBinding;

import java.util.ArrayList;
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

        firestore = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        binding.scrollView.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);
        binding.progButton.setVisibility(View.GONE);

        buttonDesign = new ButtonDesign(getContext());
        buttonDesign.setButtonOutline(binding.button8);

        buttonDesign.setButtonOutline(binding.rateClassBtn);
        binding.rateClassBtn.setVisibility(View.GONE);

        requireActivity().removeMenuProvider(this);
        requireActivity().addMenuProvider(this, getViewLifecycleOwner());

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
        AdministrationViewModel viewModel = new ViewModelProvider(this).get(AdministrationViewModel.class);
        viewModel.getAdministrator(user.getUid());
        viewModel.getAdminMutableLiveData().observe(this, aBoolean -> {
            menuInflater.inflate(R.menu.class_menu_teacher, menu);
            binding.button8.setVisibility(View.INVISIBLE);
            binding.rateClassBtn.setVisibility(View.GONE);
        });
        viewModel.getAdminErrorMutableLiveData().observe(this, s -> {
            if (userTypeClass.isUserLearner())
                menuInflater.inflate(R.menu.class_menu, menu);
            else
                menuInflater.inflate(R.menu.class_menu_teacher, menu);
        });
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
        } else if (menuItem.getItemId() == R.id.feedbacks_menuitem) {
            SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(requireContext());
            sharedPreferenceClass.setClassID(id);
            NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_navigation_home_to_FragmentFeedback);
        } else if (menuItem.getItemId() == R.id.chat_with_teacher_menuitem) {
            if (teacherData != null) {
                String[] permissions = {Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS};
                Permissions.check(requireContext(), permissions, null, null, new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        if (contactExists(teacherData.getPhoneNumber()))
                            openWhatsappContact(teacherData.getPhoneNumber());
                        else
                            addContact();
                    }
                });
            } else
                snackbarTop.showSnackBar("Failed to get phone number", false);
        } else if (menuItem.getItemId() == R.id.learnerList_menuitem) {
            SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(requireContext());
            sharedPreferenceClass.setClassID(id);
            NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_navigation_home_to_FragmentLearnerListForClass);
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

    void openWhatsappContact(String number) {
        Uri uri = Uri.parse("smsto:" + number);
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.setPackage("com.whatsapp");
        startActivity(i);
    }

    private void addContact() {
        ArrayList<ContentProviderOperation> op_list = new ArrayList<>();
        op_list.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                //.withValue(RawContacts.AGGREGATION_MODE, RawContacts.AGGREGATION_MODE_DEFAULT)
                .build());

        op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, teacherData.getUserName())
                .build());

        op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, teacherData.getPhoneNumber())
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .build());

        try{
            ContentProviderResult[] results = requireContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, op_list);
            androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
            alert.setTitle("Chat with teacher");
            alert.setMessage("Chat with teacher on whatsapp");
            alert.setPositiveButton("open whatsapp", (dialogInterface, i) -> openWhatsappContact(teacherData.getPhoneNumber()));
            alert.setNegativeButton("cancel", null);
            alert.show();
        }catch(Exception e){
            Log.d(CONSTANTS.TAG2, e.toString());
            e.printStackTrace();
        }
    }

    public boolean contactExists(String number) {
        if (number != null) {
            Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
            Cursor cur = requireContext().getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
            try {
                if (cur.moveToFirst()) {
                    return true;
                }
            } finally {
                if (cur != null)
                    cur.close();
            }
        }
        return false;
    }
}