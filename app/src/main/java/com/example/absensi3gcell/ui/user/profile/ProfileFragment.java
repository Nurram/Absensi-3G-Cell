package com.example.absensi3gcell.ui.user.profile;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.absensi3gcell.R;
import com.example.absensi3gcell.databinding.FragmentProfileBinding;
import com.example.absensi3gcell.ui.login.LoginActivity;
import com.example.absensi3gcell.ui.user.ChangePasswordActivity;
import com.example.absensi3gcell.ui.user.updateProfile.UpdateProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getProfile();
        binding.logout.setOnClickListener(view1 -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();

            SharedPreferences preferences = getContext().getSharedPreferences("main", MODE_PRIVATE);
            preferences.edit().clear().apply();

            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
        binding.change.setOnClickListener(view12 -> {
            Intent i = new Intent(getContext(), ChangePasswordActivity.class);
            startActivity(i);
        });
        binding.update.setOnClickListener(view12 -> {
            Intent i = new Intent(getContext(), UpdateProfileActivity.class);
            startActivity(i);
        });
    }

    private void getProfile() {
        SharedPreferences pref = getContext().getSharedPreferences("main", MODE_PRIVATE);
        String userId = pref.getString("userId", "");

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore
                .collection("users")
                .document(userId)
                .addSnapshotListener((value, error) -> {
                    if(error != null) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        binding.tvName.setText(value.getString("name"));
                        binding.tvNip.setText("NIP: " + value.getString("nip"));

                        if(value.getString("imageUrl") != null) {
                            Glide.with(this).load(value.getString("imageUrl")).into(binding.ivImage);
                        }
                    }
                });

    }
}