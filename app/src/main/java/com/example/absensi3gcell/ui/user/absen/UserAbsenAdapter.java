package com.example.absensi3gcell.ui.user.absen;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.absensi3gcell.databinding.AbsensiItemBinding;
import com.example.absensi3gcell.model.AbsensiResponse;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserAbsenAdapter extends RecyclerView.Adapter<UserAbsenAdapter.UserAbsenHolder> {
    private final List<AbsensiResponse> absens = new ArrayList<>();
    private Context context;

    public UserAbsenAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public UserAbsenHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AbsensiItemBinding binding = AbsensiItemBinding.inflate(inflater, parent, false);
        return new UserAbsenHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAbsenHolder holder, int position) {
        holder.bind(absens.get(position));
    }

    @Override
    public int getItemCount() {
        return absens.size();
    }

    protected class UserAbsenHolder extends RecyclerView.ViewHolder {
        AbsensiItemBinding binding;

        public UserAbsenHolder(AbsensiItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AbsensiResponse response) {
            DocumentSnapshot snapshot = response.getAbsensiData();
            DocumentSnapshot snapshot1 = response.getKaryawanData();

            binding.tvName.setText(snapshot.getString("name"));
            binding.tvNip.setText(snapshot1.getString("nip"));
            binding.tvStore.setText(snapshot.getString("place"));
            binding.tvLocation.setText(snapshot.getString("location"));

            binding.llDetail.setVisibility(View.VISIBLE);
            binding.tvAbsen.setVisibility(View.GONE);

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy. HH:mm", Locale.getDefault());
            String startDateString = sdf.format(new Date(snapshot.getLong("absentTime")));
            binding.tvDate.setText(startDateString);

            Glide.with(context).load(response.getAbsensiData().getString("imageUrl")).into(binding.iv);
        }
    }

    public void addItems(List<AbsensiResponse> karyawans) {
        this.absens.clear();
        this.absens.addAll(karyawans);
        notifyDataSetChanged();
    }
}
