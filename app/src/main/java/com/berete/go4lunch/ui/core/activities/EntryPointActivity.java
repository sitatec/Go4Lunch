package com.berete.go4lunch.ui.core.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.berete.go4lunch.R;
import com.berete.go4lunch.databinding.ActivityEntryPointBinding;
import com.berete.go4lunch.domain.shared.UserProvider;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EntryPointActivity extends AppCompatActivity {

  private ActivityEntryPointBinding binding;
  @Inject
  public UserProvider userProvider;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityEntryPointBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    requireAuthentication();
    binding.retryLogin.setOnClickListener(button -> startLoginActivity());
  }

  private void requireAuthentication() {
    if ( userProvider.getCurrentUser() != null) {
        startMainActivity();
    } else {
      startLoginActivity();
    }
  }

  private void startLoginActivity() {
    Log.i("LOGING", "____ON_REQUEST_LOGIN______");
    registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::onActivityResult)
        .launch(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(getAuthProviders())
                .setLogo(R.drawable.logo)
                .setAuthMethodPickerLayout(getCustomAuthLayout())
                .setTheme(R.style.NoActionBarSysUITransparentTheme)
                .build());
  }

  private List<AuthUI.IdpConfig> getAuthProviders() {
    return Arrays.asList(
        new AuthUI.IdpConfig.GoogleBuilder()
            .setSignInOptions(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .build(),
        new AuthUI.IdpConfig.FacebookBuilder().build());
  }

  private AuthMethodPickerLayout getCustomAuthLayout() {
    return new AuthMethodPickerLayout.Builder(R.layout.activity_auth_method_picker)
        .setGoogleButtonId(R.id.google_login_btn)
        .setFacebookButtonId(R.id.fb_login_btn)
        .build();
  }

  private void onActivityResult(ActivityResult result) {
    Log.i("LOGING", "____ON_RESULT_LOGIN______");
    if (result.getResultCode() == Activity.RESULT_OK) {
      startMainActivity();
    } else {
      binding.getRoot().setVisibility(View.VISIBLE);
      if (result.getResultCode() == Activity.RESULT_CANCELED) {
        showLoginError(R.string.login_canceled);
      } else handleLoginFailure(result.getData());
    }
  }

  @SuppressWarnings({"ConstantConditions"})
  private void handleLoginFailure(Intent loginResponseData) {
    try {
      final FirebaseUiException responseError =
          IdpResponse.fromResultIntent(loginResponseData).getError();
      if (responseError.getErrorCode() == ErrorCodes.NO_NETWORK) {
        showLoginError(R.string.login_failed_network_error);
      } else {
        showLoginError(R.string.login_failed_unknown_error);
      }
    } catch (NullPointerException e) {
      showLoginError(R.string.login_failed_unknown_error);
    }
  }

  private void showLoginError(int resourceId) {
    binding.loginError.setVisibility(View.VISIBLE);
    binding.loginErrorText.setText(resourceId);
  }

  private void startMainActivity() {
    final Intent startMainActivityIntent = new Intent(this, MainActivity.class);
    startMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
    startActivity(startMainActivityIntent);
    finish();
  }
}
