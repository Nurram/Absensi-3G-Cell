package com.example.absensi3gcell.model;

import android.net.Uri;

public class AbsensiRequest {
    String userId;
    String name;
    String place;
    String location;
    Long absentTime;
    Uri imageUrl;
    String imagePath;

    public AbsensiRequest(String userId, String name, String place, String location, Long date, Uri imageUrl, String imagePath) {
        this.userId = userId;
        this.name = name;
        this.place = place;
        this.location = location;
        this.absentTime = date;
        this.imageUrl = imageUrl;
        this.imagePath = imagePath;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getPlace() {
        return place;
    }

    public String getLocation() {
        return location;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Uri getImageUrl() {
        return imageUrl;
    }

    public Long getAbsentTime() {
        return absentTime;
    }
}
