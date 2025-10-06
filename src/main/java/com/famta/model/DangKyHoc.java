package com.famta.model;

import java.time.LocalDate;

public class DangKyHoc {    private String maDangKy;
    private HocSinh hocSinh;
    private LopHoc lopHoc;
    private LocalDate ngayDangKy;

    // Constructors
    public DangKyHoc() {}
    public DangKyHoc(String maDangKy, HocSinh hocSinh, LopHoc lopHoc, LocalDate ngayDangKy) { this.maDangKy = maDangKy; this.hocSinh = hocSinh; this.lopHoc = lopHoc; this.ngayDangKy = ngayDangKy; }

    // Getters
    public String getMaDangKy() { return maDangKy; }
    public HocSinh getHocSinh() { return hocSinh; }
    public LopHoc getLopHoc() { return lopHoc; }
    public LocalDate getNgayDangKy() { return ngayDangKy; }

    // Setters
    public void setMaDangKy(String maDangKy) { this.maDangKy = maDangKy; }
    public void setHocSinh(HocSinh hocSinh) { this.hocSinh = hocSinh; }
    public void setLopHoc(LopHoc lopHoc) { this.lopHoc = lopHoc; }
    public void setNgayDangKy(LocalDate ngayDangKy) { this.ngayDangKy = ngayDangKy; }
}
