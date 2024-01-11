package com.example.absensi3gcell.model;

import com.google.firebase.firestore.DocumentSnapshot;

public class AbsensiResponse {
    private DocumentSnapshot karyawanData;
    private DocumentSnapshot absensiData;

    public AbsensiResponse(DocumentSnapshot karyawanData, DocumentSnapshot absensiData) {
        this.karyawanData = karyawanData;
        this.absensiData = absensiData;
    }

    public DocumentSnapshot getAbsensiData() {
        return absensiData;
    }

    public DocumentSnapshot getKaryawanData() {
        return karyawanData;
    }

}
