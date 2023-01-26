package com.reiserx.nimbleq.Activities.Administration;

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

import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.ViewModels.AdministrationViewModel;
import com.reiserx.nimbleq.databinding.FragmentLimitsBinding;

public class FragmentLimits extends Fragment {

    private FragmentLimitsBinding binding;

    AdministrationViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLimitsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AdministrationViewModel.class);

        viewModel.getSlotLimit();
        viewModel.getFileSizeLimit();

        viewModel.getSlotLimitMutableLiveData().observe(getViewLifecycleOwner(), aLong -> binding.textView15.setText(String.valueOf(aLong)));
        viewModel.getFileSizeLimitMutableLiveData().observe(getViewLifecycleOwner(), aLong -> binding.textView19.setText(String.valueOf(aLong)));

        binding.slotHolderLt.setOnClickListener(view1 -> updateSlotLimit(binding.textView15.getText().toString()));

        binding.fileSizeHolderLt.setOnClickListener(view1 -> updateFileSizeLimit(binding.textView19.getText().toString()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void updateFileSizeLimit(String limit) {
        AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());

        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.single_edittext_layout, null);
        final EditText editText = mView.findViewById(R.id.editTextNumber);

        editText.setText(limit);

        alert.setTitle(getString(R.string.file_size_limit));
        alert.setMessage(getString(R.string.file_size_limit_msg));

        alert.setPositiveButton(getString(R.string.add), (dialogInterface, i) -> {
            if (editText.getText().toString().trim().equals(""))
                Toast.makeText(getContext(), getString(R.string.field_required), Toast.LENGTH_SHORT).show();
            else {
                viewModel.updateFileSizeLimit(Long.valueOf(editText.getText().toString().trim()));
            }
        });

        alert.setNegativeButton(getString(R.string.cancel), null);

        alert.setView(mView);
        alert.show();
    }

    public void updateSlotLimit(String limit) {
        AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());

        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.single_edittext_layout, null);
        final EditText editText = mView.findViewById(R.id.editTextNumber);

        editText.setText(limit);

        alert.setTitle(getString(R.string.slot_limit));
        alert.setMessage(getString(R.string.slot_limit_msg));

        alert.setPositiveButton(getString(R.string.add), (dialogInterface, i) -> {
            if (editText.getText().toString().trim().equals(""))
                Toast.makeText(getContext(), getString(R.string.field_required), Toast.LENGTH_SHORT).show();
            else {
                viewModel.updateSlotLimit(Long.valueOf(editText.getText().toString().trim()));
            }
        });

        alert.setNegativeButton(getString(R.string.cancel), null);

        alert.setView(mView);
        alert.show();
    }
}