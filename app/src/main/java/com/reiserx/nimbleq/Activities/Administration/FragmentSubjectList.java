package com.reiserx.nimbleq.Activities.Administration;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.reiserx.nimbleq.Adapters.Administration.AdminListsAdapter;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.ViewModels.AdministrationViewModel;
import com.reiserx.nimbleq.databinding.FragmentUpdateGradeListBinding;

public class FragmentSubjectList extends Fragment {

    private FragmentUpdateGradeListBinding binding;

    AdminListsAdapter adapter;
    AdministrationViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUpdateGradeListBinding.inflate(inflater, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recycler.setLayoutManager(layoutManager);
        adapter = new AdminListsAdapter(getContext());

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AdministrationViewModel.class);

        viewModel.getSubjectModelList();
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

    public void update() {
        AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());

        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.single_edittext_layout, null);
        final EditText editText = mView.findViewById(R.id.editTextNumber);

        editText.setHint(getString(R.string.enter_a_subject_name));

        alert.setTitle(getString(R.string.add_subject));
        alert.setMessage(getString(R.string.add_subject_msg));

        alert.setPositiveButton(getString(R.string.add), (dialogInterface, i) -> {
            if (editText.getText().toString().trim().equals(""))
                Toast.makeText(getContext(), getString(R.string.please_enter_a_subject), Toast.LENGTH_SHORT).show();
            else {
                viewModel.updateSubjectModelList(editText.getText().toString().trim());
            }
        });

        alert.setNegativeButton(getString(R.string.cancel), null);

        alert.setView(mView);
        alert.show();
    }
}