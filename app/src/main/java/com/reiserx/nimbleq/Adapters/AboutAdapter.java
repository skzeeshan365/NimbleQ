package com.reiserx.nimbleq.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.reiserx.nimbleq.Adapters.Administration.HomeAdapter;
import com.reiserx.nimbleq.Adapters.Announcements.announcementLinksAdapter;
import com.reiserx.nimbleq.Adapters.Doubts.DoubtsAdapter;
import com.reiserx.nimbleq.Models.AboutModelList;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.SharedPreferenceClass;
import com.reiserx.nimbleq.Utils.UserTypeClass;
import com.reiserx.nimbleq.databinding.AboutDataHolderBinding;
import com.reiserx.nimbleq.databinding.ListHeaderBinding;
import com.reiserx.nimbleq.databinding.LtHomeListAdminBinding;

import java.util.List;

public class AboutAdapter extends RecyclerView.Adapter {

    Context context;
    List<AboutModelList> data;

    final int HEADER_ITEM = 1;
    final int DATA_ITEM = 2;

    public AboutAdapter(Context context, List<AboutModelList> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADER_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_header, parent, false);
            return new AboutAdapter.HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.about_data_holder, parent, false);
            return new AboutAdapter.AboutViewHolder(view);
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AboutModelList model = data.get(position);

        if (holder.getClass() == AboutAdapter.AboutViewHolder.class) {
            AboutAdapter.AboutViewHolder viewHolder = (AboutAdapter.AboutViewHolder) holder;

            viewHolder.binding.textView25.setText(model.getName());

            viewHolder.binding.getRoot().setOnClickListener(view -> {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getData()));
                context.startActivity(sendIntent);
            });

        } else if (holder.getClass() == AboutAdapter.HeaderViewHolder.class) {
            AboutAdapter.HeaderViewHolder viewHolder = (AboutAdapter.HeaderViewHolder) holder;

            viewHolder.binding.textView40.setText(model.getName());
        }
    }

    @Override
    public int getItemViewType(int position) {
        AboutModelList aboutModelList = data.get(position);
        if (aboutModelList.isHeader())
            return HEADER_ITEM;
        else
            return DATA_ITEM;
    }

    @Override
    public int getItemCount() {
        if (data == null)
            return 0;
        else
            return data.size();
    }

    public class AboutViewHolder extends RecyclerView.ViewHolder {

        AboutDataHolderBinding binding;

        public AboutViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = AboutDataHolderBinding.bind(itemView);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        ListHeaderBinding binding;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ListHeaderBinding.bind(itemView);
        }
    }
}