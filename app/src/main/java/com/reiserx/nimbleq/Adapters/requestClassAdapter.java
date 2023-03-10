package com.reiserx.nimbleq.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.reiserx.nimbleq.Activities.ClassActivity;
import com.reiserx.nimbleq.Activities.CreateClass;
import com.reiserx.nimbleq.Models.ClassRequestModel;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.SnackbarTop;
import com.reiserx.nimbleq.databinding.RequestClassListLayoutBinding;

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

        if (model.getStudentID().equals(userID)) {
            holder.binding.status.setVisibility(View.VISIBLE);
            holder.binding.deleteImg.setVisibility(View.VISIBLE);
            if (model.isAccepted() != null) {
                holder.binding.status.setText(context.getString(R.string.class_alloted));
                holder.binding.status.setTextColor(context.getColor(R.color.GREEN));
                holder.binding.reqClassHolder.setOnClickListener(view -> {
                    Intent intent = new Intent(context, ClassActivity.class);
                    intent.putExtra("classID", model.isAccepted());
                    context.startActivity(intent);
                });
            } else {
                holder.binding.status.setText(context.getString(R.string.pending));
                holder.binding.status.setTextColor(context.getColor(R.color.RED));
            }
        } else {
            holder.binding.deleteImg.setVisibility(View.GONE);
            holder.binding.status.setVisibility(View.GONE);

            holder.binding.reqClassHolder.setOnClickListener(view -> {
                Gson gson = new Gson();
                Intent intent = new Intent(context, CreateClass.class);
                intent.putExtra("request", gson.toJson(model));
                intent.putExtra("requestMode", true);
                intent.putExtra("subject", model.getSubject());
                intent.putExtra("topic", model.getTopic());
                intent.putExtra("slot", model.getTimeSlot());
                context.startActivity(intent);
            });
        }
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