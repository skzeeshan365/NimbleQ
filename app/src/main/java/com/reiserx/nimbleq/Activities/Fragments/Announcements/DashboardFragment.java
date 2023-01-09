package com.reiserx.nimbleq.Activities.Fragments.Announcements;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.reiserx.nimbleq.Activities.AnnouncementsActivity;
import com.reiserx.nimbleq.Adapters.Announcements.announcementsAdapter;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.ButtonDesign;
import com.reiserx.nimbleq.Utils.UserTypeClass;
import com.reiserx.nimbleq.ViewModels.AnnouncementsViewModel;
import com.reiserx.nimbleq.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment implements MenuProvider {

    private FragmentDashboardBinding binding;

    announcementsAdapter adapter;

    LinearLayoutManager layoutManager;

    String id;

    ButtonDesign buttonDesign;
    UserTypeClass userTypeClass;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.recycler.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);
        binding.progButton.setVisibility(View.GONE);

        layoutManager = new LinearLayoutManager(getContext());
        binding.recycler.setHasFixedSize(true);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(true);
        binding.recycler.setLayoutManager(layoutManager);
        adapter = new announcementsAdapter();
        binding.recycler.setAdapter(adapter);

        buttonDesign = new ButtonDesign(getContext());

        id = getActivity().getIntent().getExtras().getString("classID");

        userTypeClass = new UserTypeClass(requireContext());

        if (userTypeClass.isUserLearner())
            requireActivity().removeMenuProvider(this);
        else {
            requireActivity().removeMenuProvider(this);
            requireActivity().addMenuProvider(this, getViewLifecycleOwner());
        }

        getData(id);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    void getData(String id) {
        AnnouncementsViewModel viewModel = new ViewModelProvider(this).get(AnnouncementsViewModel.class);

        viewModel.getAllData(id);
        viewModel.getParentItemMutableLiveData().observe(getViewLifecycleOwner(), parentItemList -> {
                adapter.setParentItemList(parentItemList);
                adapter.notifyDataSetChanged();
                binding.recycler.setVisibility(View.VISIBLE);
                binding.progHolder.setVisibility(View.GONE);
        });
        viewModel.getDatabaseErrorMutableLiveData().observe(getViewLifecycleOwner(), error -> {
                binding.textView9.setText("Announcements not available");
                binding.recycler.setVisibility(View.GONE);
                binding.progHolder.setVisibility(View.VISIBLE);
                binding.progressBar2.setVisibility(View.GONE);
                binding.textView9.setVisibility(View.VISIBLE);

                if (userTypeClass.isUserLearner())
                    binding.progButton.setVisibility(View.GONE);
                else {
                    binding.progButton.setVisibility(View.VISIBLE);
                    buttonDesign.setButtonOutline(binding.progButton);
                    binding.progButton.setText("Post announcement");
                    binding.progButton.setOnClickListener(view -> {
                        Intent intent = new Intent(getContext(), AnnouncementsActivity.class);
                        intent.putExtra("id", id);
                        requireContext().startActivity(intent);
                    });
                }
        });
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menu.clear();
        menuInflater.inflate(R.menu.announcements_menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.post_announcements) {
            Intent intent = new Intent(getContext(), AnnouncementsActivity.class);
            intent.putExtra("id", id);
            getContext().startActivity(intent);
        }
        return false;
    }
}