package com.reiserx.nimbleq.Adapters.Doubts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.reiserx.nimbleq.Activities.Feedbacks.RateAndFeedbackActivity;
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

        if (!model.getTEACHER_UID().equals(FirebaseAuth.getInstance().getUid())) {
            holder.binding.getRoot().setOnClickListener(view -> {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle(context.getString(R.string.rate_this_user));
                alert.setMessage(context.getString(R.string.rate_this_user_msg));
                alert.setPositiveButton(context.getString(R.string.rate), (dialogInterface, i) -> {
                    rateTeacher(model.getTEACHER_UID(), model.getTeacherName());
                });
                alert.setNegativeButton(context.getString(R.string.cancel), null);
                alert.show();
            });
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

    void rateTeacher(String uid, String token) {
        Intent intent = new Intent(context, RateAndFeedbackActivity.class);
        intent.putExtra("id", 2);
        intent.putExtra("teacherID", uid);
        intent.putExtra("Message", context.getString(R.string.feedback_msg_1));
        intent.putExtra("userID", FirebaseAuth.getInstance().getUid());
        intent.putExtra("token", token);
        context.startActivity(intent);
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