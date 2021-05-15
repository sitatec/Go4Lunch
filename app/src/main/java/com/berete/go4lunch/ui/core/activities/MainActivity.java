package com.berete.go4lunch.ui.core.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.berete.go4lunch.R;
import com.berete.go4lunch.databinding.ActivityMainBinding;
import com.berete.go4lunch.ui.core.utils.LocationPermissionHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;


public class MainActivity extends AppCompatActivity{

    private LocationPermissionHandler locationPermissionHandler;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        locationPermissionHandler = new LocationPermissionHandler(this);
        locationPermissionHandler.requirePermission();
        setContentView(binding.getRoot());
        setupNavigation();
    }

    private void setupNavigation(){
        final NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        assert navHostFragment != null;
        final NavController navController = navHostFragment.getNavController();
        final Toolbar toolbar = findViewById(R.id.toolbar);
        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        final NavigationView navigationView = findViewById(R.id.bottom_navigation_view);
        NavigationUI.setupWithNavController(toolbar, navController, getAppBarConfig());
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    private AppBarConfiguration getAppBarConfig() {
        return new AppBarConfiguration
                .Builder(R.id.mapFragment, R.id.restaurantListFragment,
                R.id.workmatesListFragment, R.id.conversationsListFragment)
                .setOpenableLayout(binding.getRoot())
                .build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}