package com.reiserx.nimbleq.Adapters.Administration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.databinding.LtHomeListAdminBinding;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    Context context;
    List<String> data;
    NavController navController;

    public HomeAdapter(Context context, List<String> data, NavController navController) {
        this.context = context;
        this.data = data;
        this.navController = navController;
    }

    @NonNull
    @Override
    public HomeAdapter.HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lt_home_list_admin, parent, false);
        return new HomeAdapter.HomeViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.HomeViewHolder holder, int position) {
        String model = data.get(position);
        holder.binding.textView25.setText(model);

        switch (position) {
            case 0:
                holder.binding.getRoot().setOnClickListener(view -> navController.navigate(R.id.action_HomeFragment_to_FragmentUserList));
                break;
            case 1:
                holder.binding.getRoot().setOnClickListener(view -> navController.navigate(R.id.action_HomeFragment_to_FragmentStudentList));
                break;
            case 2:
                holder.binding.getRoot().setOnClickListener(view -> navController.navigate(R.id.action_HomeFragment_to_FragmentTeacherList));
                break;
            case 3:
                holder.binding.getRoot().setOnClickListener(view -> navController.navigate(R.id.action_HomeFragment_to_FragmentUpdateGradeList));
                break;
            case 4:
                holder.binding.getRoot().setOnClickListener(view -> navController.navigate(R.id.action_HomeFragment_to_FragmentSubjectList));
                break;
            case 5:
                holder.binding.getRoot().setOnClickListener(view -> navController.navigate(R.id.action_HomeFragment_to_FragmentSlotList));
                break;
            case 6:
                holder.binding.getRoot().setOnClickListener(view -> navController.navigate(R.id.action_HomeFragment_to_FragmentClassListByDemand));
                break;
            case 7:
                holder.binding.getRoot().setOnClickListener(view -> navController.navigate(R.id.action_HomeFragment_to_FragmentClassListByRating));
                break;
            case 8:
                holder.binding.getRoot().setOnClickListener(view -> navController.navigate(R.id.action_HomeFragment_to_FragmentFileTypes));
                break;
            case 9:
                holder.binding.getRoot().setOnClickListener(view -> navController.navigate(R.id.action_HomeFragment_to_FragmentLimits));
                break;
            case 10:
                holder.binding.getRoot().setOnClickListener(view -> navController.navigate(R.id.action_HomeFragment_to_FragmentLinkPolicies));
                break;
        }
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