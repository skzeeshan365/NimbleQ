package com.reiserx.nimbleq.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.reiserx.nimbleq.Adapters.Administration.HomeAdapter;
import com.reiserx.nimbleq.Models.RatingModel;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.databinding.FeedbackListLayoutBinding;
import com.reiserx.nimbleq.databinding.LtHomeListAdminBinding;

import java.util.List;

public class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.RatingsViewHolder> {

    Context context;
    List<RatingModel> data;

    public void setData(List<RatingModel> data) {
        this.data = data;
    }

    public RatingsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RatingsAdapter.RatingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.feedback_list_layout, parent, false);
        return new RatingsAdapter.RatingsViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull RatingsAdapter.RatingsViewHolder holder, int position) {
        RatingModel model = data.get(position);

        holder.binding.ratingTitle.setText(model.getName());
        holder.binding.timestamp.setText(TimeAgo.using(model.getTimeStamp()));
        holder.binding.ratingBar2.setRating(model.getRating());

        if (model.getFeedback() != null)
            holder.binding.ratingData.setContent(model.getFeedback());
    }

    @Override
    public int getItemCount() {
        if (data == null)
            return 0;
        else
            return data.size();
    }

    public class RatingsViewHolder extends RecyclerView.ViewHolder {

        FeedbackListLayoutBinding binding;

        public RatingsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = FeedbackListLayoutBinding.bind(itemView);
        }
    }
}