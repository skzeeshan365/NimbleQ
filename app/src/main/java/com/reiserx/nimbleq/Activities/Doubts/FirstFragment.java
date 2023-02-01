package com.reiserx.nimbleq.Activities.Doubts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.reiserx.nimbleq.Adapters.Doubts.DoubtsAdapter;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.Doubts.DoubtsModel;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.SnackbarTop;
import com.reiserx.nimbleq.Utils.UserTypeClass;
import com.reiserx.nimbleq.ViewModels.DoubtsViewModel;
import com.reiserx.nimbleq.ViewModels.slotsViewModel;
import com.reiserx.nimbleq.databinding.FragmentFirstBinding;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    DoubtsAdapter adapter;

    LinearLayoutManager layoutManager;
    List<DoubtsModel> dataList;

    FirebaseAuth auth;
    FirebaseUser user;

    SnackbarTop snackbarTop;

    UserTypeClass userTypeClass;

    int pastVisiblesItems, totalItemCount, first_visible_item, lastItem;
    boolean loading;

    DoubtsViewModel viewModel;

    String subject;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);

        userTypeClass = new UserTypeClass(getContext());

        if (userTypeClass.isUserLearner()) {
            binding.fab2.setVisibility(View.VISIBLE);
            binding.fab2.setOnClickListener(view -> NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.action_FirstFragment_to_SecondFragment));
        } else
            binding.fab2.setVisibility(View.GONE);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        binding.recycler.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);

        loading = true;

        dataList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getContext());
        binding.recycler.setHasFixedSize(true);
        binding.recycler.setLayoutManager(layoutManager);
        adapter = new DoubtsAdapter(requireContext().getApplicationContext(), NavHostFragment.findNavController(FirstFragment.this), loading);
        binding.recycler.setAdapter(adapter);

        snackbarTop = new SnackbarTop(binding.getRoot());

        getData();

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("NotifyDataSetChanged")
    void getData() {
        viewModel = new ViewModelProvider(this).get(DoubtsViewModel.class);

        slotsViewModel slotsViewModel = new ViewModelProvider(this).get(com.reiserx.nimbleq.ViewModels.slotsViewModel.class);

        if (userTypeClass.isUserLearner()) {
            viewModel.getDoubtsForStudents(user.getUid());
        } else {
            slotsViewModel.getSubjectForTeachers(user.getUid());
            slotsViewModel.getParentItemMutableLiveData().observe(getViewLifecycleOwner(), subjectAndTimeSlot -> {
                viewModel.getDoubtsForTeachers(subjectAndTimeSlot.getSubject());
                subject = subjectAndTimeSlot.getSubject();
            });
        }
        viewModel.getDoubtListModelMutableLiveData().observe(getViewLifecycleOwner(), doubtsModelList -> {
            adapter.setParentItemList(doubtsModelList);
            dataList.clear();
            dataList.addAll(doubtsModelList);
            adapter.notifyDataSetChanged();
            scrollListener();
            binding.recycler.setVisibility(View.VISIBLE);
            binding.progHolder.setVisibility(View.GONE);
        });
        viewModel.getDatabaseErrorMutableLiveData().observe(getViewLifecycleOwner(), error -> {
            binding.textView9.setText(error);
            binding.recycler.setVisibility(View.GONE);
            binding.progHolder.setVisibility(View.VISIBLE);
            binding.progressBar2.setVisibility(View.GONE);
            binding.textView9.setVisibility(View.VISIBLE);
        });
    }

    private void scrollListener() {
        binding.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) { //check for scroll down
                    first_visible_item = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                    if ((first_visible_item + pastVisiblesItems) >= totalItemCount) {
                        if (lastItem != totalItemCount) {
                            paginate();
                            lastItem = totalItemCount;
                        }
                    }
                }
            }
        });
    }

    public void paginate() {
        if (subject != null) {
            viewModel.paginateDoubts(subject, adapter);
            loading = true;

            viewModel.getDoubtPageListModelMutableLiveData().observe(getViewLifecycleOwner(), doubtsModelList -> loading = false);
            viewModel.getDatabaseErrorMutableLiveData().observe(getViewLifecycleOwner(), s -> Log.d(CONSTANTS.TAG2, s));
        }
    }
}