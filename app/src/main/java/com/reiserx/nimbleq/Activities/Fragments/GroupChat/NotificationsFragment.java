package com.reiserx.nimbleq.Activities.Fragments.GroupChat;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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
import com.reiserx.nimbleq.Utils.SnackbarTop;
import com.reiserx.nimbleq.ViewModels.ChatsViewModel;
import com.reiserx.nimbleq.ViewModels.UserDataViewModel;
import com.reiserx.nimbleq.databinding.FragmentNotificationsBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    FirebaseAuth auth;
    FirebaseUser user;

    private String replyUId, replyID, senderName;

    ChatsViewModel chatsViewModel;

    String classID;

    LinearLayoutManager layoutManager;
    MessagesAdapter adapter;

    Message message;

    SnackbarTop snackbarTop;

    int pastVisiblesItems, totalItemCount, lastItem;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        chatsViewModel = new ViewModelProvider(this).get(ChatsViewModel.class);
        UserDataViewModel userDataViewModel = new ViewModelProvider(this).get(UserDataViewModel.class);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        snackbarTop = new SnackbarTop(root);

        classID = requireActivity().getIntent().getExtras().getString("classID");

        userDataViewModel.getUsername(user.getUid());
        userDataViewModel.getUserName().observe(getViewLifecycleOwner(), s -> senderName = s);
        userDataViewModel.getDatabaseErrorMutableLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                snackbarTop.showSnackBar(s, false);
            }
        });

        binding.sendButton.setOnClickListener(view -> sendMessage());

        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);
        adapter = new MessagesAdapter(getContext(), binding.recyclerView, classID, user.getUid(), chatsViewModel);
        adapter.setAdapter(adapter);
        binding.replyHolder.setVisibility(View.GONE);

        getMessages();

        scrollListener();
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
            Calendar c = Calendar.getInstance();
            String senttime = new SimpleDateFormat("hh:mm a").format(c.getTime());

            Calendar cal = Calendar.getInstance();
            long currentTime = cal.getTimeInMillis();

            if (binding.replyMsg.getText().toString().trim().equals("")) {
                message = new Message(MessageTxt, user.getUid(), senderName, senttime, currentTime);
            } else {
                message = new Message(MessageTxt, user.getUid(), senderName, senttime, binding.replyMsg.getText().toString(), binding.replyNameTxt.getText().toString(), replyUId, replyID, currentTime);
            }

            chatsViewModel.submitMessage(message, classID);
            chatsViewModel.getMessageMutableLiveData().observe(getViewLifecycleOwner(), message1 -> {
                binding.messageBox.setText("");
                binding.replyHolder.setVisibility(View.GONE);
                binding.replyMsg.setText("");
                binding.replyNameTxt.setText("");
                replyID = null;
                replyUId = null;
            });
        } else Toast.makeText(getContext(), "Please type a message", Toast.LENGTH_SHORT).show();
    }

    private void getMessages() {
        chatsViewModel.getMessages(classID, 15);
        chatsViewModel.getMessageListMutableLiveData().observe(getViewLifecycleOwner(), messages -> {
            adapter.setData(messages);
            binding.recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            swipeController(messages);

            chatsViewModel.getLatestMessages(classID);
            chatsViewModel.getLatestMessageListMutableLiveData().observe(getViewLifecycleOwner(), message -> {
                adapter.addData(message);
                binding.recyclerView.setAdapter(adapter);

                if (adapter.getItemCount() > 50) {
                    chatsViewModel.getMessages(classID, 15);
                }
            });
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
                binding.replyNameTxt.setText("me");
                replyUId = user.getUid();
            } else {
                binding.replyNameTxt.setText(message.getReplyname());
                replyUId = message.getSenderId();
            }
            if (message.getImageUrl() != null) {
                binding.replyMsg.setText("Photo");
            } else {
                binding.replyMsg.setText(message.getMessage());
            }
            binding.imageView4.setOnClickListener(view -> {
                binding.replyHolder.setVisibility(View.GONE);
                binding.replyMsg.setText("");
                binding.replyNameTxt.setText("");
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
                            Log.d(CONSTANTS.TAG, "load message");
                            chatsViewModel.paginateMessages(classID, adapter);
                            lastItem = totalItemCount;

                        } else
                            Log.d(CONSTANTS.TAG, "loaded all messages");
                    }
                }
            }
        });
    }
}