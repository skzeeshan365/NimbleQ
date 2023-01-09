package com.reiserx.nimbleq.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.reiserx.nimbleq.Adapters.slotsAdapter;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.subjectAndTimeSlot;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.ViewModels.SlotListViewModel;
import com.reiserx.nimbleq.databinding.ActivitySlotsListBinding;

import java.util.ArrayList;

public class SlotsListActivity extends AppCompatActivity {

    ActivitySlotsListBinding binding;

    ArrayList<subjectAndTimeSlot> data;
    ArrayList<subjectAndTimeSlot> data2;
    slotsAdapter adapter;

    LinearLayoutManager layoutManager;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySlotsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recycler.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);

        setTitle("Available slots");

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = auth.getCurrentUser();

        data = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        binding.recycler.setLayoutManager(layoutManager);
        adapter = new slotsAdapter(this, data, findViewById(android.R.id.content), true, user.getUid());
        binding.recycler.setAdapter(adapter);

        getData();
    }

    void getData() {

        SlotListViewModel viewModel = new ViewModelProvider(this).get(SlotListViewModel.class);

        viewModel.getSlotList(this);
        viewModel.getSlotListMutableData().observe(this, datas -> {
            Log.d(CONSTANTS.TAG, String.valueOf(datas.size()));
            if (!datas.isEmpty()) {
                adapter.setSlotListData(datas);
                removeDuplicates();
                adapter.notifyDataSetChanged();
                binding.recycler.setVisibility(View.VISIBLE);
                binding.progHolder.setVisibility(View.GONE);
            } else {
                binding.textView9.setText(getString(R.string.slot_not_avail));
                binding.recycler.setVisibility(View.GONE);
                binding.progHolder.setVisibility(View.VISIBLE);
                binding.progressBar2.setVisibility(View.GONE);
                binding.textView9.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getDatabaseErrorMutableLiveData().observe(this, error -> {
            binding.textView9.setText(error);
            binding.recycler.setVisibility(View.GONE);
            binding.progHolder.setVisibility(View.VISIBLE);
            binding.progressBar2.setVisibility(View.GONE);
            binding.textView9.setVisibility(View.VISIBLE);
        });
    }

    void removeDuplicates() {
        int count = data.size();

        for (int i = 0; i < count; i++) {
            for (int j = i + 1; j < count; j++) {
                subjectAndTimeSlot subjectAndTimeSlot = data.get(i);
                subjectAndTimeSlot subjectAndTimeSlot1 = data.get(j);
                if (subjectAndTimeSlot.getTimeSlot().equals(subjectAndTimeSlot1.getTimeSlot()) && subjectAndTimeSlot.getSubject().equals(subjectAndTimeSlot1.getSubject()) && subjectAndTimeSlot.getTopic().equals(subjectAndTimeSlot1.getTopic())) {
                    data.remove(j--);
                    count--;
                }
            }
        }
    }
}