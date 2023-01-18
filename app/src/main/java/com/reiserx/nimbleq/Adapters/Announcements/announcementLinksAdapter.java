package com.reiserx.nimbleq.Adapters.Announcements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reiserx.nimbleq.Models.Announcements.linkModel;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.FileOperations;
import com.reiserx.nimbleq.databinding.FileViewLayoutBinding;

import java.util.Collections;
import java.util.List;

public class announcementLinksAdapter extends RecyclerView.Adapter<announcementLinksAdapter.UsersViewHolder> {

    private List<linkModel> childItemList;
    Context context;

    public announcementLinksAdapter(Context context) {
        this.context = context;
    }

    public void setChildItemList(List<linkModel> childItemList) {
        this.childItemList = childItemList;
        if (childItemList != null && !childItemList.isEmpty())
            this.childItemList.removeAll(Collections.singleton(null));
    }

    @NonNull
    @Override
    public announcementLinksAdapter.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_view_layout, parent, false);
        context = parent.getContext();
        return new announcementLinksAdapter.UsersViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull announcementLinksAdapter.UsersViewHolder holder, int position) {
        linkModel childItem = childItemList.get(position);

        holder.binding.textView33.setTextSize(14);
        holder.binding.textView33.setText(childItem.getFilename());


        holder.binding.imageView16.setVisibility(View.GONE);
        holder.binding.progressBar4.setVisibility(View.GONE);

        holder.binding.fileHolder.setOnClickListener(view -> {
            FileOperations fileOperations = new FileOperations(context.getApplicationContext());
            fileOperations.checkFile(childItem.getLink(), childItem.getFilename(), true);
        });
    }

    @Override
    public int getItemCount() {
        if (childItemList != null) {
            return childItemList.size();
        } else {
            return 0;
        }
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        FileViewLayoutBinding binding;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = FileViewLayoutBinding.bind(itemView);
        }
    }
}