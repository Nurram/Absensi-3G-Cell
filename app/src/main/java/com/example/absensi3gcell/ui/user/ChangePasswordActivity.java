package com.example.absensi3gcell.ui.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.absensi3gcell.R;
import com.example.absensi3gcell.databinding.ActivityChangePasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

public class ChangePasswordActivity extends AppCompatActivity {
    private ActivityChangePasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btn.setOnClickListener(view -> {
            SharedPreferences pref = getSharedPreferences("main", MODE_PRIVATE);
            String userId = pref.getString("userId", "");

            if(binding.etOld.getText() == null || binding.etOld.getText().length() == 0 |
            binding.etNew.getText() == null || binding.etNew.getText().length() == 0 ||
            binding.etConfirmation.getText() == null || binding.etConfirmation.getText().length() == 0) {
                Toast.makeText(this, "Silahkan isi semua field!", Toast.LENGTH_SHORT).show();
            } else if(!binding.etNew.getText().toString().equals(binding.etConfirmation.getText().toString())) {
                Toast.makeText(this, "Password tidak sesuai!", Toast.LENGTH_SHORT).show();
            } else {
                setLoading(true);

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("users").document(userId).get()
                        .addOnSuccessListener(snapshot ->
                                changePassword(snapshot.getString("email")))
                        .addOnFailureListener(e -> {
                            setLoading(false);
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private void changePassword(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        AuthCredential credential = EmailAuthProvider
                .getCredential(email, binding.etOld.getText().toString());
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    setLoading(false);

                    if (task.isSuccessful()) {
                        user.updatePassword(binding.etNew.getText().toString()).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                finish();
                                Toast.makeText(ChangePasswordActivity.this, "Password telah diubah", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ChangePasswordActivity.this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setLoading(Boolean loading) {
        if(loading) {
            binding.pb.setVisibility(View.VISIBLE);
            binding.btn.setVisibility(View.GONE);
        } else {
            binding.pb.setVisibility(View.GONE);
            binding.btn.setVisibility(View.VISIBLE);
        }
    }
}