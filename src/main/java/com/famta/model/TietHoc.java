package com.famta.model;

import java.time.LocalTime;

public class TietHoc {    private String maTietHoc;
    private String tenTietHoc;
    private LocalTime thoiGianBatDau;
    private LocalTime thoiGianKetThuc;

    // Constructors
    public TietHoc() {}
    public TietHoc(String maTietHoc, String tenTietHoc, LocalTime thoiGianBatDau, LocalTime thoiGianKetThuc) { this.maTietHoc = maTietHoc; this.tenTietHoc = tenTietHoc; this.thoiGianBatDau = thoiGianBatDau; this.thoiGianKetThuc = thoiGianKetThuc; }

    // Getters
    public String getMaTietHoc() { return maTietHoc; }
    public String getTenTietHoc() { return tenTietHoc; }
    public LocalTime getThoiGianBatDau() { return thoiGianBatDau; }
    public LocalTime getThoiGianKetThuc() { return thoiGianKetThuc; }

    // Setters
    public void setMaTietHoc(String maTietHoc) { this.maTietHoc = maTietHoc; }
    public void setTenTietHoc(String tenTietHoc) { this.tenTietHoc = tenTietHoc; }
    public void setThoiGianBatDau(LocalTime thoiGianBatDau) { this.thoiGianBatDau = thoiGianBatDau; }
    public void setThoiGianKetThuc(LocalTime thoiGianKetThuc) { this.thoiGianKetThuc = thoiGianKetThuc; }
}
