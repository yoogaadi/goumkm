package com.goumkm.yoga.go_umkm.controller;

public class control_umkm {

    String nama_umkm, alamat,telepon,deskripsi;
    int jam_buka,jam_tutup,hari_buka,hari_tutup,klaster;

    public control_umkm() {
    }

    public control_umkm(String nama_umkm, String alamat, String telepon, String deskripsi, int jam_buka, int jam_tutup, int hari_buka, int hari_tutup, int klaster) {
        this.nama_umkm = nama_umkm;
        this.alamat = alamat;
        this.telepon = telepon;
        this.deskripsi = deskripsi;
        this.jam_buka = jam_buka;
        this.jam_tutup = jam_tutup;
        this.hari_buka = hari_buka;
        this.hari_tutup = hari_tutup;
        this.klaster = klaster;
    }

    public String getNama_umkm() {
        return nama_umkm;
    }

    public void setNama_umkm(String nama_umkm) {
        this.nama_umkm = nama_umkm;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public int getJam_buka() {
        return jam_buka;
    }

    public void setJam_buka(int jam_buka) {
        this.jam_buka = jam_buka;
    }

    public int getJam_tutup() {
        return jam_tutup;
    }

    public void setJam_tutup(int jam_tutup) {
        this.jam_tutup = jam_tutup;
    }

    public int getHari_buka() {
        return hari_buka;
    }

    public void setHari_buka(int hari_buka) {
        this.hari_buka = hari_buka;
    }

    public int getHari_tutup() {
        return hari_tutup;
    }

    public void setHari_tutup(int hari_tutup) {
        this.hari_tutup = hari_tutup;
    }

    public int getKlaster() {
        return klaster;
    }

    public void setKlaster(int klaster) {
        this.klaster = klaster;
    }
}
