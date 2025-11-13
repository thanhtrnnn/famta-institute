package com.famta.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GiaoVien extends NguoiDung {
    private final String maGiaoVien;
    private String gioiTinh;
    private String diaChiEmail;
    private String sdt;
    private final List<LopHoc> cacLopDangDay = new ArrayList<>();

    public GiaoVien(String maGiaoVien, String ho, String tenLot, String ten, String gioiTinh, String diaChiEmail, String sdt) {
        super(ho, tenLot, ten);
        this.maGiaoVien = Objects.requireNonNull(maGiaoVien, "maGiaoVien");
        this.gioiTinh = gioiTinh;
        this.diaChiEmail = diaChiEmail;
        this.sdt = sdt;
    }

    public String getMaGiaoVien() {
        return maGiaoVien;
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

    public List<LopHoc> getCacLopDangDay() {
        return Collections.unmodifiableList(cacLopDangDay);
    }

    public void nhapDiem(DangKyHoc dangKyHoc, Float tx, Float gk, Float ck) {
        Objects.requireNonNull(dangKyHoc, "dangKyHoc").capNhatDiem(tx, gk, ck);
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
