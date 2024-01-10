package com.example.absensi3gcell.model;

public class KarywanAddRequest {
    String id;
    String nip;
    String name;
    String email;
    String password;

    boolean isAdmin = false;

    public KarywanAddRequest(String id, String nip, String name, String email, String password) {
        this.id = id;
        this.nip = nip;
        this.name = name;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
