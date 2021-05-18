package com.berete.go4lunch.ui.core.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.berete.go4lunch.R;
import com.berete.go4lunch.databinding.ActivityMainBinding;
import com.berete.go4lunch.ui.core.services.location.LocationPermissionHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity{

    @Inject
    public LocationPermissionHandler locationPermissionHandler;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupNavigation();
        requestLocationPermission();
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

    private void requestLocationPermission(){
        if(!locationPermissionHandler.hasPermission())
            locationPermissionHandler.requestPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}