package com.reiserx.nimbleq.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.reiserx.nimbleq.Activities.CreateClass;
import com.reiserx.nimbleq.Models.ClassRequestModel;
import com.reiserx.nimbleq.Models.classModel;
import com.reiserx.nimbleq.Models.subjectAndTimeSlot;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.SnackbarTop;
import com.reiserx.nimbleq.Utils.dialogs;
import com.reiserx.nimbleq.databinding.RequestClassListLayoutBinding;
import com.reiserx.nimbleq.databinding.SlotsLayoutBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class requestClassAdapter extends RecyclerView.Adapter<requestClassAdapter.UsersViewHolder> {

    Context context;
    List<ClassRequestModel> classRequestModels;
    SnackbarTop snackbarTop;
    String userID;

    public void setData(List<ClassRequestModel> list) {
        classRequestModels = list;
    }


    public requestClassAdapter(Context context, View view, String userID) {
        this.context = context;
        this.snackbarTop = new SnackbarTop(view);
        this.userID = userID;
    }

    @NonNull
    @Override
    public requestClassAdapter.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.request_class_list_layout, parent, false);
        return new requestClassAdapter.UsersViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull requestClassAdapter.UsersViewHolder holder, int position) {
        ClassRequestModel model = classRequestModels.get(position);

        holder.binding.reqSubTxt.setText(model.getTimeSlot());
        holder.binding.reqDescTxt.setText(model.getSubject().concat(" • ".concat(model.getTopic())));

        if (model.isAccepted()) {
            holder.binding.status.setText("Class allotted");
            holder.binding.status.setTextColor(context.getColor(R.color.GREEN));
        } else {
            holder.binding.status.setText("Pending");
            holder.binding.status.setTextColor(context.getColor(R.color.RED));
        }

        if (model.getStudentID().equals(userID)) {
            holder.binding.deleteImg.setVisibility(View.VISIBLE);
        } else holder.binding.deleteImg.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return classRequestModels.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        RequestClassListLayoutBinding binding;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RequestClassListLayoutBinding.bind(itemView);
        }
    }
}