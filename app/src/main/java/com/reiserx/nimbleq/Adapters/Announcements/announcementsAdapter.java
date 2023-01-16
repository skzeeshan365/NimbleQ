package com.reiserx.nimbleq.Adapters.Announcements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.exoplayer2.util.Log;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.Announcements.announcementsModel;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.databinding.AnnouncementsLayoutBinding;

import java.util.List;

public class announcementsAdapter extends RecyclerView.Adapter<announcementsAdapter.UsersViewHolder> {

    private List<announcementsModel> parentItemList;
    Context context;

    public void setParentItemList(List<announcementsModel> parentItemList) {
        this.parentItemList = parentItemList;
    }

    public announcementsAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public announcementsAdapter.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.announcements_layout, parent, false);
        return new announcementsAdapter.UsersViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull announcementsAdapter.UsersViewHolder holder, int position) {
        announcementsModel model = parentItemList.get(position);

        holder.binding.name.setText(model.getName());
        holder.binding.data.setContent(model.getInfo());

        Log.d(CONSTANTS.TAG, String.valueOf(model.getTimeStamp()));
        holder.binding.elementHolder.setOnClickListener(view -> {

        });

        holder.binding.timestamp.setText(TimeAgo.using(model.getTimeStamp()));

        holder.binding.recycler.setHasFixedSize(true);
        holder.binding.recycler.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        announcementLinksAdapter childAdapter = new announcementLinksAdapter();
        if (model.getLinkModels() != null) {
            childAdapter.setChildItemList(model.getLinkModels());
        }
        holder.binding.recycler.setAdapter(childAdapter);
        childAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (parentItemList != null) {
            return parentItemList.size();
        } else {
            return 0;
        }

    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        AnnouncementsLayoutBinding binding;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = AnnouncementsLayoutBinding.bind(itemView);
        }
    }

    public void setFilter(List<announcementsModel> FilteredDataList) {
        parentItemList = FilteredDataList;
        notifyDataSetChanged();
    }
}