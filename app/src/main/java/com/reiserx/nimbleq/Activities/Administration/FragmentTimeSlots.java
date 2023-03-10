package com.reiserx.nimbleq.Activities.Administration;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.reiserx.nimbleq.Adapters.Administration.AdminListsAdapter;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.ViewModels.AdministrationViewModel;
import com.reiserx.nimbleq.databinding.FragmentUpdateGradeListBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FragmentTimeSlots extends Fragment {

    private FragmentUpdateGradeListBinding binding;

    AdminListsAdapter adapter;
    AdministrationViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUpdateGradeListBinding.inflate(inflater, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recycler.setLayoutManager(layoutManager);
        adapter = new AdminListsAdapter(getContext());

        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AdministrationViewModel.class);

        viewModel.getSlotModelList();
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
        View mView = inflater.inflate(R.layout.single_textview_dialog, null);
        final TextView editText = mView.findViewById(R.id.textView26);

        editText.setHint(getString(R.string.select_time_slot));

        alert.setTitle(getString(R.string.add_time_slot));
        alert.setMessage(getString(R.string.add_time_slot_msg));

        editText.setOnClickListener(view -> showTimeDialog(editText));
        alert.setPositiveButton(getString(R.string.add), (dialogInterface, i) -> {
            if (editText.getText().toString().trim().equals(""))
                Toast.makeText(getContext(), getString(R.string.select_time_slot), Toast.LENGTH_SHORT).show();
            else {
                viewModel.updateSlotModelList(editText.getText().toString().trim());
            }
        });

        alert.setNegativeButton(getString(R.string.cancel), null);

        alert.setView(mView);
        alert.show();
    }

    private void showTimeDialog(final TextView time_in) {
        final Calendar calendar = Calendar.getInstance();

        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa");
            time_in.setText(simpleDateFormat.format(calendar.getTime()));
        };

        new TimePickerDialog(getContext(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
    }
}