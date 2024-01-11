package com.example.absensi3gcell.ui.admin.absen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.Toast;

import com.example.absensi3gcell.R;
import com.example.absensi3gcell.databinding.FragmentAbsenBinding;
import com.example.absensi3gcell.model.AbsensiResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AbsenFragment extends Fragment {
    private FragmentAbsenBinding binding;

    private Long startDate = new Date().getTime();
    private Long endDate = new Date().getTime();

    private final List<AbsensiResponse> absensiList = new ArrayList<>();
    private final List<DocumentSnapshot> users = new ArrayList<>();

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    Calendar startCalendar = Calendar.getInstance();
    Calendar endCalendar = Calendar.getInstance();

    private AbsenAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAbsenBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String startDateString = sdf.format(new Date(startDate));
        String endDateString = sdf.format(new Date(endDate));

        String selectedDateRange = startDateString + " - " + endDateString;

        binding.etDate.setText(selectedDateRange);

        binding.etDate.setOnClickListener(view1 -> pickDate());
        binding.btnSearch.setOnClickListener(view12 -> filterData());

        adapter = new AbsenAdapter();
        binding.rv.setAdapter(adapter);
        binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));

        getUsers();
    }

    private void pickDate() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select a date range");

        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();
        datePicker.addOnPositiveButtonClickListener(selection -> {

            startCalendar.setTimeInMillis(selection.first);
            startCalendar.set(Calendar.HOUR, 0);
            startCalendar.set(Calendar.MINUTE, 0);
            startCalendar.set(Calendar.SECOND, 0);
            startCalendar.set(Calendar.MILLISECOND, 0);

            endCalendar.setTimeInMillis(selection.second);
            endCalendar.set(Calendar.HOUR, 23);
            endCalendar.set(Calendar.MINUTE, 59);
            endCalendar.set(Calendar.SECOND, 59);
            endCalendar.set(Calendar.MILLISECOND, 59);

            startDate = startCalendar.getTimeInMillis();
            endDate = endCalendar.getTimeInMillis();

            String startDateString = sdf.format(new Date(startDate));
            String endDateString = sdf.format(new Date(endDate));

            String selectedDateRange = startDateString + " - " + endDateString;

            binding.etDate.setText(selectedDateRange);
        });

        datePicker.show(getChildFragmentManager(), "DATE_PICKER");
    }

    private void filterData() {
        setLoading(true);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore
                .collection("absensi")
                .where(Filter.greaterThanOrEqualTo("absentTime", startDate))
                .where(Filter.lessThanOrEqualTo("absentTime", endDate))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    setLoading(false);
                    absensiList.clear();

                    queryDocumentSnapshots.getDocuments().forEach(snapshot -> {
                        String karyawanId = snapshot.getString("karyawanId");
                        DocumentSnapshot karyawanData = users.stream().filter(snapshot1 ->
                                snapshot1
                                        .getId()
                                        .equals(karyawanId)
                        ).collect(Collectors.toList()).get(0);

                        absensiList.add(
                                new AbsensiResponse(karyawanData, snapshot, false)
                        );
                    });

                    adapter.addItems(absensiList);
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void getUsers() {
        setLoading(true);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore
                .collection("users")
                .where(Filter.equalTo("isAdmin", false))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    users.clear();
                    users.addAll(queryDocumentSnapshots.getDocuments());

                    filterData();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setLoading(Boolean loading) {
        if(loading) {
            binding.pb.setVisibility(View.VISIBLE);
            binding.rv.setVisibility(View.GONE);
        } else {
            binding.pb.setVisibility(View.GONE);
            binding.rv.setVisibility(View.VISIBLE);
        }
    }

    private void exportExcel()
    {
        //nama file adalah history-absensi.xls
        //file akan disimpna di folder Download
        String startDate = sdf.format(startCalendar);
        String endDate = sdf.format(endCalendar);

        File file = new File(Environment.getExternalStorageDirectory().toString() + "/Download/" + "/history-absensi-" + startDate + " - " + endDate + ".xls");

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Absensi");

        for(int i = 0; i < absensiList.size(); i++) {
            Row row = sheet.createRow((i+1));
            AbsensiResponse absensiResponse = absensiList.get(i);
            DocumentSnapshot karyawan = absensiResponse.getKaryawanData();
            DocumentSnapshot absen = absensiResponse.getAbsensiData();

            Cell nameCell = row.createCell(0);
            nameCell.setCellValue(karyawan.getString("name"));

            Cell nipCell = row.createCell(1);
            nipCell.setCellValue(karyawan.getString("nip"));

            Cell placeCell = row.createCell(2);
            placeCell.setCellValue(absen.getString("place"));

            Cell locationCell = row.createCell(3);
            locationCell.setCellValue(absen.getString("location"));

            Cell dateCell = row.createCell(3);
            SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault());
            Date date = new Date(absen.getLong("absentTime"));
            dateCell.setCellValue(format.format(date));
        }

        try {
            if (!file.exists()){
                file.createNewFile();
            }

            FileOutputStream fileOutputStream= new FileOutputStream(file);
            workbook.write(fileOutputStream);
            Log.e("EXCEL", "Writing file" + file);

            fileOutputStream.flush();
            fileOutputStream.close();

            Log.i("EXCEL", fileOutputStream.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("EXCEL", e.getMessage());
        }

        Toast.makeText(getContext(), "Disimpan di " + Environment.getExternalStorageDirectory().toString() + "/Download/", Toast.LENGTH_SHORT).show();
    }
}