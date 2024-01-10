package com.example.absensi3gcell.ui.admin.absen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.absensi3gcell.R;
import com.example.absensi3gcell.databinding.FragmentAbsenBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AbsenFragment extends Fragment {
    private FragmentAbsenBinding binding;

    private Long startDate = new Date().getTime();
    private Long endDate = new Date().getTime();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAbsenBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String startDateString = sdf.format(new Date(startDate));
        String endDateString = sdf.format(new Date(endDate));

        String selectedDateRange = startDateString + " - " + endDateString;

        binding.etDate.setText(selectedDateRange);

        binding.etDate.setOnClickListener(view1 -> pickDate());
        binding.btnSearch.setOnClickListener(view12 -> filterData());

        filterData();
    }

    private void pickDate() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select a date range");

        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();
        datePicker.addOnPositiveButtonClickListener(selection -> {

            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTimeInMillis(selection.first);
            startCalendar.set(Calendar.HOUR, 0);
            startCalendar.set(Calendar.MINUTE, 0);
            startCalendar.set(Calendar.SECOND, 0);
            startCalendar.set(Calendar.MILLISECOND, 0);

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTimeInMillis(selection.second);
            endCalendar.set(Calendar.HOUR, 23);
            endCalendar.set(Calendar.MINUTE, 59);
            endCalendar.set(Calendar.SECOND, 59);
            endCalendar.set(Calendar.MILLISECOND, 59);

            startDate = startCalendar.getTimeInMillis();
            endDate = endCalendar.getTimeInMillis();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
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
                .addOnSuccessListener(queryDocumentSnapshots -> setLoading(false))
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
}