package com.reiserx.nimbleq.Adapters.Administration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.SharedPreferenceClass;
import com.reiserx.nimbleq.databinding.LtHomeListAdminBinding;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.HomeViewHolder> {

    Context context;
    List<UserData> data;
    NavController navController;
    int actionCode;

    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }

    public void setData(List<UserData> data) {
        this.data = data;
    }

    public UserListAdapter(Context context, NavController navController) {
        this.context = context;
        this.navController = navController;
    }

    @NonNull
    @Override
    public UserListAdapter.HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lt_home_list_admin, parent, false);
        return new UserListAdapter.HomeViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull UserListAdapter.HomeViewHolder holder, int position) {
        UserData model = data.get(position);
        holder.binding.textView25.setText(model.getUserName());

        holder.binding.getRoot().setOnClickListener(view -> {
            SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(context);

            sharedPreferenceClass.setUserInfo(model);
            navController.navigate(actionCode);
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
}