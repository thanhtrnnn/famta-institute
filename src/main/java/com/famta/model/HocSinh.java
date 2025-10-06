package com.famta.model;

import java.time.LocalDate;
import java.util.List;

public class HocSinh {
    private String maHocSinh;
    private String ho;
    private String tenLot;
    private String ten;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private LocalDate ngayNhapHoc;    private List<MoiQuanHeGiamHo> danhSachNguoiGiamHo;
    private List<DangKyHoc> quaTrinhHocTap;

    // Constructors
    public HocSinh() {}
    public HocSinh(String maHocSinh, String ho, String tenLot, String ten, LocalDate ngaySinh, String gioiTinh, LocalDate ngayNhapHoc) { this.maHocSinh = maHocSinh; this.ho = ho; this.tenLot = tenLot; this.ten = ten; this.ngaySinh = ngaySinh; this.gioiTinh = gioiTinh; this.ngayNhapHoc = ngayNhapHoc; }

    // Getters
    public String getMaHocSinh() { return maHocSinh; }
    public String getHo() { return ho; }
    public String getTenLot() { return tenLot; }
    public String getTen() { return ten; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public String getGioiTinh() { return gioiTinh; }
    public LocalDate getNgayNhapHoc() { return ngayNhapHoc; }
    public List<MoiQuanHeGiamHo> getDanhSachNguoiGiamHo() { return danhSachNguoiGiamHo; }
    public List<DangKyHoc> getQuaTrinhHocTap() { return quaTrinhHocTap; }

    // Setters
    public void setMaHocSinh(String maHocSinh) { this.maHocSinh = maHocSinh; }
    public void setHo(String ho) { this.ho = ho; }
    public void setTenLot(String tenLot) { this.tenLot = tenLot; }
    public void setTen(String ten) { this.ten = ten; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    public void setNgayNhapHoc(LocalDate ngayNhapHoc) { this.ngayNhapHoc = ngayNhapHoc; }
    public void setDanhSachNguoiGiamHo(List<MoiQuanHeGiamHo> danhSachNguoiGiamHo) { this.danhSachNguoiGiamHo = danhSachNguoiGiamHo; }
    public void setQuaTrinhHocTap(List<DangKyHoc> quaTrinhHocTap) { this.quaTrinhHocTap = quaTrinhHocTap; }

    public String getHoTenDayDu() { return ho + " " + (tenLot != null ? tenLot + " " : "") + ten; }

    public void dangKyLopHoc(LopHoc lopHoc) {
        
    }

    public List<DiemSo> layBangDiem() {
        return null;
    }
}
