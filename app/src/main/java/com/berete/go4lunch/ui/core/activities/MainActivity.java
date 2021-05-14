package com.berete.go4lunch.ui.core.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.berete.go4lunch.R;
import com.berete.go4lunch.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupNavigation();
    }

    @SuppressWarnings({"ConstantConditions"})
    private void setupNavigation(){
        final NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        final NavController navController = navHostFragment.getNavController();
        final Toolbar toolbar = findViewById(R.id.toolbar);
        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        final NavigationView navigationView = findViewById(R.id.bottom_navigation_view);
        NavigationUI.setupWithNavController(toolbar, navController, getAppBarConfig());
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private AppBarConfiguration getAppBarConfig() {
        return new AppBarConfiguration
                .Builder(R.id.mapFragment, R.id.restaurantListFragment,
                R.id.workmatesListFragment, R.id.conversationsListFragment)
                .setOpenableLayout(binding.getRoot())
                .build();
    }

}