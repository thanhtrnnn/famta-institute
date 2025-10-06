package com.famta.model;

import java.time.LocalDate;

public class HocKy {    private String maHocKy;
    private int thuTuKy;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private NamHoc namHoc;

    // Constructors
    public HocKy() {}
    public HocKy(String maHocKy, int thuTuKy, LocalDate ngayBatDau, LocalDate ngayKetThuc, NamHoc namHoc) { this.maHocKy = maHocKy; this.thuTuKy = thuTuKy; this.ngayBatDau = ngayBatDau; this.ngayKetThuc = ngayKetThuc; this.namHoc = namHoc; }

    // Getters
    public String getMaHocKy() { return maHocKy; }
    public int getThuTuKy() { return thuTuKy; }
    public LocalDate getNgayBatDau() { return ngayBatDau; }
    public LocalDate getNgayKetThuc() { return ngayKetThuc; }
    public NamHoc getNamHoc() { return namHoc; }

    // Setters
    public void setMaHocKy(String maHocKy) { this.maHocKy = maHocKy; }
    public void setThuTuKy(int thuTuKy) { this.thuTuKy = thuTuKy; }
    public void setNgayBatDau(LocalDate ngayBatDau) { this.ngayBatDau = ngayBatDau; }
    public void setNgayKetThuc(LocalDate ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }
    public void setNamHoc(NamHoc namHoc) { this.namHoc = namHoc; }
}
