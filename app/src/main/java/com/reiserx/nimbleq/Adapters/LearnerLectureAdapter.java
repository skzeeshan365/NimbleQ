package com.reiserx.nimbleq.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.nimbleq.Models.LecturesModel;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.SnackbarTop;
import com.reiserx.nimbleq.databinding.LecturesCustomListLayoutBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LearnerLectureAdapter extends RecyclerView.Adapter<LearnerLectureAdapter.LecturesViewHolder> {

    Context context;
    List<LecturesModel> data;
    SnackbarTop snackbarTop;
    String classID;

    public void setData(List<LecturesModel> data) {
        this.data = data;
    }

    public LearnerLectureAdapter(Context context, SnackbarTop snackbarTop, String classID) {
        this.context = context;
        this.snackbarTop = snackbarTop;
        this.classID = classID;
    }

    @NonNull
    @Override
    public LearnerLectureAdapter.LecturesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lectures_custom_list_layout, parent, false);
        return new LearnerLectureAdapter.LecturesViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LearnerLectureAdapter.LecturesViewHolder holder, int position) {
        LecturesModel model = data.get(position);

        holder.binding.checkBox2.setText(context.getString(R.string.lecture)+" "+model.getLecture());
        holder.binding.checkBox2.setChecked(model.isStatus());

        holder.binding.checkBox2.setOnClickListener(view -> {
            boolean status = !model.isStatus();
            Map<String, Object> map = new HashMap<>();
            map.put("status", status);
            model.getDatabaseReference().updateChildren(map);
        });
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        else
            return 0;
    }

    public class LecturesViewHolder extends RecyclerView.ViewHolder {

        LecturesCustomListLayoutBinding binding;

        public LecturesViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = LecturesCustomListLayoutBinding.bind(itemView);
        }
    }
}