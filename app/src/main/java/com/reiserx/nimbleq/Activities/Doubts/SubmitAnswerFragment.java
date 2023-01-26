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
import com.reiserx.nimbleq.Models.Doubts.AnswerModel;
import com.reiserx.nimbleq.Models.Doubts.DoubtsModel;
import com.reiserx.nimbleq.Models.fileTypeModel;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.ButtonDesign;
import com.reiserx.nimbleq.Utils.Notify;
import com.reiserx.nimbleq.Utils.SharedPreferenceClass;
import com.reiserx.nimbleq.Utils.SnackbarTop;
import com.reiserx.nimbleq.ViewModels.AdministrationViewModel;
import com.reiserx.nimbleq.ViewModels.DoubtsViewModel;
import com.reiserx.nimbleq.ViewModels.FirebaseStorageViewModel;
import com.reiserx.nimbleq.ViewModels.UserDataViewModel;
import com.reiserx.nimbleq.databinding.FragmentSubmitAnswerBinding;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.MimeType;
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SubmitAnswerFragment extends Fragment {

    FragmentSubmitAnswerBinding binding;

    ButtonDesign buttonDesign;

    FirebaseStorageViewModel firebaseStorageViewModel;
    DoubtsViewModel doubtsViewModel;
    UserDataViewModel userDataViewModel;

    FirebaseAuth auth;
    FirebaseUser user;

    ArrayList<fileTypeModel> data;
    ArrayList<linkModel> links;
    fileListAdapter adapter;
    LinearLayoutManager layoutManager;

    SnackbarTop snackbarTop;

    String displayName, userName;

    String[] mimetype;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSubmitAnswerBinding.inflate(inflater, container, false);

        firebaseStorageViewModel = new ViewModelProvider(this).get(FirebaseStorageViewModel.class);
        doubtsViewModel = new ViewModelProvider(this).get(DoubtsViewModel.class);

        userDataViewModel = new ViewModelProvider(this).get(UserDataViewModel.class);

        getMimeTypes();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            userDataViewModel.getUsername(user.getUid());
        }
        userDataViewModel.getUserName().observe(getViewLifecycleOwner(), s -> userName = s);
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

        postAnswer();
        attachFile();
        observers();
    }

    void postAnswer() {
        binding.submitAnswerBtn.setOnClickListener(view -> {
            buttonDesign.buttonFill(binding.submitAnswerBtn);
            submitAnswer();
        });
    }

    void submitAnswer() {
        if (binding.answerDescTxt.getText().toString().trim().equals(""))
            binding.answerDescTxt.setError(getString(R.string.field_required));
        else {
            SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(requireContext());
            DoubtsModel doubtsModel = sharedPreferenceClass.getDoubtInfo();

            userDataViewModel.getUserData(doubtsModel.getUserID());
            userDataViewModel.getUserData().observe(getViewLifecycleOwner(), userData -> {
                AnswerModel answerModel = new AnswerModel(doubtsModel.getId(), binding.answerDescTxt.getText().toString().trim(), user.getUid());

                doubtsViewModel.submitAnswer(answerModel, links);
                doubtsViewModel.getAnswerSubmittedModelMutableLiveData().observe(getViewLifecycleOwner(), unused -> {
                    Notify notify = new Notify(getContext());
                    notify.submitAnswerPayload(userName.concat(getString(R.string.has_answered_doubt)), binding.answerDescTxt.getText().toString().trim(), userData.getFCM_TOKEN(), doubtsModel);
                    AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
                    alert.setTitle(getString(R.string.success));
                    alert.setMessage(getString(R.string.answer_has_been_submitted));
                    alert.setPositiveButton(getString(R.string.close), (dialogInterface, i) -> requireActivity().onBackPressed());
                    alert.setCancelable(false);
                    alert.show();
                });
                doubtsViewModel.getDatabaseErrorMutableLiveData().observe(getViewLifecycleOwner(), s -> snackbarTop.showSnackBar(s, false));
            });
        }
    }

    void attachFile() {
        binding.attachHolder.setOnClickListener(view -> {
            if (mimetype != null && mimetype.length != 0) {
                AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
                alert.setMessage(getString(R.string.send_a_photo));

                alert.setPositiveButton(getString(R.string.files), (dialogInterface, i) -> {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetype);
                    FilesActivityResultLauncher.launch(intent);
                });
                alert.setNegativeButton(getString(R.string.images), (dialogInterface, i) -> FishBun.with(SubmitAnswerFragment.this)
                        .setImageAdapter(new GlideAdapter())
                        .setIsUseDetailView(true)
                        .setMaxCount(1)
                        .setMinCount(1)
                        .setPickerSpanCount(2)
                        .setAlbumSpanCount(1, 2)
                        .setButtonInAlbumActivity(false)
                        .setCamera(true)
                        .setReachLimitAutomaticClose(true)
                        .setAllViewTitle(getString(R.string.all))
                        .setActionBarTitle(getString(R.string.images))
                        .textOnImagesSelectionLimitReached(getString(R.string.limit_reached))
                        .textOnNothingSelected(getString(R.string.nothing_selected))
                        .setSelectCircleStrokeColor(requireContext().getColor(R.color.primaryColor))
                        .isStartInAllView(false)
                        .exceptMimeType(listOf(MimeType.GIF))
                        .setActionBarColor(requireContext().getColor(R.color.primaryColor), requireActivity().getColor(R.color.primaryColor), false)
                        .startAlbumWithActivityResultCallback(ImagesActivityResultLauncher));
                alert.show();
            } else {
                FishBun.with(SubmitAnswerFragment.this)
                        .setImageAdapter(new GlideAdapter())
                        .setIsUseDetailView(true)
                        .setMaxCount(1)
                        .setMinCount(1)
                        .setPickerSpanCount(2)
                        .setAlbumSpanCount(1, 2)
                        .setButtonInAlbumActivity(false)
                        .setCamera(true)
                        .setReachLimitAutomaticClose(true)
                        .setAllViewTitle(getString(R.string.all))
                        .setActionBarTitle(getString(R.string.images))
                        .textOnImagesSelectionLimitReached(getString(R.string.limit_reached))
                        .textOnNothingSelected(getString(R.string.nothing_selected))
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
                            } else
                                snackbarTop.showSnackBar(getString(R.string.file_already_added), false);
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
                        // There are no request codes
                        Intent datas = result.getData();
                        if (datas != null) {

                            if (!adapter.isElementExist(getFileName(getContext(), datas.getData()))) {
                                firebaseStorageViewModel.uploadSingleFile(getContext(), user.getUid(), datas.getData());
                            } else
                                snackbarTop.showSnackBar(getString(R.string.file_already_added), false);
                        }
                    }
                }
            });

    @SuppressLint("NotifyDataSetChanged")
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