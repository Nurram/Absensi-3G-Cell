package com.example.absensi3gcell.model;

public class KarywanAddRequest {
    String id;
    String nip;
    String name;
    String email;

    boolean isAdmin = false;

    public KarywanAddRequest(String id, String nip, String name, String email) {
        this.id = id;
        this.nip = nip;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getNip() {
        return nip;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }


    public boolean isAdmin() {
        return isAdmin;
    }
}
