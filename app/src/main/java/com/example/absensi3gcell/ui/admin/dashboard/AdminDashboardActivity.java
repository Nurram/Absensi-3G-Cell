package com.example.absensi3gcell.ui.admin.dashboard;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.absensi3gcell.R;
import com.example.absensi3gcell.databinding.ActivityAdminDashboardBinding;
import com.example.absensi3gcell.model.AbsensiResponse;
import com.example.absensi3gcell.ui.admin.absen.AbsenFragment;
import com.example.absensi3gcell.ui.admin.karyawan.KaryawanFragment;
import com.example.absensi3gcell.ui.admin.karyawanAdd.KaryawanAddActivity;
import com.example.absensi3gcell.ui.login.LoginActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AdminDashboardActivity extends AppCompatActivity {
    boolean showMenu = true;
    private ActivityResultLauncher<Intent> launcher; // Initialise this object in Activity.onCreate()
    private Uri baseDocumentTreeUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                o -> {
                    if (o.getResultCode() == Activity.RESULT_OK) {
                        baseDocumentTreeUri = Objects.requireNonNull(o.getData()).getData();
                        exportExcel();
                    } else {
                        Log.e("FileUtility", "Some Error Occurred : " + o.getData());

                    }
                });
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
        if(item.getItemId() == R.id.admin_logout) {
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

    List<AbsensiResponse> absensiList;
    Long startDateL;
    Long endDateL;

    public void launchIntent(List<AbsensiResponse> absensiList, Long startDateL, Long endDateL) {
        this.absensiList = absensiList;
        this.startDateL = startDateL;
        this.endDateL = endDateL;

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        launcher.launch(intent);
    }

    private void exportExcel()
    {
        //nama file adalah history-absensi.xls
        //file akan disimpna di folder Download
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String startDate = sdf.format(new Date(startDateL));
        String endDate = sdf.format(new Date(endDateL));


        DocumentFile directory = DocumentFile.fromTreeUri(this, baseDocumentTreeUri);
        DocumentFile file = directory.createFile("application/vnd.ms-excel", "history-absensi-" + startDate + " - " + endDate + ".xls");

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Absensi");

        Row headerRow = sheet.createRow((0));
        Cell nameCell = headerRow.createCell(0);
        nameCell.setCellValue("Nama");

        Cell nipCell = headerRow.createCell(1);
        nipCell.setCellValue("NIP");

        Cell placeCell = headerRow.createCell(2);
        placeCell.setCellValue("Tempat");

        Cell locationCell = headerRow.createCell(3);
        locationCell.setCellValue("Lokasi");

        Cell dateCell = headerRow.createCell(3);
        dateCell.setCellValue("Tanggal");

        for(int i = 0; i < absensiList.size(); i++) {
            Row row = sheet.createRow((i+1));
            AbsensiResponse absensiResponse = absensiList.get(i);
            DocumentSnapshot karyawan = absensiResponse.getKaryawanData();
            DocumentSnapshot absen = absensiResponse.getAbsensiData();

             nameCell = row.createCell(0);
            nameCell.setCellValue(karyawan.getString("name"));

             nipCell = row.createCell(1);
            nipCell.setCellValue(karyawan.getString("nip"));

            if(absen != null) {
                 placeCell = row.createCell(2);
                placeCell.setCellValue(absen.getString("place"));

                 locationCell = row.createCell(3);
                locationCell.setCellValue(absen.getString("location"));

                 dateCell = row.createCell(3);
                SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault());
                Date date = new Date(absen.getLong("absentTime"));
                dateCell.setCellValue(format.format(date));
            }
        }

        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(file.getUri(), "w");
            FileOutputStream fileOutputStream= new FileOutputStream(pfd.getFileDescriptor());
            workbook.write(fileOutputStream);
            Log.e("EXCEL", "Writing file" + file);

            fileOutputStream.flush();
            fileOutputStream.close();

            Log.i("EXCEL", fileOutputStream.toString());
            Toast.makeText(this, "File berhasil disimpan", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("EXCEL", e.getMessage());
        }
    }
}