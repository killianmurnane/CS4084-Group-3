package com.example.cs4084_group_3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import android.util.Log;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import java.util.concurrent.Executors;

public class AuthActivity extends AppCompatActivity {

    private static final String TAG = "AuthActivity";
    private static final String WEB_CLIENT_ID =
            "304922438341-ku8lsm0580p761hg0mr67oud7md7ulcl.apps.googleusercontent.com";

    private EditText inputName;
    private EditText inputEmail;
    private EditText inputPassword;
    private TextView modeTitle;
    private TextView modeToggle;
    private Button submitButton;
    private Button btnGoogleSignIn;

    private boolean isRegisterMode = false;
    private FirebaseAuth firebaseAuth;
    private CredentialManager credentialManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        credentialManager = CredentialManager.create(this);

        if (firebaseAuth.getCurrentUser() != null) {
            openMain();
            return;
        }

        setContentView(R.layout.activity_auth);

        inputName = findViewById(R.id.inputRegisterName);
        inputEmail = findViewById(R.id.inputAuthEmail);
        inputPassword = findViewById(R.id.inputAuthPassword);
        modeTitle = findViewById(R.id.authModeTitle);
        modeToggle = findViewById(R.id.authModeToggle);
        submitButton = findViewById(R.id.authSubmitButton);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);

        updateModeUi();

        modeToggle.setOnClickListener(v -> {
            isRegisterMode = !isRegisterMode;
            updateModeUi();
        });

        submitButton.setOnClickListener(v -> handleSubmit());
        btnGoogleSignIn.setOnClickListener(v -> handleGoogleSignIn());
    }

    private void handleSubmit() {
        String name = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty() || (isRegisterMode && name.isEmpty())) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        submitButton.setEnabled(false);

        if (isRegisterMode) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Profile profile = new Profile();
                        profile.name = name;
                        profile.email = email;
                        // Write locally first, then explicitly sync the profile we have
                        // in hand so name + email are guaranteed to reach the DB
                        ProfileStore.writeProfile(this, profile);
                        FirebaseSyncManager.syncProfile(this, profile);
                        openMain();
                    } else {
                        submitButton.setEnabled(true);
                        String msg = task.getException() != null
                                ? task.getException().getMessage()
                                : "Registration failed";
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    }
                });
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseSyncManager.syncAllFromLocal(this, (success, errorMessage) -> {
                            if (!success) {
                                Toast.makeText(this, "Cloud sync failed: " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                        });
                        openMain();
                    } else {
                        submitButton.setEnabled(true);
                        Toast.makeText(this, "Invalid email or password", Toast.LENGTH_LONG).show();
                    }
                });
        }
    }

    private void updateModeUi() {
        if (isRegisterMode) {
            modeTitle.setText("Create account");
            submitButton.setText("Register");
            modeToggle.setText("Already have an account? Login");
            inputName.setVisibility(View.VISIBLE);
            btnGoogleSignIn.setVisibility(View.GONE);
        } else {
            modeTitle.setText("Welcome back");
            submitButton.setText("Login");
            modeToggle.setText("No account yet? Register");
            inputName.setVisibility(View.GONE);
            btnGoogleSignIn.setVisibility(View.VISIBLE);
        }
    }

    private void openMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void handleGoogleSignIn() {
        btnGoogleSignIn.setEnabled(false);

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(WEB_CLIENT_ID)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                this,
                request,
                null,
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        runOnUiThread(() -> handleGoogleCredential(result));
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        runOnUiThread(() -> {
                            btnGoogleSignIn.setEnabled(true);
                            Log.e(TAG, "Google sign-in failed", e);
                            if (e instanceof NoCredentialException) {
                                Toast.makeText(AuthActivity.this,
                                        "No Google account on this device. Opening Google sign-in...",
                                        Toast.LENGTH_LONG).show();
                                openGoogleAccountSignIn();
                            } else {
                                Toast.makeText(AuthActivity.this,
                                        "Google sign-in failed: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
        );
    }

    private void handleGoogleCredential(GetCredentialResponse result) {
        try {
            GoogleIdTokenCredential googleIdToken =
                    GoogleIdTokenCredential.createFrom(result.getCredential().getData());
            AuthCredential firebaseCredential =
                    GoogleAuthProvider.getCredential(googleIdToken.getIdToken(), null);

            firebaseAuth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            boolean isNewUser = task.getResult().getAdditionalUserInfo() != null
                                    && task.getResult().getAdditionalUserInfo().isNewUser();
                            if (isNewUser) {
                                Profile profile = new Profile();
                                profile.name = task.getResult().getUser().getDisplayName();
                                profile.email = task.getResult().getUser().getEmail();
                                ProfileStore.writeProfile(AuthActivity.this, profile);
                                FirebaseSyncManager.syncProfile(AuthActivity.this, profile);
                            } else {
                                FirebaseSyncManager.syncAllFromLocal(AuthActivity.this, null);
                            }
                            openMain();
                        } else {
                            btnGoogleSignIn.setEnabled(true);
                            Toast.makeText(AuthActivity.this,
                                    "Google sign-in failed", Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (Exception e) {
            btnGoogleSignIn.setEnabled(true);
            Log.e(TAG, "Failed to parse Google credential", e);
            Toast.makeText(this, "Google sign-in error", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGoogleAccountSignIn() {
        try {
            Intent addAccountIntent = new Intent(Settings.ACTION_ADD_ACCOUNT);
            addAccountIntent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, new String[]{"com.google"});
            startActivity(addAccountIntent);
        } catch (Exception ignored) {
            try {
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://accounts.google.com/signin")
                );
                startActivity(browserIntent);
            } catch (Exception browserError) {
                Toast.makeText(this,
                        "Unable to open Google sign-in on this device",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}