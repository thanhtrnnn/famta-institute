package com.famta.model;

import java.time.LocalDate;
import java.util.List;

public class NamHoc {
    private String maNamHoc;
    private String tenNamHoc;
    private LocalDate ngayBatDau;    private LocalDate ngayKetThuc;

    // Constructors
    public NamHoc() {}
    public NamHoc(String maNamHoc, String tenNamHoc, LocalDate ngayBatDau, LocalDate ngayKetThuc) { this.maNamHoc = maNamHoc; this.tenNamHoc = tenNamHoc; this.ngayBatDau = ngayBatDau; this.ngayKetThuc = ngayKetThuc; }

    // Getters
    public String getMaNamHoc() { return maNamHoc; }
    public String getTenNamHoc() { return tenNamHoc; }
    public LocalDate getNgayBatDau() { return ngayBatDau; }
    public LocalDate getNgayKetThuc() { return ngayKetThuc; }

    // Setters
    public void setMaNamHoc(String maNamHoc) { this.maNamHoc = maNamHoc; }
    public void setTenNamHoc(String tenNamHoc) { this.tenNamHoc = tenNamHoc; }
    public void setNgayBatDau(LocalDate ngayBatDau) { this.ngayBatDau = ngayBatDau; }
    public void setNgayKetThuc(LocalDate ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }

    public List<HocKy> layDanhSachHocKy() {
        return null;
    }
}
