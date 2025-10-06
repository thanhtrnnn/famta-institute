package com.famta.model;

import java.util.List;

public class GiaoVien {
    private String maGiaoVien;
    private String ho;
    private String tenLot;
    private String ten;
    private String gioiTinh;
    private String diaChiEmail;
    private String sdt;    private List<LopHoc> cacLopDangDay;

    // Constructors
    public GiaoVien() {}
    public GiaoVien(String maGiaoVien, String ho, String tenLot, String ten, String gioiTinh, String diaChiEmail, String sdt) { this.maGiaoVien = maGiaoVien; this.ho = ho; this.tenLot = tenLot; this.ten = ten; this.gioiTinh = gioiTinh; this.diaChiEmail = diaChiEmail; this.sdt = sdt; }

    // Getters
    public String getMaGiaoVien() { return maGiaoVien; }
    public String getHo() { return ho; }
    public String getTenLot() { return tenLot; }
    public String getTen() { return ten; }
    public String getGioiTinh() { return gioiTinh; }
    public String getDiaChiEmail() { return diaChiEmail; }
    public String getSdt() { return sdt; }
    public List<LopHoc> getCacLopDangDay() { return cacLopDangDay; }

    // Setters
    public void setMaGiaoVien(String maGiaoVien) { this.maGiaoVien = maGiaoVien; }
    public void setHo(String ho) { this.ho = ho; }
    public void setTenLot(String tenLot) { this.tenLot = tenLot; }
    public void setTen(String ten) { this.ten = ten; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    public void setDiaChiEmail(String diaChiEmail) { this.diaChiEmail = diaChiEmail; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public void setCacLopDangDay(List<LopHoc> cacLopDangDay) { this.cacLopDangDay = cacLopDangDay; }

    // Utility methods
    public String getHoTenDayDu() { return ho + " " + (tenLot != null ? tenLot + " " : "") + ten; }

    public void nhapDiem(DangKyHoc dangKyHoc, DiemSo diem) {
    }

    public void xemLichDay() {
    }
}
