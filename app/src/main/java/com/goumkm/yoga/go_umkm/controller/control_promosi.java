package com.goumkm.yoga.go_umkm.controller;

public class control_promosi {
    String nama_promosi, gambar_promosi,deskripsi;
    Integer tanggal_mulai, tanggal_selesai, status;

    public control_promosi() {
    }

    public control_promosi(String nama_promosi, String gambar_promosi, String deskripsi, Integer tanggal_mulai, Integer tanggal_selesai, Integer status) {
        this.nama_promosi = nama_promosi;
        this.gambar_promosi = gambar_promosi;
        this.deskripsi = deskripsi;
        this.tanggal_mulai = tanggal_mulai;
        this.tanggal_selesai = tanggal_selesai;
        this.status = status;
    }

    public String getNama_promosi() {
        return nama_promosi;
    }

    public void setNama_promosi(String nama_promosi) {
        this.nama_promosi = nama_promosi;
    }

    public String getGambar_promosi() {
        return gambar_promosi;
    }

    public void setGambar_promosi(String gambar_promosi) {
        this.gambar_promosi = gambar_promosi;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public Integer getTanggal_mulai() {
        return tanggal_mulai;
    }

    public void setTanggal_mulai(Integer tanggal_mulai) {
        this.tanggal_mulai = tanggal_mulai;
    }

    public Integer getTanggal_selesai() {
        return tanggal_selesai;
    }

    public void setTanggal_selesai(Integer tanggal_selesai) {
        this.tanggal_selesai = tanggal_selesai;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
