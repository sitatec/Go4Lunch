package com.berete.go4lunch.ui.core.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.berete.go4lunch.R;
import com.berete.go4lunch.databinding.ActivityRootBinding;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RootActivity extends AppCompatActivity {

    private static final int AUTH_RESULT_CODE = 4;

    private ActivityRootBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRootBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        requireAuthentication();
        binding.retryLogin.setOnClickListener(button -> startLoginActivity());
    }

    private void requireAuthentication(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            startMainActivity();
        }else {
            startLoginActivity();
        }
    }

    private void startLoginActivity(){
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(getAuthProviders())
                .setAuthMethodPickerLayout(getCustomAuthLayout())
                .setTheme(R.style.NoActionBarSysUITransparentTheme)
                .build(), AUTH_RESULT_CODE);
    }

    private List<AuthUI.IdpConfig> getAuthProviders(){
        return Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build()
        );
    }

    private AuthMethodPickerLayout getCustomAuthLayout(){
        return new AuthMethodPickerLayout
                .Builder(R.layout.activity_auth_method_picker)
                .setGoogleButtonId(R.id.google_login_btn)
                .setFacebookButtonId(R.id.fb_login_btn)
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        binding.getRoot().setVisibility(View.VISIBLE);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AUTH_RESULT_CODE){
            if(resultCode == Activity.RESULT_OK) {
                startMainActivity();
            }
            else if (resultCode == Activity.RESULT_CANCELED){
                showLoginError(R.string.login_canceled);
            }
            else handleLoginFailure(data);
        }
    }

    @SuppressWarnings({"ConstantConditions"})
    private void handleLoginFailure(Intent loginResponseData){
        try {
            final FirebaseUiException responseError = IdpResponse
                    .fromResultIntent(loginResponseData).getError();
            if (responseError.getErrorCode() == ErrorCodes.NO_NETWORK) {
                showLoginError(R.string.login_failed_network_error);
            } else {
                showLoginError(R.string.login_failed_unknown_error);
            }
        } catch (NullPointerException e){
            showLoginError(R.string.login_failed_unknown_error);
        }
    }

    private void showLoginError(int resourceId){
        binding.loginError.setVisibility(View.VISIBLE);
        binding.loginErrorText.setText(resourceId);
    }

    private void startMainActivity(){
        startActivity(new Intent(this, MainActivity.class));
    }
}