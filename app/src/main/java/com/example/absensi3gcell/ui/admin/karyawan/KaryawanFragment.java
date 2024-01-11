package com.example.absensi3gcell.ui.admin.karyawan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.absensi3gcell.databinding.FragmentKaryawanBinding;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;

public class KaryawanFragment extends Fragment {
    private FragmentKaryawanBinding binding;
    private KaryawanAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentKaryawanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new KaryawanAdapter();
        binding.rv.setAdapter(adapter);
        binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        fetchKaryawans();
    }

    private void fetchKaryawans() {
        setLoading(true);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore
                .collection("users")
                .where(Filter.equalTo("admin", false))
                .addSnapshotListener((value, error) -> {
                    setLoading(false);

                    if (error != null) {
                        Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        if (value != null) {
                            adapter.addItems(value.getDocuments());
                        }
                    }
                });
    }

    private void setLoading(Boolean loading) {
        if (loading) {
            binding.pb.setVisibility(View.VISIBLE);
            binding.rv.setVisibility(View.GONE);
        } else {
            binding.pb.setVisibility(View.GONE);
            binding.rv.setVisibility(View.VISIBLE);
        }
    }
}