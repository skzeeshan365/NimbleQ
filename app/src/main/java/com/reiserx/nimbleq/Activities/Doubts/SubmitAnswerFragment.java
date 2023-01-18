package com.reiserx.nimbleq.Activities.Doubts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.reiserx.nimbleq.Adapters.fileListAdapter;
import com.reiserx.nimbleq.Models.Announcements.linkModel;
import com.reiserx.nimbleq.Models.Doubts.AnswerModel;
import com.reiserx.nimbleq.Models.Doubts.DoubtsModel;
import com.reiserx.nimbleq.Models.fileTypeModel;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.ButtonDesign;
import com.reiserx.nimbleq.Utils.SharedPreferenceClass;
import com.reiserx.nimbleq.Utils.SnackbarTop;
import com.reiserx.nimbleq.ViewModels.DoubtsViewModel;
import com.reiserx.nimbleq.ViewModels.FirebaseStorageViewModel;
import com.reiserx.nimbleq.databinding.FragmentSubmitAnswerBinding;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SubmitAnswerFragment extends Fragment {

    FragmentSubmitAnswerBinding binding;

    ButtonDesign buttonDesign;

    FirebaseStorageViewModel firebaseStorageViewModel;
    DoubtsViewModel doubtsViewModel;

    FirebaseAuth auth;
    FirebaseUser user;

    ArrayList<fileTypeModel> data;
    ArrayList<linkModel> links;
    fileListAdapter adapter;
    LinearLayoutManager layoutManager;

    SnackbarTop snackbarTop;

    String displayName;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSubmitAnswerBinding.inflate(inflater, container, false);

        firebaseStorageViewModel = new ViewModelProvider(this).get(FirebaseStorageViewModel.class);
        doubtsViewModel = new ViewModelProvider(this).get(DoubtsViewModel.class);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        data = new ArrayList<>();
        links = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView2.setLayoutManager(layoutManager);
        adapter = new fileListAdapter(getContext(), data, adapter, 1);
        binding.recyclerView2.setAdapter(adapter);

        snackbarTop = new SnackbarTop(binding.getRoot());

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonDesign = new ButtonDesign(getContext());
        buttonDesign.setButtonOutline(binding.submitAnswerBtn);

        postDoubt();
        attachFile();
        observers();
    }

    void postDoubt() {
        binding.submitAnswerBtn.setOnClickListener(view -> {
            buttonDesign.buttonFill(binding.submitAnswerBtn);
            submitDoubt();
        });
    }

    void submitDoubt() {
        if (binding.answerDescTxt.getText().toString().trim().equals(""))
            binding.answerDescTxt.setError("Please enter subject");
        else {
            SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(requireContext());
            DoubtsModel doubtsModel = sharedPreferenceClass.getDoubtInfo();

            AnswerModel answerModel = new AnswerModel(doubtsModel.getId(), binding.answerDescTxt.getText().toString().trim(), user.getUid());

            doubtsViewModel.submitAnswer(answerModel, links);
            doubtsViewModel.getAnswerSubmittedModelMutableLiveData().observe(getViewLifecycleOwner(), unused -> {
                AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
                alert.setTitle("Success");
                alert.setMessage("Answer has been submitted");
                alert.setPositiveButton("close", (dialogInterface, i) -> requireActivity().onBackPressed());
                alert.setCancelable(false);
                alert.show();
            });
            doubtsViewModel.getDatabaseErrorMutableLiveData().observe(getViewLifecycleOwner(), s -> snackbarTop.showSnackBar(s, false));
        }
    }

    void attachFile() {
        binding.attachHolder.setOnClickListener(view -> FishBun.with(SubmitAnswerFragment.this)
                .setImageAdapter(new GlideAdapter())
                .setIsUseDetailView(true)
                .setMaxCount(1)
                .setMinCount(1)
                .setPickerSpanCount(2)
                .setAlbumSpanCount(1, 2)
                .setButtonInAlbumActivity(false)
                .setCamera(true)
                .setReachLimitAutomaticClose(true)
                .setAllViewTitle("All")
                .setActionBarTitle("Image Library")
                .textOnImagesSelectionLimitReached("Limit Reached!")
                .textOnNothingSelected("Nothing Selected")
                .setSelectCircleStrokeColor(requireContext().getColor(R.color.primaryColor))
                .isStartInAllView(false)
                .setActionBarColor(requireContext().getColor(R.color.primaryColor), requireActivity().getColor(R.color.primaryColor), false)
                .startAlbumWithActivityResultCallback(someActivityResultLauncher));
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent datas = result.getData();
                        if (datas != null) {
                            List<Uri> path = datas.getParcelableArrayListExtra(FishBun.INTENT_PATH);

                            if (!adapter.isElementExist(getFileName(getContext(), path.get(0)))) {
                                firebaseStorageViewModel.uploadSingleFile(getContext(), user.getUid(), path.get(0));
                            } else
                                snackbarTop.showSnackBar("File already added", false);
                        }
                    }
                }
            });

    void observers() {
        firebaseStorageViewModel.getUploadStartMutableLiveData().observe(getViewLifecycleOwner(), fileTypeModel -> {
            data.add(fileTypeModel);
            adapter.notifyDataSetChanged();
        });

        firebaseStorageViewModel.getUploadProgressMutableLiveData().observe(getViewLifecycleOwner(), uploadProgressModel -> {
            int pos = adapter.getTargetPosition(uploadProgressModel.getFilename());
            adapter.updateProg(pos, uploadProgressModel.getProgress());
            adapter.notifyItemChanged(pos);
        });

        firebaseStorageViewModel.getRemoteFileModelSingleMutableLiveData().observe(getViewLifecycleOwner(), remoteFileModel -> {
            adapter.uploadDone(adapter.getTargetPosition(remoteFileModel.getFilename()));
            linkModel linkModel = new linkModel(remoteFileModel.getUrl(), remoteFileModel.getFilename());
            links.add(linkModel);
            adapter.notifyDataSetChanged();
        });
        firebaseStorageViewModel.getDatabaseErrorMutableLiveData().observe(getViewLifecycleOwner(), s -> snackbarTop.showSnackBar(s, false));
    }

    @SuppressLint("Range")
    public String getFileName(Context context, Uri uri) {
        if (uri.toString().startsWith("content://")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = String.valueOf(cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
                }
            }
        } else if (uri.toString().startsWith("file://")) {
            File myFile = new File(uri.toString());
            displayName = myFile.getName();
        }
        return displayName;
    }
}