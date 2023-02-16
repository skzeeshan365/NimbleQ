package com.reiserx.nimbleq.Activities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.reiserx.nimbleq.Adapters.AboutAdapter;
import com.reiserx.nimbleq.BuildConfig;
import com.reiserx.nimbleq.Models.AboutModelList;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.databinding.FragmentAboutBinding;

import java.util.ArrayList;
import java.util.List;

public class FragmentAbout extends DialogFragment {

    public static final String TAG = "about_dialog";
    FragmentAboutBinding binding;

    AboutAdapter aboutAdapter;
    List<AboutModelList> data;

    public static FragmentAbout display(FragmentManager fragmentManager) {
        FragmentAbout exampleDialog = new FragmentAbout();
        exampleDialog.show(fragmentManager, TAG);
        return exampleDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentAboutBinding.inflate(inflater, container, false);

        data = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recycler.setLayoutManager(layoutManager);
        aboutAdapter = new AboutAdapter(getContext(), data);
        binding.recycler.setNestedScrollingEnabled(false);
        binding.recycler.setAdapter(aboutAdapter);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.imageView6.setOnClickListener(view1 -> {
          dismiss();
        });

        binding.textView39.setText(BuildConfig.VERSION_NAME);

        data.add(new AboutModelList(getString(R.string.Website), true));
        data.add(new AboutModelList(getString(R.string.Website), "https://nimbleq.org", false));
        data.add(new AboutModelList(getString(R.string.contact_us), true));
        data.add(new AboutModelList(getString(R.string.Email), "mailto:contact@nimbleq.org", false));
        data.add(new AboutModelList(getString(R.string.LinkedIn), "https://www.linkedin.com/company/nimbleqindia/", false));
        data.add(new AboutModelList(getString(R.string.Instagram), "https://www.instagram.com/nimbleqorg/", false));
        data.add(new AboutModelList(getString(R.string.Facebook), "https://www.facebook.com/NimbleQ", false));
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }
}