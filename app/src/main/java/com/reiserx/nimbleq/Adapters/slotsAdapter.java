package com.reiserx.nimbleq.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;
import com.reiserx.nimbleq.Activities.ClassActivity;
import com.reiserx.nimbleq.Activities.CreateClass;
import com.reiserx.nimbleq.Models.classModel;
import com.reiserx.nimbleq.Models.subjectAndTimeSlot;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.SnackbarTop;
import com.reiserx.nimbleq.Utils.TopicSubscription;
import com.reiserx.nimbleq.Utils.UserTypeClass;
import com.reiserx.nimbleq.Utils.dialogs;
import com.reiserx.nimbleq.databinding.SlotsLayoutBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class slotsAdapter extends RecyclerView.Adapter<slotsAdapter.UsersViewHolder> {

    Context context;
    ArrayList<subjectAndTimeSlot> users;
    SnackbarTop snackbarTop;
    boolean isList;
    String userID;
    View view;

    public void setSlotListData(ArrayList<subjectAndTimeSlot> list) {
        users = list;
    }


    public slotsAdapter(Context context, ArrayList<subjectAndTimeSlot> users, View view, boolean isList, String userID) {
        this.context = context;
        this.users = users;
        this.view = view;
        this.snackbarTop = new SnackbarTop(view);
        this.isList = isList;
        this.userID = userID;
    }

    @NonNull
    @Override
    public slotsAdapter.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.slots_layout, parent, false);
        return new slotsAdapter.UsersViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull slotsAdapter.UsersViewHolder holder, int position) {
        subjectAndTimeSlot model = users.get(position);

        if (isList) {
            holder.binding.selectedImg.setVisibility(View.GONE);
            holder.binding.deleteImg.setVisibility(View.GONE);
            holder.binding.editImg.setVisibility(View.GONE);
            holder.binding.slotsSubTxt.setText(model.getTimeSlot());
            holder.binding.slotsDescTxt.setText(model.getSubject().concat(" ??? ").concat(model.getTopic()));

            holder.binding.slotHolder.setOnClickListener(view -> {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                snackbarTop.showSnackBar(context.getString(R.string.checking_class), true);
                CollectionReference collection = firestore.collection("Main").document("Class").collection("ClassInfo");

                Query query = collection
                        .whereEqualTo("teacher_info", userID)
                        .whereEqualTo("subject", model.getSubject())
                        .whereEqualTo("time_slot", model.getTimeSlot())
                        .whereEqualTo("topic", model.getTopic());

                query.get().addOnSuccessListener(task -> {
                    if (task == null || task.isEmpty()) {
                        Intent intent = new Intent(context, CreateClass.class);
                        intent.putExtra("subject", model.getSubject());
                        intent.putExtra("topic", model.getTopic());
                        intent.putExtra("slot", model.getTimeSlot());
                        context.startActivity(intent);
                    } else {
                        for (DocumentSnapshot document : task.getDocuments()) {
                            classModel classModel = document.toObject(com.reiserx.nimbleq.Models.classModel.class);
                            if (classModel != null) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                alert.setTitle(context.getString(R.string.class_already_exists));
                                alert.setMessage(context.getString(R.string.class_name1).concat(classModel.getClassName()) + "\n".concat(context.getString(R.string.do_you_want_to_open_it)));
                                alert.setPositiveButton(context.getString(R.string.open_class), (dialogInterface, i) -> {
                                    Intent intent = new Intent(context, ClassActivity.class);
                                    intent.putExtra("classID", document.getId());
                                    context.startActivity(intent);
                                });
                                alert.setNegativeButton(context.getString(R.string.create_class), (dialogInterface, i) -> {
                                    Intent intent = new Intent(context, CreateClass.class);
                                    intent.putExtra("subject", model.getSubject());
                                    intent.putExtra("topic", model.getTopic());
                                    intent.putExtra("slot", model.getTimeSlot());
                                    context.startActivity(intent);
                                });
                                alert.setNeutralButton(context.getString(R.string.cancel), null);
                                alert.show();
                            }
                        }
                    }
                });
            });
        } else {
            holder.binding.slotsSubTxt.setText(context.getString(R.string.slot2) + " ".concat(String.valueOf(position + 1)));
            if (model.getTimeSlot() != null)
                holder.binding.slotsDescTxt.setText(model.getTimeSlot().concat(" ??? ".concat(model.getSubject().concat(" ??? ".concat(model.getTopic())))));
            else
                holder.binding.slotsDescTxt.setText(model.getSubject().concat(" ??? ".concat(model.getTopic())));

            holder.binding.deleteImg.setOnClickListener(view -> {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle(context.getString(R.string.delete_slot));
                alert.setMessage(context.getString(R.string.delete_slot_msg));
                alert.setPositiveButton(context.getString(R.string.delete), (dialogInterface, i) -> {
                    FirebaseMessaging fcm = FirebaseMessaging.getInstance();
                    fcm.unsubscribeFromTopic(TopicSubscription.getTopicForSlot(model));

                    if (model.isCurrent()) {
                        model.getReference().limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                        subjectAndTimeSlot subjectAndTimeSlot = snapshot1.getValue(com.reiserx.nimbleq.Models.subjectAndTimeSlot.class);
                                        if (subjectAndTimeSlot != null) {
                                            HashMap<String, Object> map = new HashMap<>();
                                            map.put("current", true);
                                            if (snapshot1.getKey() != null)
                                                model.getReference().child(snapshot1.getKey()).updateChildren(map);
                                            if (model.getTimeSlot() == null) {
                                                SharedPreferences save = context.getSharedPreferences("subjectSlots", MODE_PRIVATE);
                                                SharedPreferences.Editor myEdit = save.edit();
                                                myEdit.putString("subject", subjectAndTimeSlot.getSubject());
                                                myEdit.putString("topic", subjectAndTimeSlot.getTopic());
                                                myEdit.apply();
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    model.getReference().child(model.getKey()).removeValue();
                    snackbarTop.showSnackBar(context.getString(R.string.slot_deleted), true);
                });
                alert.setNegativeButton(context.getString(R.string.cancel), null);
                alert.show();
            });

            holder.binding.editImg.setOnClickListener(view -> {
                dialogs dialogs = new dialogs(context, view);
                UserTypeClass userTypeClass = new UserTypeClass(context);

                if (userTypeClass.isUserLearner())
                    dialogs.updateSubjectForLearner(model, model.getReference().child(model.getKey()));
                else
                    dialogs.updateSubjectForTeacher(model, model.getReference().child(model.getKey()));
            });

            holder.binding.slotHolder.setOnClickListener(view -> model.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("current", false);
                            if (snapshot1.getKey() != null)
                                model.getReference().child(snapshot1.getKey()).updateChildren(map);
                        }
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("current", true);
                        model.getReference().child(model.getKey()).updateChildren(map);

                        if (model.getTimeSlot() == null) {
                            SharedPreferences save = context.getSharedPreferences("subjectSlots", MODE_PRIVATE);
                            SharedPreferences.Editor myEdit = save.edit();
                            myEdit.putString("subject", model.getSubject());
                            myEdit.putString("topic", model.getTopic());
                            myEdit.apply();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            }));

            if (model.isCurrent())
                holder.binding.selectedImg.setVisibility(View.VISIBLE);
            else
                holder.binding.selectedImg.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        SlotsLayoutBinding binding;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SlotsLayoutBinding.bind(itemView);
        }
    }
}