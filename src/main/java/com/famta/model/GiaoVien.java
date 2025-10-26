package com.famta.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GiaoVien {
    private final String maGiaoVien;
    private String ho;
    private String tenLot;
    private String ten;
    private String gioiTinh;
    private String diaChiEmail;
    private String sdt;
    private TaiKhoan taiKhoan;
    private final List<LopHoc> cacLopDangDay = new ArrayList<>();

    public GiaoVien(String maGiaoVien, String ho, String tenLot, String ten, String gioiTinh, String diaChiEmail, String sdt) {
        this.maGiaoVien = Objects.requireNonNull(maGiaoVien, "maGiaoVien");
        this.ho = ho;
        this.tenLot = tenLot;
        this.ten = ten;
        this.gioiTinh = gioiTinh;
        this.diaChiEmail = diaChiEmail;
        this.sdt = sdt;
    }

    public String getMaGiaoVien() {
        return maGiaoVien;
    }

    public String getHo() {
        return ho;
    }

    public void setHo(String ho) {
        this.ho = ho;
    }

    public String getTenLot() {
        return tenLot;
    }

    public void setTenLot(String tenLot) {
        this.tenLot = tenLot;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getDiaChiEmail() {
        return diaChiEmail;
    }

    public void setDiaChiEmail(String diaChiEmail) {
        this.diaChiEmail = diaChiEmail;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public List<LopHoc> getCacLopDangDay() {
        return Collections.unmodifiableList(cacLopDangDay);
    }

    public String getHoTenDayDu() {
        return ho + " " + (tenLot != null && !tenLot.isBlank() ? tenLot + " " : "") + ten;
    }

    public void nhapDiem(DangKyHoc dangKyHoc, float diemSo) {
        Objects.requireNonNull(dangKyHoc, "dangKyHoc").capNhatDiem(diemSo);
    }

    public String xemLichDay() {
        return cacLopDangDay.stream()
                .map(lop -> lop.getTenLopHoc() + " - " + lop.getTietHocBatDau().getTenTietHoc() + " -> " + lop.getTietHocKetThuc().getTenTietHoc())
                .collect(Collectors.joining("\n"));
    }

    public void diemDanh(LopHoc lopHoc, HocSinh hocSinh, String trangThai) {
        Objects.requireNonNull(lopHoc, "lopHoc").ghiNhanDiemDanh(Objects.requireNonNull(hocSinh, "hocSinh"), Objects.requireNonNull(trangThai, "trangThai"));
    }

    void addLopDangDayInternal(LopHoc lopHoc) {
        if (!cacLopDangDay.contains(lopHoc)) {
            cacLopDangDay.add(lopHoc);
        }
    }

    void removeLopDangDayInternal(LopHoc lopHoc) {
        cacLopDangDay.remove(lopHoc);
    }
}
