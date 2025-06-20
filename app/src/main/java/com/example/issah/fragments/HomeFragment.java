package com.example.issah.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.issah.LoginActivity;
import com.example.issah.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class HomeFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvUserName, tvUserEmail;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        MaterialToolbar topAppBar = view.findViewById(R.id.topAppBar);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);

        // Set up toolbar menu
        topAppBar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_profile) {
                Toast.makeText(getContext(), "Profile clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.menu_settings) {
                Toast.makeText(getContext(), "Settings clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.menu_logout) {
                mAuth.signOut();
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
                return true;
            }

            return false;
        });

        // Load user data
        loadUserData();
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            db.collection("users").document(uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Get user data from Firestore
                                String firstName = document.getString("firstName");
                                String lastName = document.getString("lastName");
                                String email = document.getString("email");

                                // Update UI
                                tvUserName.setText(firstName + " " + lastName);
                                tvUserEmail.setText(email);
                            } else {
                                Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}