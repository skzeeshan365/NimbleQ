package com.reiserx.nimbleq.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.downloader.PRDownloader;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.databinding.ActivityClassBinding;

public class ClassActivity extends AppCompatActivity {

    public static String classID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.reiserx.nimbleq.databinding.ActivityClassBinding binding = ActivityClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();

        classID = getIntent().getExtras().getString("classID");

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_class);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        PRDownloader.initialize(getApplicationContext());
    }

}