package com.example.absensi3gcell.ui.admin.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.absensi3gcell.R;
import com.example.absensi3gcell.databinding.ActivityAdminDashboardBinding;
import com.example.absensi3gcell.ui.admin.absen.AbsenFragment;
import com.example.absensi3gcell.ui.admin.karyawan.KaryawanFragment;
import com.example.absensi3gcell.ui.admin.karyawanAdd.KaryawanAddActivity;
import com.example.absensi3gcell.ui.login.LoginActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {
    boolean showMenu = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAdminDashboardBinding binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        List<Fragment> fragments = new ArrayList<Fragment>() {{
            add(new AbsenFragment());
            add(new KaryawanFragment());
        }};

        moveFragment(fragments.get(0));
        binding.tabAdminDashboard.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int index = tab.getPosition();
                moveFragment(fragments.get(index));

                if(index == 0) {
                    showMenu = true;
                    binding.fabAdd.setVisibility(View.GONE);
                } else {
                    showMenu = false;
                    binding.fabAdd.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        binding.fabAdd.setOnClickListener(view -> {
            Intent i = new Intent(AdminDashboardActivity.this, KaryawanAddActivity.class);
            startActivity(i);
        });
    }

    private void moveFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_admin_dashboard, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(showMenu) {
            getMenuInflater().inflate(R.menu.admin_menu, menu);
        }

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.admin_export) {

        } else if(item.getItemId() == R.id.admin_logout) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();

            SharedPreferences preferences = getSharedPreferences("main", MODE_PRIVATE);
            preferences.edit().clear().apply();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
}