package com.famta.model;

import java.time.LocalDate;

public class DiemSo {
    private String maDiem;
    private HocSinh hocSinh;
    private MonHoc monHoc;
    private double diem;
    private LoaiDiem loaiDiem;
    private LocalDate ngayNhapDiem;    private String ghiChu;

    // Constructors
    public DiemSo() {}
    public DiemSo(String maDiem, HocSinh hocSinh, MonHoc monHoc, double diem, LoaiDiem loaiDiem, LocalDate ngayNhapDiem, String ghiChu) { this.maDiem = maDiem; this.hocSinh = hocSinh; this.monHoc = monHoc; this.diem = diem; this.loaiDiem = loaiDiem; this.ngayNhapDiem = ngayNhapDiem; this.ghiChu = ghiChu; }

    // Getters
    public String getMaDiem() { return maDiem; }
    public HocSinh getHocSinh() { return hocSinh; }
    public MonHoc getMonHoc() { return monHoc; }
    public double getDiem() { return diem; }
    public LoaiDiem getLoaiDiem() { return loaiDiem; }
    public LocalDate getNgayNhapDiem() { return ngayNhapDiem; }
    public String getGhiChu() { return ghiChu; }

    // Setters
    public void setMaDiem(String maDiem) { this.maDiem = maDiem; }
    public void setHocSinh(HocSinh hocSinh) { this.hocSinh = hocSinh; }
    public void setMonHoc(MonHoc monHoc) { this.monHoc = monHoc; }
    public void setDiem(double diem) { this.diem = diem; }
    public void setLoaiDiem(LoaiDiem loaiDiem) { this.loaiDiem = loaiDiem; }
    public void setNgayNhapDiem(LocalDate ngayNhapDiem) { this.ngayNhapDiem = ngayNhapDiem; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public void capNhatDiem(double diemMoi) {
    }

    public String xepLoai() {
        return null;
    }

    public boolean daDat() {
        return false;
    }
}