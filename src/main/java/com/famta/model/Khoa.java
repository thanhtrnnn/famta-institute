package com.famta.model;

import java.util.List;

public class Khoa {    private String maKhoa;
    private String tenKhoa;

    // Constructors
    public Khoa() {}
    public Khoa(String maKhoa, String tenKhoa) { this.maKhoa = maKhoa; this.tenKhoa = tenKhoa; }

    // Getters
    public String getMaKhoa() { return maKhoa; }
    public String getTenKhoa() { return tenKhoa; }

    // Setters
    public void setMaKhoa(String maKhoa) { this.maKhoa = maKhoa; }
    public void setTenKhoa(String tenKhoa) { this.tenKhoa = tenKhoa; }

    public List<MonHoc> layDanhSachMonHoc() {
        return null;
    }
}
