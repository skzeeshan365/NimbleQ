package com.reiserx.nimbleq.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reiserx.nimbleq.Activities.ClassActivity;
import com.reiserx.nimbleq.Models.classModel;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.databinding.ClassListLayoutBinding;

import java.util.List;

public class classListAdapter extends RecyclerView.Adapter<classListAdapter.UsersViewHolder> {

    Context context;
    List<classModel> users;


    public void setClassList(List<classModel> classModelList) {
        this.users = classModelList;
    }

    public classListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public classListAdapter.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.class_list_layout, parent, false);
        return new UsersViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull classListAdapter.UsersViewHolder holder, int position) {
        classModel model = users.get(position);

        holder.binding.className.setText(model.getClassName());
        holder.binding.subTopic.setText(model.getSubject() + " • " + model.getTopic());
        holder.binding.timeSlotTxt.setText(model.getTime_slot());
        holder.binding.teacherTxt.setText("Teacher ".concat(model.getTeacher_name()));

        holder.binding.classHolder.setOnClickListener(view -> {
            Intent intent = new Intent(context, ClassActivity.class);
            intent.putExtra("classID", model.getClassID());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (users == null)
            return 0;
        else
            return users.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        ClassListLayoutBinding binding;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ClassListLayoutBinding.bind(itemView);
        }
    }
}
