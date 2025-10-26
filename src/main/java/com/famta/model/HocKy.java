package com.famta.model;

import java.time.LocalDate;
import java.util.Objects;

public class HocKy {
    private final String maHocKy;
    private int thuTuKy;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private NamHoc namHoc;

    public HocKy(String maHocKy, int thuTuKy, LocalDate ngayBatDau, LocalDate ngayKetThuc, NamHoc namHoc) {
        this.maHocKy = Objects.requireNonNull(maHocKy, "maHocKy");
        this.thuTuKy = thuTuKy;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        setNamHoc(namHoc);
    }

    public String getMaHocKy() {
        return maHocKy;
    }

    public int getThuTuKy() {
        return thuTuKy;
    }

    public void setThuTuKy(int thuTuKy) {
        this.thuTuKy = thuTuKy;
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public NamHoc getNamHoc() {
        return namHoc;
    }

    public void setNamHoc(NamHoc namHoc) {
        if (this.namHoc != null) {
            this.namHoc.xoaHocKyInternal(this);
        }
        this.namHoc = namHoc;
        if (namHoc != null) {
            namHoc.themHocKyInternal(this);
        }
    }
}
