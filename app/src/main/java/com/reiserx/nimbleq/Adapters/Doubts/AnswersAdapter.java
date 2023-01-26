package com.reiserx.nimbleq.Adapters.Doubts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.FirebaseDatabase;
import com.reiserx.nimbleq.Adapters.Announcements.announcementLinksAdapter;
import com.reiserx.nimbleq.Models.Doubts.AnswerModel;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.databinding.AnswerListLayoutBinding;

import java.util.List;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.UsersViewHolder> {

    private List<AnswerModel> answerModelList;
    Context context;
    FirebaseDatabase database;

    public void setAnswerModelList(List<AnswerModel> answerModelList) {
        this.answerModelList = answerModelList;
    }

    public AnswersAdapter(Context context) {
        this.context = context;
        database = FirebaseDatabase.getInstance();
    }


    @NonNull
    @Override
    public AnswersAdapter.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_list_layout, parent, false);
        return new AnswersAdapter.UsersViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull AnswersAdapter.UsersViewHolder holder, int position) {
        AnswerModel model = answerModelList.get(position);

        holder.binding.answerText.setText(model.getAnswer());
        holder.binding.teacherName.setText(model.getTeacherName());

        holder.binding.getRoot().setOnClickListener(view -> {

        });

        holder.binding.timestampAns.setText(TimeAgo.using(model.getTimeStamp()));

        holder.binding.recycler.setHasFixedSize(true);
        holder.binding.recycler.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        announcementLinksAdapter childAdapter = new announcementLinksAdapter(context);
        if (model.getLinkModels() != null) {
            childAdapter.setChildItemList(model.getLinkModels());
        }
        holder.binding.recycler.setAdapter(childAdapter);
        childAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (answerModelList != null) {
            return answerModelList.size();
        } else {
            return 0;
        }

    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        AnswerListLayoutBinding binding;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = AnswerListLayoutBinding.bind(itemView);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilter(List<AnswerModel> FilteredDataList) {
        answerModelList = FilteredDataList;
        notifyDataSetChanged();
    }
}