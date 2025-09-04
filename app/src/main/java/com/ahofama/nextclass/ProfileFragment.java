package com.ahofama.nextclass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private ImageView ivProfileImage;
    private TextView tvUserName, tvUserEmail, tvUserId;
    private Button btnLogout;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Init Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvUserId = view.findViewById(R.id.tvUserId);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Load user profile
        loadUserProfile();

        return view;
    }

    private void loadUserProfile() {
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();

            // Fetch additional profile data from Firestore
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            // ✅ Firestore record found
                            showUserData(firebaseUser, document);
                        } else {
                            // ✅ No Firestore record → just use FirebaseAuth data
                            showUserData(firebaseUser, null);
                            Toast.makeText(requireContext(), "Using account info only", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // ⚠️ Silent fallback, no annoying toast
                        // Still show FirebaseAuth data so UI doesn't break
                        showUserData(firebaseUser, null);
                    });

            btnLogout.setText(R.string.logout);
            btnLogout.setOnClickListener(v -> performLogout());

        } else {
            // No Firebase user → guest view
            tvUserName.setText("Guest");
            tvUserEmail.setText("Not signed in");
            tvUserId.setText("N/A");
            ivProfileImage.setImageResource(R.drawable.ic_person);

            btnLogout.setText(R.string.login);
            btnLogout.setOnClickListener(v -> redirectToLogin());
        }
    }


    private void showUserData(FirebaseUser firebaseUser, @Nullable DocumentSnapshot document) {
        // Prefer Firestore fields if available
        String name = (document != null && document.contains("name"))
                ? document.getString("name")
                : firebaseUser.getDisplayName();

        String email = (document != null && document.contains("email"))
                ? document.getString("email")
                : firebaseUser.getEmail();

        String photoUrl = (document != null && document.contains("photoUrl"))
                ? document.getString("photoUrl")
                : (firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null);

        String uid = firebaseUser.getUid();

        tvUserName.setText(name != null ? name : "Unknown User");
        tvUserEmail.setText(email != null ? email : "No email available");
        tvUserId.setText(uid != null && uid.length() > 8 ? "****" + uid.substring(0, 8) : "Unknown");

        if (photoUrl != null) {
            Glide.with(this)
                    .load(photoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .circleCrop()
                    .into(ivProfileImage);
        } else {
            ivProfileImage.setImageResource(R.drawable.ic_person);
        }
    }

    private void performLogout() {
        btnLogout.setText(R.string.logging_out);
        btnLogout.setEnabled(false);

        auth.signOut();
        clearUserData();
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        redirectToLogin(); // 🚀 go back to LoginActivity
    }

    private void clearUserData() {
        SharedPreferences sharedPrefs = requireContext().getSharedPreferences("user_prefs", requireContext().MODE_PRIVATE);
        sharedPrefs.edit().clear().apply();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
