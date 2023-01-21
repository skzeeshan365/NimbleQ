package com.reiserx.nimbleq.Activities.Doubts;

import static com.google.android.gms.common.util.CollectionUtils.listOf;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.reiserx.nimbleq.Adapters.fileListAdapter;
import com.reiserx.nimbleq.Models.Announcements.linkModel;
import com.reiserx.nimbleq.Models.Doubts.DoubtsModel;
import com.reiserx.nimbleq.Models.fileTypeModel;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.ButtonDesign;
import com.reiserx.nimbleq.Utils.SnackbarTop;
import com.reiserx.nimbleq.ViewModels.AdministrationViewModel;
import com.reiserx.nimbleq.ViewModels.DoubtsViewModel;
import com.reiserx.nimbleq.ViewModels.FirebaseStorageViewModel;
import com.reiserx.nimbleq.ViewModels.slotsViewModel;
import com.reiserx.nimbleq.databinding.FragmentSecondBinding;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.MimeType;
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

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

    String[] mimetype;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);

        firebaseStorageViewModel = new ViewModelProvider(this).get(FirebaseStorageViewModel.class);
        doubtsViewModel = new ViewModelProvider(this).get(DoubtsViewModel.class);

        getMimeTypes();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        data = new ArrayList<>();
        links = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView2.setLayoutManager(layoutManager);
        adapter = new fileListAdapter(getContext(), data, adapter, 1);
        binding.recyclerView2.setAdapter(adapter);

        snackbarTop = new SnackbarTop(binding.getRoot());

        binding.subjectTxt.setEnabled(false);

        slotsViewModel slotsViewModel = new ViewModelProvider(this).get(com.reiserx.nimbleq.ViewModels.slotsViewModel.class);
        slotsViewModel.getSubjectForStudents(user.getUid());
        slotsViewModel.getParentItemMutableLiveData().observe(getViewLifecycleOwner(), subjectAndTimeSlot -> {
            binding.subjectTxt.setText(subjectAndTimeSlot.getSubject());
        });

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonDesign = new ButtonDesign(getContext());
        buttonDesign.setButtonOutline(binding.postDoubtBtn);

        postDoubt();
        attachFile();
        observers();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    void postDoubt() {
        binding.postDoubtBtn.setOnClickListener(view -> {
            buttonDesign.buttonFill(binding.postDoubtBtn);
            submitDoubt();
        });
    }

    void submitDoubt() {
        if (binding.subjectTxt.getText().toString().trim().equals(""))
            binding.subjectTxt.setError("Please enter subject");
        else if (binding.doubtTopicTxt.getText().toString().trim().equals(""))
            binding.doubtTopicTxt.setError("Please enter topic");
        else if (binding.doubtOneLineXt.getText().toString().trim().equals(""))
            binding.doubtOneLineXt.setError("Please enter short description");
        else if (binding.doubtDescTxt.getText().toString().trim().equals(""))
            binding.doubtDescTxt.setError("Please enter description");
        else {
            Calendar cal = Calendar.getInstance();
            long currentTime = cal.getTimeInMillis();

            DoubtsModel model = new DoubtsModel(binding.subjectTxt.getText().toString(), binding.doubtTopicTxt.getText().toString(), binding.doubtOneLineXt.getText().toString(), binding.doubtDescTxt.getText().toString(), user.getUid(), currentTime);

            doubtsViewModel.submitDoubt(model, links);
            doubtsViewModel.getFileSubmittedModelMutableLiveData().observe(getViewLifecycleOwner(), unused -> {
                AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
                alert.setTitle("Success");
                alert.setMessage("Doubt has been submitted");
                alert.setPositiveButton("close", (dialogInterface, i) -> requireActivity().onBackPressed());
                alert.setCancelable(false);
                alert.show();
            });
            doubtsViewModel.getDatabaseErrorMutableLiveData().observe(getViewLifecycleOwner(), s -> snackbarTop.showSnackBar(s, false));
        }
    }

    void attachFile() {
        binding.attachHolder.setOnClickListener(view -> {
            if (mimetype != null && mimetype.length != 0) {
                AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
                alert.setMessage("Send a photo");

                alert.setPositiveButton("Files", (dialogInterface, i) -> {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetype);
                    FilesActivityResultLauncher.launch(intent);
                });
                alert.setNegativeButton("Images", (dialogInterface, i) -> {
                    FishBun.with(SecondFragment.this)
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
                            .exceptMimeType(listOf(MimeType.GIF))
                            .setActionBarColor(requireContext().getColor(R.color.primaryColor), requireActivity().getColor(R.color.primaryColor), false)
                            .startAlbumWithActivityResultCallback(ImagesActivityResultLauncher);
                });
                alert.show();
            } else {
                FishBun.with(SecondFragment.this)
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
                        .exceptMimeType(listOf(MimeType.GIF))
                        .setActionBarColor(requireContext().getColor(R.color.primaryColor), requireActivity().getColor(R.color.primaryColor), false)
                        .startAlbumWithActivityResultCallback(ImagesActivityResultLauncher);
            }
        });
    }

    ActivityResultLauncher<Intent> ImagesActivityResultLauncher = registerForActivityResult(
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
                            }
                        }
                    }
                }
            });

    ActivityResultLauncher<Intent> FilesActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (result.getData() != null) {
                            if (!adapter.isElementExist(getFileName(getContext(), result.getData().getData()))) {
                                firebaseStorageViewModel.uploadSingleFile(getContext(), user.getUid(), result.getData().getData());
                            } else
                                snackbarTop.showSnackBar("File already added", false);
                        }
                    }
                }
            });

    @SuppressLint("Range")
    public String getFileName(Context context, Uri uri) {
        if (uri.toString().startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = String.valueOf(cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
                }
            } finally {
                cursor.close();
            }
        } else if (uri.toString().startsWith("file://")) {
            File myFile = new File(uri.toString());
            displayName = myFile.getName();
        }
        return displayName;
    }

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

    void getMimeTypes() {
        AdministrationViewModel administrationViewModel = new ViewModelProvider(this).get(AdministrationViewModel.class);
        administrationViewModel.getFileEnabled();
        administrationViewModel.getFileEnabledMutableLiveData().observe(getViewLifecycleOwner(), enabled -> {
            if (!enabled)
                administrationViewModel.getMimeTypesForGroupChats();
        });
        administrationViewModel.getMimeTypesListMutableLiveData().observe(getViewLifecycleOwner(), stringList -> mimetype = stringList.toArray(new String[0]));
        administrationViewModel.getDatabaseErrorMutableLiveData().observe(getViewLifecycleOwner(), s -> snackbarTop.showSnackBar(s, false));
    }
}