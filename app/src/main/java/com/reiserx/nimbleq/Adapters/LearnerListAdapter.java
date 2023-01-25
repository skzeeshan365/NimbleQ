package com.reiserx.nimbleq.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.reiserx.nimbleq.Adapters.Administration.UserListAdapter;
import com.reiserx.nimbleq.Models.Announcements.announcementsModel;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.SharedPreferenceClass;
import com.reiserx.nimbleq.databinding.LtHomeListAdminBinding;

import java.util.List;

public class LearnerListAdapter extends RecyclerView.Adapter<LearnerListAdapter.HomeViewHolder> {

    Context context;
    List<UserData> data;
    NavController navController;

    public void setData(List<UserData> data) {
        this.data = data;
    }

    public void clear() {
        if (data != null && !data.isEmpty())
            data.clear();
    }

    public List<UserData> getData() {
        return data;
    }

    public LearnerListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public LearnerListAdapter.HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lt_home_list_admin, parent, false);
        return new LearnerListAdapter.HomeViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull LearnerListAdapter.HomeViewHolder holder, int position) {
        UserData model = data.get(position);
        holder.binding.textView25.setText(model.getUserName());

        holder.binding.getRoot().setOnClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle(model.getUserName());
            String grade = context.getString(R.string.grade2)+model.getUserDetails().getGrade();
            String stateCity = "\n"+context.getString(R.string.lives_in_1)+model.getUserDetails().getState()+", "+model.getUserDetails().getCity();
            String gender = "\n"+context.getString(R.string.gender_2)+model.getUserDetails().getGender();
            String schoolname = "\n"+context.getString(R.string.school_2)+model.getUserDetails().getSchoolName();
            alert.setMessage(grade+schoolname+stateCity+gender);
            alert.setPositiveButton(context.getString(R.string.ok), null);
            alert.show();
        });
    }

    @Override
    public int getItemCount() {
        if (data == null)
            return 0;
        else
            return data.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {

        LtHomeListAdminBinding binding;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = LtHomeListAdminBinding.bind(itemView);
        }
    }

    public void setFilter(List<UserData> FilteredDataList) {
        data = FilteredDataList;
        notifyDataSetChanged();
    }
}