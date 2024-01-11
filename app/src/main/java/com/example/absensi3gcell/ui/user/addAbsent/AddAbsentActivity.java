package com.example.absensi3gcell.ui.user.addAbsent;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.absensi3gcell.databinding.ActivityAddAbsentBinding;
import com.example.absensi3gcell.model.AbsensiRequest;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddAbsentActivity extends AppCompatActivity {
    private ActivityAddAbsentBinding binding;
    private Uri pickedImage;
    private Date date = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddAbsentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ib.setOnClickListener(view -> ImagePicker
                .with(AddAbsentActivity.this)
                .maxResultSize(1080, 1080)
                .start());

        SharedPreferences pref = getSharedPreferences("main", MODE_PRIVATE);
        String name = pref.getString("name", "");
        binding.etName.setText(name);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault());
        String currentDateTime = simpleDateFormat.format(date);
        binding.etDate.setText(currentDateTime);
        binding.btnAdd.setOnClickListener(view -> {
            addAbsent();
        });
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

    private void addAbsent() {
        setLoading(true);

        if (pickedImage == null ||
                binding.etEmail.getText() == null ||
                binding.etEmail.getText().length() == 0 ||
                binding.etPassword.getText() == null ||
                binding.etPassword.length() == 0
        ) {
            Toast.makeText(this, "Silahkan isi semua field!", Toast.LENGTH_SHORT).show();
        } else {
            setLoading(true);

            SharedPreferences pref = getSharedPreferences("main", MODE_PRIVATE);
            String userId = pref.getString("userId", "");
            String path = userId + Calendar.getInstance().getTimeInMillis();

            FirebaseStorage storage = FirebaseStorage.getInstance();
            storage
                    .getReference(path)
                    .putFile(pickedImage)
                    .addOnSuccessListener(taskSnapshot ->
                            taskSnapshot
                                    .getStorage()
                                    .getDownloadUrl()
                                    .addOnSuccessListener(uri -> storeData(uri, path))
                                    .addOnFailureListener(e -> {
                                        setLoading(false);
                                        Toast.makeText(AddAbsentActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }))
                    .addOnFailureListener(e -> {
                        setLoading(false);
                        Toast.makeText(AddAbsentActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void storeData(Uri uri, String path) {
        SharedPreferences pref = getSharedPreferences("main", MODE_PRIVATE);
        String userId = pref.getString("userId", "");

        AbsensiRequest request = new AbsensiRequest(
                userId,
                binding.etName.getText().toString(),
                binding.etEmail.getText().toString(),
                binding.etPassword.getText().toString(),
                date.getTime(),
                uri,
                path
        );

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore
                .collection("absensi")
                .add(request)
                .addOnSuccessListener(documentReference -> {
                    setLoading(false);
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(AddAbsentActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    storage.getReference(path).delete();
                });
    }

    private void setLoading(Boolean loading) {
        if (loading) {
            binding.pdAdd.setVisibility(View.VISIBLE);
            binding.btnAdd.setVisibility(View.GONE);
        } else {
            binding.pdAdd.setVisibility(View.GONE);
            binding.btnAdd.setVisibility(View.VISIBLE);
        }
    }
}