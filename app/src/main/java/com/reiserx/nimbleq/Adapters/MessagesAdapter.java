package com.reiserx.nimbleq.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.Message;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.ViewModels.ChatsViewModel;
import com.reiserx.nimbleq.databinding.ItemReceiveBinding;
import com.reiserx.nimbleq.databinding.ItemReceiveImageBinding;
import com.reiserx.nimbleq.databinding.ItemSendBinding;
import com.reiserx.nimbleq.databinding.ItemSendImageBinding;
import com.vdurmont.emoji.EmojiManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MessagesAdapter extends RecyclerView.Adapter {

    Context context;
    List<Message> messages;
    RecyclerView recyclerView;
    String room;
    String uid, rec_uid;
    ChatsViewModel chatsViewModel;
    MessagesAdapter adapter;

    final int ITEM_SENT_MESSAGE = 1;
    final int ITEM_RECEIVE_MESSAGE = 2;
    final int ITEM_SENT_IMAGE = 3;
    final int ITEM_RECEIVE_IMAGE = 4;

    public void setData(List<Message> messages) {
        this.messages = messages;
    }

    public MessagesAdapter(Context context, RecyclerView recyclerView, String room, String uid, ChatsViewModel chatsViewModel) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.room = room;
        this.uid = uid;
        this.rec_uid = rec_uid;
        this.chatsViewModel = chatsViewModel;
    }

    public void addData(Message message) {
        Message msg = messages.get(messages.size() - 1);
        if (!msg.getMessageId().equals(message.getMessageId())) {
            messages.add(message);
            notifyItemInserted(messages.size());
        }
    }

    public void addDataAt0(Message message) {
        Message msg = messages.get(messages.size() - 1);
        if (!msg.getMessageId().equals(message.getMessageId())) {
            messages.add(0, message);
            notifyItemInserted(0);
        }
    }

    public List<Message> getList() {
        return messages;
    }

    public Message getLastItem() {
        Message message = messages.get(0);
        Log.d(CONSTANTS.TAG, message.getMessage());
        return message;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SENT_MESSAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_send, parent, false);
            return new SentViewHolder(view);
        } else if (viewType == ITEM_SENT_IMAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_send_image, parent, false);
            return new SentViewHolderImage(view);
        } else if (viewType == ITEM_RECEIVE_MESSAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receive, parent, false);
            return new ReceiverViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receive_image, parent, false);
            return new ReceiverViewHolderImage(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (Objects.equals(FirebaseAuth.getInstance().getUid(), message.getSenderId())) {
            if (message.getImageUrl() == null) {
                return ITEM_SENT_MESSAGE;
            } else return ITEM_SENT_IMAGE;
        } else {
            if (message.getImageUrl() == null) {
                return ITEM_RECEIVE_MESSAGE;
            } else return ITEM_RECEIVE_IMAGE;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (holder.getClass() == SentViewHolder.class) {
            SentViewHolder viewHolder = (SentViewHolder) holder;

            Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_recycler_item_show);
            viewHolder.itemView.startAnimation(animation);

            AlphaAnimation aa = new AlphaAnimation(0.1f, 1.0f);
            aa.setDuration(400);

            viewHolder.binding.timeSent.setText(message.getTimeStamp());
            viewHolder.binding.message.setText(message.getMessage());

            if (message.getReplymsg() != null && message.getReplyuid() != null && message.getReplyid() != null) {
                viewHolder.binding.replyName.setVisibility(View.VISIBLE);
                viewHolder.binding.replyMsgAdapter.setVisibility(View.VISIBLE);
                viewHolder.binding.replyMsgAdapter.setText(message.getReplymsg());
                viewHolder.binding.replyMsgAdapter.setOnClickListener(view -> clickReply(message.getReplyid()));
                viewHolder.binding.replyName.setOnClickListener(view -> clickReply(message.getReplyid()));
                if (message.getReplyuid().equals(uid)) {
                    viewHolder.binding.replyName.setText("me");
                } else viewHolder.binding.replyName.setText(message.getReplyname());
            } else {
                viewHolder.binding.replyName.setVisibility(View.GONE);
                viewHolder.binding.replyMsgAdapter.setVisibility(View.GONE);
            }
            if (message.getMessage() != null && !message.getMessage().equals("This message was deleted")) {
                viewHolder.itemView.setOnLongClickListener(view -> {
                    deleteMessage(message, viewHolder.getAbsoluteAdapterPosition());
                    return false;
                });
            }
            if (EmojiManager.isOnlyEmojis(message.getMessage())) {
                viewHolder.binding.message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
            } else viewHolder.binding.message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        } else if (holder.getClass() == ReceiverViewHolder.class) {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;

            viewHolder.binding.timeRec.setText(message.getTimeStamp());
            viewHolder.binding.messageReceive.setText(message.getMessage());
            viewHolder.binding.username.setText(message.getSenderName());

            if (viewHolder.binding.messageReceive.getText().toString().equals("")) {
                viewHolder.binding.messageReceive.setText(message.getMessage());
            }
            if (EmojiManager.isOnlyEmojis(message.getMessage())) {
                viewHolder.binding.messageReceive.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
            } else viewHolder.binding.messageReceive.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            if (message.getReplymsg() != null && message.getReplyuid() != null && message.getReplyid() != null) {
                viewHolder.binding.replyMsgAdapter.setVisibility(View.VISIBLE);
                viewHolder.binding.replyName.setVisibility(View.VISIBLE);
                viewHolder.binding.replyMsgAdapter.setText(message.getReplymsg());
                viewHolder.binding.replyMsgAdapter.setOnClickListener(view -> clickReply(message.getReplyid()));
                viewHolder.binding.replyName.setOnClickListener(view -> clickReply(message.getReplyid()));

                if (message.getReplyuid().equals(uid)) {
                    viewHolder.binding.replyName.setText("me");
                } else viewHolder.binding.replyName.setText(message.getReplyname());
            } else {
                viewHolder.binding.replyMsgAdapter.setVisibility(View.GONE);
                viewHolder.binding.replyName.setVisibility(View.GONE);
            }
        } else if (holder.getClass() == SentViewHolderImage.class) {
            SentViewHolderImage viewHolder = (SentViewHolderImage) holder;
            Glide.with(context)
                    .load(message.getImageUrl())
                    .thumbnail(0.01f)
                    .into(viewHolder.binding.img);
            viewHolder.binding.img.setOnClickListener(view -> {

            });
            if (message.getReplymsg() != null && message.getReplyuid() != null && message.getReplyid() != null) {
                viewHolder.binding.replyName.setVisibility(View.VISIBLE);
                viewHolder.binding.replyMsgAdapter.setVisibility(View.VISIBLE);
                viewHolder.binding.replyMsgAdapter.setText(message.getReplymsg());
                viewHolder.binding.replyMsgAdapter.setOnClickListener(view -> clickReply(message.getReplyid()));
                viewHolder.binding.replyName.setOnClickListener(view -> clickReply(message.getReplyid()));

                if (message.getReplyuid().equals(uid)) {
                    viewHolder.binding.replyName.setText("me");
                } else viewHolder.binding.replyName.setText(message.getReplyname());
            } else {
                viewHolder.binding.replyName.setVisibility(View.GONE);
                viewHolder.binding.replyMsgAdapter.setVisibility(View.GONE);
            }
        } else if (holder.getClass() == ReceiverViewHolderImage.class) {
            ReceiverViewHolderImage viewHolder = (ReceiverViewHolderImage) holder;
            Glide.with(context)
                    .load(message.getImageUrl())
                    .into(viewHolder.binding.imgRec);
            viewHolder.binding.imgRec.setOnClickListener(view -> {
            });
            if (message.getReplymsg() != null && message.getReplyuid() != null && message.getReplyid() != null) {
                viewHolder.binding.replyMsgAdapter.setVisibility(View.VISIBLE);
                viewHolder.binding.replyName.setVisibility(View.VISIBLE);
                viewHolder.binding.replyMsgAdapter.setText(message.getReplymsg());
                viewHolder.binding.replyMsgAdapter.setOnClickListener(view -> clickReply(message.getReplyid()));
                viewHolder.binding.replyName.setOnClickListener(view -> clickReply(message.getReplyid()));

                if (message.getReplyuid().equals(uid)) {
                    viewHolder.binding.replyName.setText("me");
                } else viewHolder.binding.replyName.setText(message.getReplyname());
            } else {
                viewHolder.binding.replyMsgAdapter.setVisibility(View.GONE);
                viewHolder.binding.replyName.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setAdapter(MessagesAdapter adapter) {
        this.adapter = adapter;
    }

    public class SentViewHolder extends RecyclerView.ViewHolder {

        ItemSendBinding binding;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSendBinding.bind(itemView);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        ItemReceiveBinding binding;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceiveBinding.bind(itemView);
        }
    }

    public class SentViewHolderImage extends RecyclerView.ViewHolder {

        ItemSendImageBinding binding;

        public SentViewHolderImage(@NonNull View itemView) {
            super(itemView);
            binding = ItemSendImageBinding.bind(itemView);
        }
    }

    public class ReceiverViewHolderImage extends RecyclerView.ViewHolder {

        ItemReceiveImageBinding binding;

        public ReceiverViewHolderImage(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceiveImageBinding.bind(itemView);
        }
    }

    public int getTargetPosition(String messageID) {
        Message msg;
        for (int i = 0; i < messages.size() - 1; i++) {
            msg = messages.get(i);
            if (msg.getMessageId().equals(messageID)) {
                return i;
            }
        }
        return getTargetPosition2(messageID);
    }

    public int getTargetPosition2(String messageID) {
        Message msg;

        chatsViewModel.getAllMessages(room, adapter);

        for (int i = 0; i < messages.size() - 1; i++) {
            msg = messages.get(i);
            if (msg.getMessageId().equals(messageID)) {
                return i;
            }
        }
        return -1;
    }

    private void clickReply(String replyID) {
        int i = getTargetPosition(replyID);
        if (i != -1) {
            recyclerView.smoothScrollToPosition(i);
        }
    }

    public void deleteMessage(Message message, int pos) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Delete message");
        alert.setMessage("Are you sure you want to delete this message");
        alert.setPositiveButton("delete", (dialogInterface, i) -> {
            Calendar c = Calendar.getInstance();
            String senttime = new SimpleDateFormat("hh:mm a").format(c.getTime());
            HashMap<String, Object> map = new HashMap<>();
            map.put("message", "This message was deleted");
            map.put("timeStamp", senttime);
            map.put("replymsg", null);
            map.put("imageUrl", null);
            map.put("replyuid", null);
            map.put("replyid", null);
            map.put("replyname", null);

            CollectionReference reference = FirebaseFirestore.getInstance().collection("Main").document("Class").collection("Message").document(room).collection("Groupchat");
            reference.document(message.getMessageId()).update(map).addOnSuccessListener(unused -> {
                Message message1 = new Message("This message was deleted", message.getSenderId(), message.getSenderName(), senttime, null, null, null, null, message.getQueryStamp());
                message1.setMessageId(message.getMessageId());
                map.clear();
                messages.set(pos, message1);
                Log.d(CONSTANTS.TAG2, "deleted");
                adapter.notifyItemChanged(pos, message1);
            }).addOnFailureListener(e -> Log.d(CONSTANTS.TAG2, e.toString()));
        });

        alert.setNegativeButton("cancel", null);
        alert.show();
    }
}
