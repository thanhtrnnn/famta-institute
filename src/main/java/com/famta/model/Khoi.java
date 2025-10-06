package com.famta.model;

public class Khoi {    private String maKhoi;
    private String tenKhoi;
    private int soThuTu;

    // Constructors
    public Khoi() {}
    public Khoi(String maKhoi, String tenKhoi, int soThuTu) { this.maKhoi = maKhoi; this.tenKhoi = tenKhoi; this.soThuTu = soThuTu; }

    // Getters
    public String getMaKhoi() { return maKhoi; }
    public String getTenKhoi() { return tenKhoi; }
    public int getSoThuTu() { return soThuTu; }

    // Setters
    public void setMaKhoi(String maKhoi) { this.maKhoi = maKhoi; }
    public void setTenKhoi(String tenKhoi) { this.tenKhoi = tenKhoi; }
    public void setSoThuTu(int soThuTu) { this.soThuTu = soThuTu; }
}
