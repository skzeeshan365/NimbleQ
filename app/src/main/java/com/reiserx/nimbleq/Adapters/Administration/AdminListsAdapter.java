package com.reiserx.nimbleq.Adapters.Administration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
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

    public void AddData(AdminListModel model) {
        data.add(model);
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

        holder.binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Delete element");
                alert.setTitle("Are you sure you want to delete ".concat(model.getName()));
                alert.setPositiveButton("delete", (dialogInterface, i) -> {
                    model.getReference().removeValue();
                });
                alert.setNegativeButton("cancel", null);
                alert.show();
                return false;
            }
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