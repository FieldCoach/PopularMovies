package com.example.android.popularmovies.ui;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.android.popularmovies.R;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private NavigationView navView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Find navController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        // Set toolbar as action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Set hamburger menu icon
        setActionBarIcon();
        // Set drawer layout and app_menu item navigation
        drawerLayout = findViewById(R.id.drawer_layout);
        AppBarConfiguration appBarConfiguration = getAppBarConfig();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        navView = findViewById(R.id.nav_view);
        // Set click listener for drawer menu items
        setDrawerItemClickListener();
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
    }

    private void setActionBarIcon() {
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    private AppBarConfiguration getAppBarConfig() {
        return new AppBarConfiguration
                .Builder(R.id.nav_home)
                .setDrawerLayout(drawerLayout).build();
    }

    private void setDrawerItemClickListener() {
        navView.setNavigationItemSelectedListener(
            menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        Navigation
                                .findNavController(this, R.id.nav_host_fragment)
                                .navigate(R.id.nav_home);
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.nav_detail:
                        Navigation
                                .findNavController(this, R.id.nav_host_fragment)
                                .navigate(R.id.nav_detail);
                        drawerLayout.closeDrawers();
                        return true;
                }
                return true;
            }
        );
    }
}