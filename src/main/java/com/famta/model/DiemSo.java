package com.famta.model;

import java.time.LocalDate;

public class DiemSo {
    private String maDiem;
    private HocSinh hocSinh;
    private MonHoc monHoc;
    private double diem;
    private LoaiDiem loaiDiem;
    private LocalDate ngayNhapDiem;
    private String ghiChu;

    public void capNhatDiem(double diemMoi) {
    }

    public String xepLoai() {
        return null;
    }

    public boolean daDat() {
        return false;
    }
}