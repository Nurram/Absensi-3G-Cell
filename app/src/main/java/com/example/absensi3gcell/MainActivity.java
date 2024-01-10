package com.example.absensi3gcell;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.absensi3gcell.ui.admin.dashboard.AdminDashboardActivity;
import com.example.absensi3gcell.ui.login.LoginActivity;
import com.example.absensi3gcell.ui.user.dashboard.UserDashboardActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences pref = getSharedPreferences("main", MODE_PRIVATE);
        String userId = pref.getString("userId", "");
        boolean isAdmin = pref.getBoolean("isAdmin", false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i;

                if(userId.isEmpty()) {
                    i = new Intent(MainActivity.this, LoginActivity.class);
                } else {
                    if(isAdmin) {
                        i = new Intent(MainActivity.this, AdminDashboardActivity.class);
                    } else {
                        i = new Intent(MainActivity.this, UserDashboardActivity.class);
                    }
                }

                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        }, 1500);
    }
}