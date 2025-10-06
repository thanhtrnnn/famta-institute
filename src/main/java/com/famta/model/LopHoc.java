package com.famta.model;

import java.util.List;

public class LopHoc {
    private String maLopHoc;
    private String tenLopHoc;
    private MonHoc monHoc;
    private GiaoVien giaoVien;
    private HocKy hocKy;
    private PhongHoc phongHoc;
    private TietHoc tietHocBatDau;    private TietHoc tietHocKetThuc;

    // Constructors
    public LopHoc() {}
    public LopHoc(String maLopHoc, String tenLopHoc, MonHoc monHoc, GiaoVien giaoVien, HocKy hocKy, PhongHoc phongHoc) { this.maLopHoc = maLopHoc; this.tenLopHoc = tenLopHoc; this.monHoc = monHoc; this.giaoVien = giaoVien; this.hocKy = hocKy; this.phongHoc = phongHoc; }

    // Getters
    public String getMaLopHoc() { return maLopHoc; }
    public String getTenLopHoc() { return tenLopHoc; }
    public MonHoc getMonHoc() { return monHoc; }
    public GiaoVien getGiaoVien() { return giaoVien; }
    public HocKy getHocKy() { return hocKy; }
    public PhongHoc getPhongHoc() { return phongHoc; }
    public TietHoc getTietHocBatDau() { return tietHocBatDau; }
    public TietHoc getTietHocKetThuc() { return tietHocKetThuc; }

    // Setters
    public void setMaLopHoc(String maLopHoc) { this.maLopHoc = maLopHoc; }
    public void setTenLopHoc(String tenLopHoc) { this.tenLopHoc = tenLopHoc; }
    public void setMonHoc(MonHoc monHoc) { this.monHoc = monHoc; }
    public void setGiaoVien(GiaoVien giaoVien) { this.giaoVien = giaoVien; }
    public void setHocKy(HocKy hocKy) { this.hocKy = hocKy; }
    public void setPhongHoc(PhongHoc phongHoc) { this.phongHoc = phongHoc; }
    public void setTietHocBatDau(TietHoc tietHocBatDau) { this.tietHocBatDau = tietHocBatDau; }
    public void setTietHocKetThuc(TietHoc tietHocKetThuc) { this.tietHocKetThuc = tietHocKetThuc; }

    public List<HocSinh> layDanhSachHocSinh() {
        return null;
    }

    public void themDangKy(DangKyHoc dangKyHoc) {
    }
}