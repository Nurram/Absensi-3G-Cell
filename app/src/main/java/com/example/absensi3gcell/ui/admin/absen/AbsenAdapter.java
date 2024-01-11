package com.example.absensi3gcell.ui.admin.absen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.absensi3gcell.databinding.AbsensiItemBinding;
import com.example.absensi3gcell.databinding.KaryawanItemBinding;
import com.example.absensi3gcell.model.AbsensiResponse;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AbsenAdapter extends RecyclerView.Adapter<AbsenAdapter.AbsenHolder> {
    private final List<AbsensiResponse> absens = new ArrayList<>();

    @NonNull
    @Override
    public AbsenHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AbsensiItemBinding binding = AbsensiItemBinding.inflate(inflater, parent, false);
        return new AbsenHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AbsenHolder holder, int position) {
        holder.bind(absens.get(position));
    }

    @Override
    public int getItemCount() {
        return absens.size();
    }

    protected class AbsenHolder extends RecyclerView.ViewHolder {
        AbsensiItemBinding binding;

        public AbsenHolder(AbsensiItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AbsensiResponse response) {
            DocumentSnapshot snapshot = response.getAbsensiData();
            binding.tvName.setText(snapshot.getString("name"));
            binding.tvNip.setText(snapshot.getString("nip"));
            binding.tvStore.setText(snapshot.getString("place"));
            binding.tvLocation.setText(snapshot.getString("location"));

            if(response.isAbsen()) {
                binding.llDetail.setVisibility(View.VISIBLE);
                binding.tvAbsen.setVisibility(View.GONE);
            } else {
                binding.llDetail.setVisibility(View.GONE);
                binding.tvAbsen.setVisibility(View.VISIBLE);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy. HH:mm", Locale.getDefault());
            String startDateString = sdf.format(snapshot.getLong("absentTime"));
            binding.tvDate.setText(startDateString);
        }
    }

    public void addItems(List<AbsensiResponse> karyawans) {
        this.absens.clear();
        this.absens.addAll(karyawans);
        notifyDataSetChanged();
    }
}
