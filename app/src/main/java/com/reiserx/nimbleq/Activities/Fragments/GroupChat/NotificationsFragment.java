package com.reiserx.nimbleq.Activities.Fragments.GroupChat;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.google.android.gms.common.util.CollectionUtils.listOf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capybaralabs.swipetoreply.SwipeController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.reiserx.nimbleq.Adapters.MessagesAdapter;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.Message;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.FileUtil;
import com.reiserx.nimbleq.Utils.SharedPreferenceClass;
import com.reiserx.nimbleq.Utils.SnackbarTop;
import com.reiserx.nimbleq.ViewModels.AdministrationViewModel;
import com.reiserx.nimbleq.ViewModels.ChatsViewModel;
import com.reiserx.nimbleq.ViewModels.FirebaseStorageViewModel;
import com.reiserx.nimbleq.ViewModels.UserDataViewModel;
import com.reiserx.nimbleq.databinding.FragmentNotificationsBinding;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.MimeType;
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NotificationsFragment extends Fragment implements MenuProvider {

    private FragmentNotificationsBinding binding;

    FirebaseAuth auth;
    FirebaseUser user;

    private String replyUId, replyID, senderName;

    ChatsViewModel chatsViewModel;
    FirebaseStorageViewModel firebaseStorageViewModel;

    String classID;

    LinearLayoutManager layoutManager;
    MessagesAdapter adapter;
    ArrayList<Message> filteredDataList;
    ArrayList<Message> dataList;
    List<Message> data;

    Message message;

    SnackbarTop snackbarTop;

    int pastVisiblesItems, totalItemCount, lastItem;

    ArrayList<Uri> path;

    String newText;

    String[] mimetype;

    boolean enabled = false, enableLatest = false;

    @SuppressLint("NotifyDataSetChanged")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        chatsViewModel = new ViewModelProvider(this).get(ChatsViewModel.class);
        UserDataViewModel userDataViewModel = new ViewModelProvider(this).get(UserDataViewModel.class);
        firebaseStorageViewModel = new ViewModelProvider(this).get(FirebaseStorageViewModel.class);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        binding.recyclerView.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);
        binding.progButton.setVisibility(View.GONE);

        snackbarTop = new SnackbarTop(root);

        classID = requireActivity().getIntent().getExtras().getString("classID");

        userDataViewModel.getUsername(user.getUid());
        userDataViewModel.getUserName().observe(getViewLifecycleOwner(), s -> senderName = s);
        userDataViewModel.getDatabaseErrorMutableLiveData().observe(getViewLifecycleOwner(), s -> snackbarTop.showSnackBar(s, false));

        binding.sendButton.setOnClickListener(view -> sendMessage());

        path = new ArrayList<>();
        dataList = new ArrayList<>();

        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);
        adapter = new MessagesAdapter(getContext(), binding.recyclerView, classID, user.getUid(), chatsViewModel, data);
        adapter.setAdapter(adapter);
        binding.replyHolder.setVisibility(View.GONE);

        SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(requireContext());
        adapter.setTeacherID(sharedPreferenceClass.getTeacherID());

        getMessages();

        scrollListener();

        binding.attachImg.setOnClickListener(v -> {

            if (mimetype != null && mimetype.length != 0) {
                AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
                alert.setMessage(getString(R.string.send_a_photo));

                alert.setPositiveButton(getString(R.string.files), (dialogInterface, i) -> {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetype);
                    FilesActivityResultLauncher.launch(intent);
                });
                alert.setNegativeButton(getString(R.string.images), (dialogInterface, i) -> FishBun.with(NotificationsFragment.this)
                        .setImageAdapter(new GlideAdapter())
                        .setIsUseDetailView(true)
                        .setMaxCount(5)
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
                FishBun.with(NotificationsFragment.this)
                        .setImageAdapter(new GlideAdapter())
                        .setIsUseDetailView(true)
                        .setMaxCount(5)
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

        chatsViewModel.getAllMessagesListMutableLiveData().observe(getViewLifecycleOwner(), messages -> {
            adapter.setData(messages);
            dataList.clear();
            dataList.addAll(messages);
            binding.recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        });

        chatsViewModel.getLatestMessages(classID);
        chatsViewModel.getLatestMessageListMutableLiveData().observe(getViewLifecycleOwner(), message -> {
            if (enableLatest) {
                adapter.addData(message);
                binding.recyclerView.setAdapter(adapter);

                if (adapter.getItemCount() > 50) {
                    chatsViewModel.getMessages(classID, 15);
                }
            }
        });

        getMimeTypes();

        swipeController(adapter.getList());

        requireActivity().removeMenuProvider(this);
        requireActivity().addMenuProvider(this, getViewLifecycleOwner());
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void sendMessage() {
        String MessageTxt = binding.messageBox.getText().toString();

        if (!MessageTxt.equals("")) {

            Calendar cal = Calendar.getInstance();
            long currentTime = cal.getTimeInMillis();

            if (binding.replyMsg.getText().toString().trim().equals("")) {
                message = new Message(MessageTxt, user.getUid(), senderName, currentTime);
            } else {
                message = new Message(MessageTxt, user.getUid(), senderName, binding.replyMsg.getText().toString(), binding.replyNameTxts.getText().toString(), replyUId, replyID, currentTime);
            }

            chatsViewModel.submitMessage(message, classID);
            chatsViewModel.getMessageMutableLiveData().observe(getViewLifecycleOwner(), message1 -> {
                binding.messageBox.setText("");
                binding.replyHolder.setVisibility(View.GONE);
                binding.replyMsg.setText("");
                binding.replyNameTxts.setText("");
                replyID = null;
                replyUId = null;
                if (adapter.getList().isEmpty())
                    getMessages();
            });
        } else
            Toast.makeText(getContext(), getString(R.string.type_a_message), Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getMessages() {
        chatsViewModel.getMessages(classID, 15);
        chatsViewModel.getMessageListMutableLiveData().observe(getViewLifecycleOwner(), messages -> {
            adapter.setData(messages);
            dataList.addAll(messages);
            binding.recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            enableLatest = true;
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.progHolder.setVisibility(View.GONE);
        });
        chatsViewModel.getDatabaseErrorMutableLiveData().observe(getViewLifecycleOwner(), error -> {
            binding.textView9.setText(error);
            binding.recyclerView.setVisibility(View.GONE);
            binding.progHolder.setVisibility(View.VISIBLE);
            binding.progressBar2.setVisibility(View.GONE);
            binding.textView9.setVisibility(View.VISIBLE);
        });
    }

    public void swipeController(List<Message> messages) {
        @SuppressLint("SetTextI18n") SwipeController controller = new SwipeController(getContext(), position -> {
            InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(INPUT_METHOD_SERVICE);
            if (!inputMethodManager.isAcceptingText()) {
                inputMethodManager.toggleSoftInputFromWindow(binding.messageBox.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                binding.messageBox.requestFocus();
            }
            TransitionManager.beginDelayedTransition(binding.cardView,
                    new AutoTransition());
            binding.replyHolder.setVisibility(View.VISIBLE);
            Message message = messages.get(position);
            replyID = message.getMessageId();
            if (message.getSenderId().equals(user.getUid())) {
                binding.replyNameTxts.setText(getString(R.string.me));
                replyUId = user.getUid();
            } else {
                binding.replyNameTxts.setText(message.getReplyname());
                replyUId = message.getSenderId();
            }
            if (message.getImageUrl() != null) {
                binding.replyMsg.setText(getString(R.string.photo));
            } else {
                binding.replyMsg.setText(message.getMessage());
            }
            binding.imageView4.setOnClickListener(view -> {
                binding.replyHolder.setVisibility(View.GONE);
                binding.replyMsg.setText("");
                binding.replyNameTxts.setText("");
                replyID = null;
                replyUId = null;
            });
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(controller);
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);
    }

    private void scrollListener() {
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy < 0) { //check for scroll down
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstCompletelyVisibleItemPosition();

                    if (pastVisiblesItems == 0) {
                        if (lastItem != totalItemCount) {
                            chatsViewModel.paginateMessages(classID, adapter);
                            lastItem = totalItemCount;

                        }
                    }
                }
            }
        });
    }

    ActivityResultLauncher<Intent> ImagesActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        path.clear();
                        if (result.getData() != null) {
                            path = result.getData().getParcelableArrayListExtra(FishBun.INTENT_PATH);
                            Log.d(CONSTANTS.TAG2, String.valueOf(FileUtil.convertUriToFilePath(getContext(), path.get(0))));
                            firebaseStorageViewModel.uploadMultipleImages(getContext(), user.getUid(), path);
                            Toast.makeText(requireContext(), getString(R.string.uploading_files), Toast.LENGTH_SHORT).show();
                            firebaseStorageViewModel.getRemoteFileModelMutableLiveData().observe(getViewLifecycleOwner(), remoteFileModel -> {

                                Calendar cal = Calendar.getInstance();
                                long currentTime = cal.getTimeInMillis();

                                if (binding.replyMsg.getText().toString().trim().equals("")) {
                                    message = new Message(remoteFileModel.getUrl(), remoteFileModel.getFilename(), user.getUid(), senderName, currentTime);
                                } else {
                                    message = new Message(remoteFileModel.getUrl(), remoteFileModel.getFilename(), user.getUid(), senderName, binding.replyMsg.getText().toString(), binding.replyNameTxts.getText().toString(), replyUId, replyID, currentTime);
                                }

                                chatsViewModel.submitMessage(message, classID);
                                chatsViewModel.getMessageMutableLiveData().observe(getViewLifecycleOwner(), message1 -> {
                                    binding.messageBox.setText("");
                                    binding.replyHolder.setVisibility(View.GONE);
                                    binding.replyMsg.setText("");
                                    binding.replyNameTxts.setText("");
                                    replyID = null;
                                    replyUId = null;
                                });
                            });
                            firebaseStorageViewModel.getDatabaseErrorMutableLiveData().observe(getViewLifecycleOwner(), s -> Log.d(CONSTANTS.TAG2, s));
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
                        path.clear();
                        if (result.getData() != null) {
                            if (null != result.getData().getClipData()) {
                                for (int i = 0; i < result.getData().getClipData().getItemCount(); i++) {
                                    path.add(result.getData().getClipData().getItemAt(i).getUri());
                                }
                            } else {
                                path.add(result.getData().getData());
                            }
                            firebaseStorageViewModel.uploadMultipleImages(getContext(), user.getUid(), path);

                            firebaseStorageViewModel.getRemoteFileModelMutableLiveData().observe(getViewLifecycleOwner(), remoteFileModel -> {

                                Calendar cal = Calendar.getInstance();
                                long currentTime = cal.getTimeInMillis();

                                if (binding.replyMsg.getText().toString().trim().equals("")) {
                                    message = new Message(remoteFileModel.getUrl(), remoteFileModel.getFilename(), user.getUid(), senderName, currentTime);
                                } else {
                                    message = new Message(remoteFileModel.getUrl(), remoteFileModel.getFilename(), user.getUid(), senderName, binding.replyMsg.getText().toString(), binding.replyNameTxts.getText().toString(), replyUId, replyID, currentTime);
                                }
                                chatsViewModel.submitMessage(message, classID);
                                chatsViewModel.getMessageMutableLiveData().observe(getViewLifecycleOwner(), message1 -> {
                                    binding.messageBox.setText("");
                                    binding.replyHolder.setVisibility(View.GONE);
                                    binding.replyMsg.setText("");
                                    binding.replyNameTxts.setText("");
                                    replyID = null;
                                    replyUId = null;
                                });
                            });
                            firebaseStorageViewModel.getDatabaseErrorMutableLiveData().observe(getViewLifecycleOwner(), s -> snackbarTop.showSnackBar(s, false));
                        }
                    }
                }
            });

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menu.clear();
        menuInflater.inflate(R.menu.single_search_menu, menu);

        MenuItem searchViewItem
                = menu.findItem(R.id.app_bar_search);
        SearchView searchView
                = (SearchView) MenuItemCompat
                .getActionView(searchViewItem);

        searchView.setOnSearchClickListener(view -> chatsViewModel.getAllMessages(classID, adapter));
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        filteredDataList = filter(dataList, newText);
                        adapter.setFilter(filteredDataList);
                        enabled = true;
                        return false;
                    }
                });
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {

        return false;
    }

    private ArrayList<Message> filter(List<Message> dataList, String newTexts) {
        if (dataList != null) {
            newText = newTexts.toLowerCase();
            String messages, name, replyMessage;
            filteredDataList = new ArrayList<>();
            for (Message dataFromDataList : dataList) {

                if (dataFromDataList.getReplymsg() != null) {

                    if (dataFromDataList.getMessage() != null) {
                        messages = dataFromDataList.getMessage().toLowerCase();
                        name = dataFromDataList.getSenderName().toLowerCase();
                        replyMessage = dataFromDataList.getReplymsg().toLowerCase();
                        if (messages.contains(newText) || name.contains(newText) || replyMessage.contains(newTexts)) {
                            filteredDataList.add(dataFromDataList);
                        }
                    } else {
                        name = dataFromDataList.getSenderName().toLowerCase();
                        replyMessage = dataFromDataList.getReplymsg().toLowerCase();
                        if (name.contains(newText) || replyMessage.contains(newText)) {
                            filteredDataList.add(dataFromDataList);
                        }
                    }
                } else {
                    if (dataFromDataList.getMessage() != null) {
                        messages = dataFromDataList.getMessage().toLowerCase();
                        name = dataFromDataList.getSenderName().toLowerCase();
                        if (messages.contains(newText) || name.contains(newText)) {
                            filteredDataList.add(dataFromDataList);
                        }
                    } else {
                        name = dataFromDataList.getSenderName().toLowerCase();
                        if (name.contains(newText)) {
                            filteredDataList.add(dataFromDataList);
                        }
                    }
                }
            }
            if (enabled) {
                if (filteredDataList.isEmpty()) {
                    chatsViewModel.getAllMessages(classID, adapter);
                }
            }
        }
        return filteredDataList;
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
