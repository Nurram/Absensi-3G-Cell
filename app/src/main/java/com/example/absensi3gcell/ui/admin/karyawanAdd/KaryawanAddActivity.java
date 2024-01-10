package com.example.absensi3gcell.ui.admin.karyawanAdd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Toast;

import com.example.absensi3gcell.R;
import com.example.absensi3gcell.databinding.ActivityKaryawanAddBinding;
import com.example.absensi3gcell.model.KarywanAddRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class KaryawanAddActivity extends AppCompatActivity {
    private ActivityKaryawanAddBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityKaryawanAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnAdd.setOnClickListener(view -> add());
    }

    private void add() {
        Editable nip = binding.etNip.getText();
        Editable name = binding.etName.getText();
        Editable email = binding.etEmail.getText();
        Editable password = binding.etPassword.getText();

        if(
                nip == null ||
                nip.length() == 0 ||
                name == null ||
                name.length() == 0 ||
                email == null ||
                email.length() == 0 ||
                password == null ||
                password.length() == 0
        ) {
            Toast.makeText(this, "Silahkan isi semua field!", Toast.LENGTH_SHORT).show();
        } else {
            setLoading(true);

            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(email.toString(), password.toString())
                    .addOnSuccessListener(authResult -> {
                        setLoading(false);

                        String id = authResult.getUser().getUid();
                        KarywanAddRequest request = new KarywanAddRequest(
                                id, nip.toString(), name.toString(), email.toString(), password.toString()
                        );

                        storeUserData(request);
                    })
                    .addOnFailureListener(e -> {
                        setLoading(false);
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void storeUserData(KarywanAddRequest request) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore
                .collection("users")
                .where(Filter.equalTo("nip", request.getNip()))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(queryDocumentSnapshots.getDocuments().isEmpty()) {
                        firestore
                                .collection("users")
                                .add(request)
                                .addOnSuccessListener(documentReference -> finish())
                                .addOnFailureListener(e -> {
                                    setLoading(false);
                                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        setLoading(false);
                        Toast.makeText(this, "Nip telah digunakan!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setLoading(Boolean loading) {
        if(loading) {
            binding.pdAdd.setVisibility(View.VISIBLE);
            binding.btnAdd.setVisibility(View.GONE);
        } else {
            binding.pdAdd.setVisibility(View.GONE);
            binding.btnAdd.setVisibility(View.VISIBLE);
        }
    }
}