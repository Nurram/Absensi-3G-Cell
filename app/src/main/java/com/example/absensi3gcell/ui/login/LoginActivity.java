package com.example.absensi3gcell.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Toast;

import com.example.absensi3gcell.databinding.ActivityLoginBinding;
import com.example.absensi3gcell.ui.admin.dashboard.AdminDashboardActivity;
import com.example.absensi3gcell.ui.user.dashboard.UserDashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(view -> {
            Editable nip = binding.etNip.getText();
            Editable password = binding.etPassword.getText();

            if(nip == null || nip.length() == 0 || password == null || password.length() == 0) {
                showToast("Mohon isi semua field!");
            } else {
                login(nip.toString(), password.toString());
            }
        });
    }

    private void login(String nip, String password) {
        setLoading(true);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore
                .collection("users")
                .where(Filter.equalTo("nip", nip))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                    if(docs.isEmpty()) {
                        setLoading(false);
                        showToast("User tidak ditemukan");
                    } else {
                        DocumentSnapshot snapshot = docs.get(0);
                        String email = snapshot.getString("email");
                        boolean isAdmin = Boolean.TRUE.equals(snapshot.getBoolean("isAdmin"));

                        doLogin(email, password, snapshot.getString("name"), isAdmin);
                    }
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    showToast(e.getMessage());
                });
    }

    private void doLogin(String email, String password, String name, boolean isAdmin) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            String id = authResult.getUser().getUid();

            SharedPreferences pref = getSharedPreferences("main", MODE_PRIVATE);
            pref
                    .edit()
                    .putString("userId", id)
                    .putString("name", name)
                    .putBoolean("isAdmin", isAdmin)
                    .apply();

            Intent i;

            if(isAdmin) {
                i = new Intent(this, AdminDashboardActivity.class);
            } else {
                i = new Intent(this, UserDashboardActivity.class);
            }

            startActivity(i);

            finish();
        }).addOnFailureListener(e -> {
            setLoading(false);
            showToast(e.getMessage());
        });
    }

    private void setLoading(Boolean loading) {
        if(loading) {
            binding.pbLogin.setVisibility(View.VISIBLE);
            binding.btnLogin.setVisibility(View.GONE);
        } else {
            binding.pbLogin.setVisibility(View.GONE);
            binding.btnLogin.setVisibility(View.VISIBLE);
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}