package com.reiserx.nimbleq.Activities.Doubts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.reiserx.nimbleq.Adapters.Doubts.AnswersAdapter;
import com.reiserx.nimbleq.Models.Doubts.AnswerModel;
import com.reiserx.nimbleq.Models.Doubts.DoubtsModel;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.SharedPreferenceClass;
import com.reiserx.nimbleq.Utils.SnackbarTop;
import com.reiserx.nimbleq.Utils.UserTypeClass;
import com.reiserx.nimbleq.ViewModels.DoubtsViewModel;
import com.reiserx.nimbleq.databinding.FragmentViewDoubtsBinding;

import java.util.ArrayList;
import java.util.List;

public class ViewDoubtsFragment extends Fragment {

   private FragmentViewDoubtsBinding binding;

    AnswersAdapter adapter;

    LinearLayoutManager layoutManager;
    List<AnswerModel> dataList;

    SnackbarTop snackbarTop;

    DoubtsModel doubtsModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentViewDoubtsBinding.inflate(inflater, container, false);

        binding.recycler.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);

        dataList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getContext());
        binding.recycler.setHasFixedSize(true);
        binding.recycler.setLayoutManager(layoutManager);
        adapter = new AnswersAdapter(requireContext());
        binding.recycler.setAdapter(adapter);

        snackbarTop = new SnackbarTop(binding.getRoot());

        
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UserTypeClass userTypeClass = new UserTypeClass(getContext());

        if (!userTypeClass.isUserLearner()) {
            binding.attachHolder.setVisibility(View.VISIBLE);
            binding.attachHolder.setOnClickListener(view1 -> NavHostFragment.findNavController(ViewDoubtsFragment.this).navigate(R.id.action_ViewDoubtsFragment_to_SubmitAnswerFragment));
        } else
            binding.attachHolder.setVisibility(View.GONE);

        SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(requireContext());
        doubtsModel = sharedPreferenceClass.getDoubtInfo();

        binding.doubtsInOneline.setText(doubtsModel.getShort_desc());
        binding.doubtDesc.setText(doubtsModel.getLong_desc());
        binding.dbTimestamp.setText(TimeAgo.using(doubtsModel.getTimeStamp()));
        binding.dbSubject.setText(doubtsModel.getSubject().concat(" • ".concat(doubtsModel.getTopic())));

        getData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("NotifyDataSetChanged")
    void getData() {
        DoubtsViewModel viewModel = new ViewModelProvider(this).get(DoubtsViewModel.class);

        viewModel.getAnswers(doubtsModel.getId());
        viewModel.getAnswerListModelMutableLiveData().observe(getViewLifecycleOwner(), answerModelList -> {
            if (!answerModelList.isEmpty()) {
                adapter.setAnswerModelList(answerModelList);
                dataList.clear();
                dataList.addAll(answerModelList);
                adapter.notifyDataSetChanged();
                binding.recycler.setVisibility(View.VISIBLE);
                binding.progHolder.setVisibility(View.GONE);
            } else {
                binding.textView9.setText(getString(R.string.answers_not_available));
                binding.recycler.setVisibility(View.GONE);
                binding.progHolder.setVisibility(View.VISIBLE);
                binding.progressBar2.setVisibility(View.GONE);
                binding.textView9.setVisibility(View.VISIBLE);
            }
        });
        viewModel.getDatabaseErrorMutableLiveData().observe(getViewLifecycleOwner(), error -> {
            binding.textView9.setText(error);
            binding.recycler.setVisibility(View.GONE);
            binding.progHolder.setVisibility(View.VISIBLE);
            binding.progressBar2.setVisibility(View.GONE);
            binding.textView9.setVisibility(View.VISIBLE);
        });
    }
}