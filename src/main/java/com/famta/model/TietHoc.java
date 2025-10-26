package com.famta.model;

import java.time.LocalTime;
import java.util.Objects;

public class TietHoc {
    private final String maTietHoc;
    private String tenTietHoc;
    private LocalTime thoiGianBatDau;
    private LocalTime thoiGianKetThuc;
    private NamHoc namHoc;

    public TietHoc(String maTietHoc, String tenTietHoc, LocalTime thoiGianBatDau, LocalTime thoiGianKetThuc, NamHoc namHoc) {
        this.maTietHoc = Objects.requireNonNull(maTietHoc, "maTietHoc");
        this.tenTietHoc = tenTietHoc;
        this.thoiGianBatDau = thoiGianBatDau;
        this.thoiGianKetThuc = thoiGianKetThuc;
        setNamHoc(namHoc);
    }

    public String getMaTietHoc() {
        return maTietHoc;
    }

    public String getTenTietHoc() {
        return tenTietHoc;
    }

    public void setTenTietHoc(String tenTietHoc) {
        this.tenTietHoc = tenTietHoc;
    }

    public LocalTime getThoiGianBatDau() {
        return thoiGianBatDau;
    }

    public void setThoiGianBatDau(LocalTime thoiGianBatDau) {
        this.thoiGianBatDau = thoiGianBatDau;
    }

    public LocalTime getThoiGianKetThuc() {
        return thoiGianKetThuc;
    }

    public void setThoiGianKetThuc(LocalTime thoiGianKetThuc) {
        this.thoiGianKetThuc = thoiGianKetThuc;
    }

    public NamHoc getNamHoc() {
        return namHoc;
    }

    public void setNamHoc(NamHoc namHoc) {
        if (this.namHoc != null) {
            this.namHoc.xoaTietHocInternal(this);
        }
        this.namHoc = namHoc;
        if (namHoc != null) {
            namHoc.themTietHocInternal(this);
        }
    }
}
