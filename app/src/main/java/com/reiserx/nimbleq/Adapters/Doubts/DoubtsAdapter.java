package com.reiserx.nimbleq.Adapters.Doubts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.reiserx.nimbleq.Adapters.Announcements.announcementLinksAdapter;
import com.reiserx.nimbleq.Models.Doubts.DoubtsModel;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.SharedPreferenceClass;
import com.reiserx.nimbleq.Utils.UserTypeClass;
import com.reiserx.nimbleq.databinding.DoubtsLayoutBinding;
import com.reiserx.nimbleq.databinding.ListLoadingLayoutBinding;

import java.util.List;

public class DoubtsAdapter extends RecyclerView.Adapter {

    private List<DoubtsModel> doubtsModelList;
    Context context;
    NavController  navHostFragment;
    boolean loading;

    final int LOADING_ITEM = 1;
    final int DATA_ITEM = 2;

    public void setParentItemList(List<DoubtsModel> doubtsModelList) {
        this.doubtsModelList = doubtsModelList;
    }

    public DoubtsAdapter(Context context, NavController navHostFragment, boolean loading) {
        this.context = context;
        this.navHostFragment = navHostFragment;
        this.loading = loading;
    }

    public void addData(DoubtsModel doubtsModelList1) {
        DoubtsModel doubtsModel = doubtsModelList.get(doubtsModelList.size()-1);
        if (!doubtsModel.getId().equals(doubtsModelList1.getId())) {
            doubtsModelList.add(doubtsModelList1);
            notifyItemInserted(doubtsModelList.size()-1);
            loading = false;
        } else
            loading = true;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == LOADING_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_loading_layout, parent, false);
            return new DoubtsAdapter.LoadingViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doubts_layout, parent, false);
            return new DoubtsAdapter.UsersViewHolder(view);
        }
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DoubtsModel model = doubtsModelList.get(position);

        if (holder.getClass() == DoubtsAdapter.UsersViewHolder.class) {
            DoubtsAdapter.UsersViewHolder viewHolder = (DoubtsAdapter.UsersViewHolder) holder;

            viewHolder.binding.dSubject.setText(model.getSubject().concat(" • ").concat(model.getTopic()));
            viewHolder.binding.descOneLine.setText(model.getShort_desc());
            viewHolder.binding.dData.setText(model.getLong_desc());
            viewHolder.binding.answerCountTxt.setText(model.getAnswerCount()+" "+context.getString(R.string.answers_lower));

            viewHolder.binding.elementHolder.setOnClickListener(view -> {
                SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(context);
                sharedPreferenceClass.setDoubtInfo(model);
                navHostFragment.navigate(R.id.action_FirstFragment_to_ViewDoubtsFragment);
            });

            viewHolder.binding.descOneLine.setOnClickListener(view -> {
                SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(context);
                sharedPreferenceClass.setDoubtInfo(model);
                navHostFragment.navigate(R.id.action_FirstFragment_to_ViewDoubtsFragment);
            });

            viewHolder.binding.dData.setOnClickListener(view -> {
                SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(context);
                sharedPreferenceClass.setDoubtInfo(model);
                navHostFragment.navigate(R.id.action_FirstFragment_to_ViewDoubtsFragment);
            });

            viewHolder.binding.timestamp.setText(TimeAgo.using(model.getTimeStamp()));

            viewHolder.binding.recycler.setHasFixedSize(true);
            viewHolder.binding.recycler.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
            announcementLinksAdapter childAdapter = new announcementLinksAdapter(context);
            if (model.getLinkModels() != null) {
                childAdapter.setChildItemList(model.getLinkModels());
            }
            viewHolder.binding.recycler.setAdapter(childAdapter);
            childAdapter.notifyDataSetChanged();

        } else if (holder.getClass() == DoubtsAdapter.LoadingViewHolder.class) {
            DoubtsAdapter.LoadingViewHolder viewHolder = (DoubtsAdapter.LoadingViewHolder) holder;

            viewHolder.binding.dSubject.setText(model.getSubject().concat(" • ").concat(model.getTopic()));
            viewHolder.binding.descOneLine.setText(model.getShort_desc());
            viewHolder.binding.dData.setContent(model.getLong_desc());
            viewHolder.binding.answerCountTxt.setText(model.getAnswerCount()+" "+context.getString(R.string.answers_lower));

            viewHolder.binding.elementHolder.setOnClickListener(view -> {
                SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(context);
                sharedPreferenceClass.setDoubtInfo(model);
                navHostFragment.navigate(R.id.action_FirstFragment_to_ViewDoubtsFragment);
            });

            viewHolder.binding.descOneLine.setOnClickListener(view -> {
                SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(context);
                sharedPreferenceClass.setDoubtInfo(model);
                navHostFragment.navigate(R.id.action_FirstFragment_to_ViewDoubtsFragment);
            });

            viewHolder.binding.dData.setOnClickListener(view -> {
                SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(context);
                sharedPreferenceClass.setDoubtInfo(model);
                navHostFragment.navigate(R.id.action_FirstFragment_to_ViewDoubtsFragment);
            });

            viewHolder.binding.timestamp.setText(TimeAgo.using(model.getTimeStamp()));

            viewHolder.binding.recycler.setHasFixedSize(true);
            viewHolder.binding.recycler.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
            announcementLinksAdapter childAdapter = new announcementLinksAdapter(context);
            if (model.getLinkModels() != null) {
                childAdapter.setChildItemList(model.getLinkModels());
            }
            viewHolder.binding.recycler.setAdapter(childAdapter);
            childAdapter.notifyDataSetChanged();

            if (loading) {
                if (doubtsModelList.size() > 10)
                    viewHolder.binding.progressBar5.setVisibility(View.VISIBLE);
                else
                    viewHolder.binding.progressBar5.setVisibility(View.GONE);
            } else
                viewHolder.binding.progressBar5.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        UserTypeClass userTypeClass = new UserTypeClass(context);
            if (position == doubtsModelList.size() - 1 && !userTypeClass.isUserLearner())
                return LOADING_ITEM;
            else
                return DATA_ITEM;
    }

    @Override
    public int getItemCount() {
        if (doubtsModelList != null) {
            return doubtsModelList.size();
        } else {
            return 0;
        }
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        DoubtsLayoutBinding binding;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DoubtsLayoutBinding.bind(itemView);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        ListLoadingLayoutBinding binding;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ListLoadingLayoutBinding.bind(itemView);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilter(List<DoubtsModel> FilteredDataList) {
        doubtsModelList = FilteredDataList;
        notifyDataSetChanged();
    }
}