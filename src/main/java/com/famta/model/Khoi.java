package com.famta.model;

import java.util.Objects;

public class Khoi {
    private final String maKhoi;
    private String tenKhoi;
    private int soThuTu;

    public Khoi(String maKhoi, String tenKhoi, int soThuTu) {
        this.maKhoi = Objects.requireNonNull(maKhoi, "maKhoi");
        this.tenKhoi = tenKhoi;
        this.soThuTu = soThuTu;
    }

    public String getMaKhoi() {
        return maKhoi;
    }

    public String getTenKhoi() {
        return tenKhoi;
    }

    public void setTenKhoi(String tenKhoi) {
        this.tenKhoi = tenKhoi;
    }

    public int getSoThuTu() {
        return soThuTu;
    }

    public void setSoThuTu(int soThuTu) {
        this.soThuTu = soThuTu;
    }
}
