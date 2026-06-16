package com.example.cs4084_group_3;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.appbar.MaterialToolbar;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!AuthStore.isLoggedIn(this)) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // Top App Bar
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        // Nav Controller setup
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Connect bottom nav with navigation
            NavigationUI.setupWithNavController(bottomNav, navController);

            // enables the back arrow in the toolbar
            NavigationUI.setupActionBarWithNavController(this, navController);
        }
    }

    // Handles back arrow click
    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
