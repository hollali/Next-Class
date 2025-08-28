package com.ahofama.nextclass;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.*;
import com.google.android.material.button.MaterialButton;
import com.google.android.gms.tasks.Task;



public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        firebaseAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // from google-services.json
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        MaterialButton btnGoogleLogin = findViewById(R.id.btn_google_login);
        btnGoogleLogin.setOnClickListener(v -> signIn());
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Toast.makeText(this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, DashboardActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
