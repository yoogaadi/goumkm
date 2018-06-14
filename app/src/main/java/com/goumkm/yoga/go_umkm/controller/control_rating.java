package com.goumkm.yoga.go_umkm.controller;

import java.sql.Timestamp;

public class control_rating {

    int rating;
    String id_user,id_produk,comment;
    Long timestamp;

    public control_rating() {
    }

    public control_rating(int rating, String id_user, String id_produk, String comment, Long timestamp) {
        this.rating = rating;
        this.id_user = id_user;
        this.id_produk = id_produk;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getId_produk() {
        return id_produk;
    }

    public void setId_produk(String id_produk) {
        this.id_produk = id_produk;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
