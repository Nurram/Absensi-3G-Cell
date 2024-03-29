package com.example.absensi3gcell.ui.admin.absen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.absensi3gcell.databinding.FragmentAbsenBinding;
import com.example.absensi3gcell.model.AbsensiResponse;
import com.example.absensi3gcell.ui.admin.dashboard.AdminDashboardActivity;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class AbsenFragment extends Fragment {
    private final List<AbsensiResponse> absensiList = new ArrayList<>();
    private final List<DocumentSnapshot> users = new ArrayList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    Calendar startCalendar = Calendar.getInstance();
    Calendar endCalendar = Calendar.getInstance();
    private FragmentAbsenBinding binding;
    private Long startDate = new Date().getTime();
    private Long endDate = new Date().getTime();
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

        startCalendar.setTimeInMillis(startDate);
        startCalendar.set(Calendar.HOUR, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);

        endCalendar.setTimeInMillis(endDate);
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

        binding.etDate.setOnClickListener(view1 -> pickDate());
        binding.btnSearch.setOnClickListener(view12 -> filterData());

        adapter = new AbsenAdapter(getContext());
        binding.rv.setAdapter(adapter);
        binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.btn.setOnClickListener(view13 -> {
            AdminDashboardActivity activity = (AdminDashboardActivity) getActivity();
            activity.launchIntent(absensiList, startDate, endDate);
        });

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

        List<String> ids = new ArrayList<>();
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
                        String karyawanId = snapshot.getString("userId");
                        ids.add(karyawanId);

                        DocumentSnapshot karyawanData = users.stream().filter(snapshot1 -> snapshot1
                                .getId()
                                .equals(karyawanId)
                        ).collect(Collectors.toList()).get(0);

                        absensiList.add(
                                new AbsensiResponse(karyawanData, snapshot)
                        );
                    });
                    users.forEach(snapshot -> {
                        if (!ids.contains(snapshot.getId())) {
                            absensiList.add(
                                    new AbsensiResponse(snapshot, null)
                            );
                        }
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
                .where(Filter.equalTo("admin", false))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    users.clear();
                    users.addAll(queryDocumentSnapshots.getDocuments());

                    if (!users.isEmpty()) {
                        filterData();
                    }
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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