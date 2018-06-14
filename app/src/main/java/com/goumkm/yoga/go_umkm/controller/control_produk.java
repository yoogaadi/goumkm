package com.goumkm.yoga.go_umkm.controller;

public class control_produk {
    String nama_produk, deskripsi,id_umkm,image_path;
    double harga_produk;
    int status;

    public control_produk() {

    }

    public control_produk(String nama_produk, String deskripsi, String id_umkm, String image_path, double harga_produk, int status) {
        this.nama_produk = nama_produk;
        this.deskripsi = deskripsi;
        this.id_umkm = id_umkm;
        this.image_path = image_path;
        this.harga_produk = harga_produk;
        this.status = status;
    }

    public String getNama_produk() {
        return nama_produk;
    }

    public void setNama_produk(String nama_produk) {
        this.nama_produk = nama_produk;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getId_umkm() {
        return id_umkm;
    }

    public void setId_umkm(String id_umkm) {
        this.id_umkm = id_umkm;
    }

    public double getHarga_produk() {
        return harga_produk;
    }

    public void setHarga_produk(double harga_produk) {
        this.harga_produk = harga_produk;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }
}
