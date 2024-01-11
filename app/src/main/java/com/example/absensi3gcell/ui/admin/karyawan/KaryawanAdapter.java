package com.example.absensi3gcell.ui.admin.karyawan;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.absensi3gcell.databinding.KaryawanItemBinding;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class KaryawanAdapter extends RecyclerView.Adapter<KaryawanAdapter.KaryawanHolder> {
    private final List<DocumentSnapshot> karyawans = new ArrayList<>();

    @NonNull
    @Override
    public KaryawanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        KaryawanItemBinding binding = KaryawanItemBinding.inflate(inflater, parent, false);
        return new KaryawanHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull KaryawanHolder holder, int position) {
        holder.bind(karyawans.get(position));
    }

    @Override
    public int getItemCount() {
        return karyawans.size();
    }

    public void addItems(List<DocumentSnapshot> karyawans) {
        this.karyawans.clear();
        this.karyawans.addAll(karyawans);
        Log.d("TAG", "Size: " + karyawans.size());
        notifyDataSetChanged();
    }

    protected class KaryawanHolder extends RecyclerView.ViewHolder {
        KaryawanItemBinding binding;

        public KaryawanHolder(KaryawanItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DocumentSnapshot snapshot) {
            binding.tvName.setText(snapshot.getString("name"));
            binding.tvNip.setText(snapshot.getString("nip"));
            Log.d("TAG", snapshot.toString());
        }
    }
}
