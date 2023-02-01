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
import com.reiserx.nimbleq.databinding.FragmentLinkPoliciesBinding;

public class FragmentLinkPolicies extends Fragment {

    private FragmentLinkPoliciesBinding binding;

    AdministrationViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLinkPoliciesBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AdministrationViewModel.class);

        viewModel.getLinkPrivacyPolicy();
        viewModel.getLinkTermsOfService();

        viewModel.getLinkPrivacyPolicyMutableLiveData().observe(getViewLifecycleOwner(), aLong -> binding.privacyLink.setText(aLong));
        viewModel.getLinkTermsOfServiceMutableLiveData().observe(getViewLifecycleOwner(), aLong -> binding.termsLink.setText(aLong));

        binding.privacyHolder.setOnClickListener(view1 -> updatePrivacyLink(binding.privacyLink.getText().toString()));

        binding.termsHolder.setOnClickListener(view1 -> updateTermsLink(binding.termsLink.getText().toString()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void updatePrivacyLink(String limit) {
        AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());

        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.single_edittext_layout, null);
        final EditText editText = mView.findViewById(R.id.editTextNumber);

        editText.setText(limit);

        alert.setTitle(getString(R.string.privacy_policy));
        alert.setMessage(getString(R.string.privacy_msg));

        alert.setPositiveButton(getString(R.string.update), (dialogInterface, i) -> {
            if (editText.getText().toString().trim().equals(""))
                Toast.makeText(getContext(), getString(R.string.field_required), Toast.LENGTH_SHORT).show();
            else {
                viewModel.updateLinkPrivacyPolicy(editText.getText().toString().trim());
            }
        });

        alert.setNegativeButton(getString(R.string.cancel), null);

        alert.setView(mView);
        alert.show();
    }

    public void updateTermsLink(String limit) {
        AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());

        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.single_edittext_layout, null);
        final EditText editText = mView.findViewById(R.id.editTextNumber);

        editText.setText(limit);

        alert.setTitle(getString(R.string.terms));
        alert.setMessage(getString(R.string.terms_msg));

        alert.setPositiveButton(getString(R.string.update), (dialogInterface, i) -> {
            if (editText.getText().toString().trim().equals(""))
                Toast.makeText(getContext(), getString(R.string.field_required), Toast.LENGTH_SHORT).show();
            else {
                viewModel.updateLinkTermsOfService(editText.getText().toString().trim());
            }
        });

        alert.setNegativeButton(getString(R.string.cancel), null);

        alert.setView(mView);
        alert.show();
    }
}