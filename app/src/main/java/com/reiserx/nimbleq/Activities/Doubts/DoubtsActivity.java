package com.reiserx.nimbleq.Activities.Doubts;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.databinding.ActivityDoubtsBinding;

public class DoubtsActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.reiserx.nimbleq.databinding.ActivityDoubtsBinding binding = ActivityDoubtsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        if (getIntent().getBooleanExtra("open", false)) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_doubts);
            appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            navController.navigate(R.id.action_FirstFragment_to_ViewDoubtsFragment);
        } else {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_doubts);
            appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_doubts);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

}