package com.example.absensi3gcell.ui.user.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.absensi3gcell.R;
import com.example.absensi3gcell.databinding.ActivityUserDashboardBinding;
import com.example.absensi3gcell.ui.user.absen.AbsenFragment;
import com.example.absensi3gcell.ui.user.addAbsent.AddAbsentActivity;
import com.example.absensi3gcell.ui.user.profile.ProfileFragment;

public class UserDashboardActivity extends AppCompatActivity {
    private ActivityUserDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        moveFragment(new AbsenFragment());
        binding.botnav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.user_menu_absen) {
                binding.fabAdd.setVisibility(View.VISIBLE);
                moveFragment(new AbsenFragment());
            } else {
                binding.fabAdd.setVisibility(View.GONE);
                moveFragment(new ProfileFragment());
            }
            return false;
        });
        binding.fabAdd.setOnClickListener(view -> {
            Intent i = new Intent(UserDashboardActivity.this, AddAbsentActivity.class);
            startActivity(i);
        });
    }

    private void moveFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_user_dashboard, fragment).commit();
    }
}