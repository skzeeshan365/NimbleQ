package com.reiserx.nimbleq.Adapters.Administration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.reiserx.nimbleq.Models.AdminListModel;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.databinding.LtHomeListAdminBinding;

import java.util.List;

public class AdminListsAdapter extends RecyclerView.Adapter<AdminListsAdapter.ListViewHolder> {

    Context context;
    List<AdminListModel> data;

    public void setData(List<AdminListModel> data) {
        this.data = data;
    }

    public AdminListsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public AdminListsAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lt_home_list_admin, parent, false);
        return new AdminListsAdapter.ListViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull AdminListsAdapter.ListViewHolder holder, int position) {
        AdminListModel model = data.get(position);
        holder.binding.textView25.setText(model.getName());

        holder.binding.getRoot().setOnLongClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle(context.getString(R.string.delete_element));
            alert.setTitle(context.getString(R.string.are_you_sure_you_want_to_delete).concat(model.getName()));
            alert.setPositiveButton(context.getString(R.string.delete), (dialogInterface, i) -> model.getReference().removeValue());
            alert.setNegativeButton(context.getString(R.string.cancel), null);
            alert.show();
            return false;
        });
    }

    @Override
    public int getItemCount() {
        if (data == null)
            return 0;
        else
            return data.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {

        LtHomeListAdminBinding binding;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = LtHomeListAdminBinding.bind(itemView);
        }
    }
}