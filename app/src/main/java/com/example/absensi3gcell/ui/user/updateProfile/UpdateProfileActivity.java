package com.example.absensi3gcell.ui.user.updateProfile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.absensi3gcell.databinding.ActivityUpdateProfileBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

public class UpdateProfileActivity extends AppCompatActivity {
    private ActivityUpdateProfileBinding binding;
    private Uri pickedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ib.setOnClickListener(view -> ImagePicker
                .with(UpdateProfileActivity.this)
                .maxResultSize(1080, 1080)
                .start());
        binding.btnAdd.setOnClickListener(view -> {
            Editable name = binding.etName.getText();

            if (name != null && name.length() > 0) {
                SharedPreferences pref = getSharedPreferences("main", MODE_PRIVATE);
                String userId = pref.getString("userId", "");

                if (pickedImage != null) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    storage.getReference(userId + "/profile").putFile(pickedImage)
                            .addOnSuccessListener(taskSnapshot -> {
                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                                    updateData(name.toString(), uri, userId + "/profile");
                                });
                            });
                } else {
                    updateData(name.toString(), null, null);
                }


            }
        });
        fetchUserData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            pickedImage = data.getData();
            binding.ib.setImageURI(pickedImage);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateData(String name, Uri imageUrl, String imagePath) {
        SharedPreferences pref = getSharedPreferences("main", MODE_PRIVATE);
        String userId = pref.getString("userId", "");

        HashMap<String, Object> request = new HashMap<String, Object>() {{
            put("name", name);
        }};

        if (imageUrl != null && imagePath != null) {
            request.put("imageUrl", imageUrl);
            request.put("imagePath", imagePath);
        }

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(userId).update(request)
                .addOnSuccessListener(unused -> finish())
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchUserData() {
        SharedPreferences pref = getSharedPreferences("main", MODE_PRIVATE);
        String userId = pref.getString("userId", "");

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(userId).get()
                .addOnSuccessListener(snapshot -> {
                    setLoading(false);
                    binding.etName.setText(snapshot.getString("name"));
                    binding.etEmail.setText(snapshot.getString("email"));
                    binding.etNip.setText(snapshot.getString("nip"));

                    if (snapshot.getString("imageUrl") != null) {
                        Glide.with(this).load(snapshot.getString("imageProfile")).into(binding.ib);
                    }
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void setLoading(Boolean loading) {
        if (loading) {
            binding.pdAdd.setVisibility(View.VISIBLE);
            binding.content.setVisibility(View.GONE);
        } else {
            binding.pdAdd.setVisibility(View.GONE);
            binding.content.setVisibility(View.VISIBLE);
        }
    }
}