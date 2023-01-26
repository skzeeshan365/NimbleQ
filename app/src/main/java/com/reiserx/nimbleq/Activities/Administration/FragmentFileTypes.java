package com.reiserx.nimbleq.Activities.Administration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.reiserx.nimbleq.Adapters.Administration.AdminListsAdapter;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.ViewModels.AdministrationViewModel;
import com.reiserx.nimbleq.databinding.FragmentFileTypesBinding;

public class FragmentFileTypes extends Fragment {

    private FragmentFileTypesBinding binding;

    AdminListsAdapter adapter;
    AdministrationViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFileTypesBinding.inflate(inflater, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recycler.setLayoutManager(layoutManager);
        adapter = new AdminListsAdapter(getContext());

        binding.recycler.setVisibility(View.GONE);
        binding.floatingActionButton.setVisibility(View.GONE);

        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AdministrationViewModel.class);

        updateSwitch();

        viewModel.getFileEnabled();
        viewModel.getFileEnabledMutableLiveData().observe(getViewLifecycleOwner(), fileEnabled -> {
            binding.switch1.setChecked(fileEnabled);
            if (fileEnabled) {
                binding.recycler.setVisibility(View.GONE);
                binding.floatingActionButton.setVisibility(View.GONE);
            } else {
                binding.recycler.setVisibility(View.VISIBLE);
                binding.floatingActionButton.setVisibility(View.VISIBLE);
                viewModel.getFileList();
            }
        });
        viewModel.getAdminModelListMutableLiveData().observe(getViewLifecycleOwner(), models -> {
            adapter.setData(models);
            binding.recycler.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        });

        binding.floatingActionButton.setOnClickListener(view1 -> update());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    void updateSwitch() {
        binding.switch1.setOnCheckedChangeListener((compoundButton, b) -> viewModel.updateFilesEnabled(binding.switch1.isChecked()));
    }

    public void update() {
        AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());

        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.single_edittext_layout, null);
        final EditText editText = mView.findViewById(R.id.editTextNumber);

        editText.setHint(getString(R.string.enter_a_mime_type));

        alert.setTitle(getString(R.string.add_file_type));
        alert.setMessage(getString(R.string.add_file_type_msg));

        alert.setPositiveButton(getString(R.string.add), (dialogInterface, i) -> {
            if (editText.getText().toString().trim().equals(""))
                Toast.makeText(getContext(), getString(R.string.enter_a_mime_type), Toast.LENGTH_SHORT).show();
            else {
                viewModel.updateFIleModelList(editText.getText().toString().trim());
            }
        });

        alert.setNegativeButton(getString(R.string.cancel), null);

        alert.setView(mView);
        alert.show();
    }
}