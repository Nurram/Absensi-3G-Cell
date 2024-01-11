package com.example.absensi3gcell.model;

import com.google.firebase.firestore.DocumentSnapshot;

public class AbsensiResponse {
    private DocumentSnapshot karyawanData;
    private DocumentSnapshot absensiData;
    private boolean isAbsen = false;

    public DocumentSnapshot getAbsensiData() {
        return absensiData;
    }

    public AbsensiResponse(DocumentSnapshot karyawanData, DocumentSnapshot absensiData, boolean isAbsen) {
        this.karyawanData = karyawanData;
        this.absensiData = absensiData;
        this.isAbsen = isAbsen;
    }

    public DocumentSnapshot getKaryawanData() {
        return karyawanData;
    }

    public boolean isAbsen() {
        return isAbsen;
    }
}
