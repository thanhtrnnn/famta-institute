package com.famta.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Lớp HocVien (Student) - Đại diện cho mỗi học viên tham gia trung tâm
 */
public class HocVien {
    // Thuộc tính (Dữ liệu)
    private String maHocVien;
    private String hoTen;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String diaChi;
    private String soDienThoai;
    private String email;
    private String thongTinNguoiGiamHo; // Thông tin người giám hộ
    
    // Constructor
    public HocVien() {
    }
    
    public HocVien(String maHocVien, String hoTen, LocalDate ngaySinh, String gioiTinh, 
                   String diaChi, String soDienThoai, String email, String thongTinNguoiGiamHo) {
        this.maHocVien = maHocVien;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.thongTinNguoiGiamHo = thongTinNguoiGiamHo;
    }
    
    // Phương thức (Hành vi)
    /**
     * Cho phép học viên đăng ký một khóa học mới
     * @param khoaHoc Khóa học muốn đăng ký
     */
    public void dangKyKhoaHoc(KhoaHoc khoaHoc) {
        // Thực hiện logic đăng ký khóa học
        System.out.println("Học viên " + hoTen + " đăng ký khóa học: " + khoaHoc.getTenKhoaHoc());
    }
    
    /**
     * Cho phép học viên xem thời khóa biểu cá nhân
     */
    public void xemLichHoc() {
        // Thực hiện logic xem lịch học
        System.out.println("Xem lịch học của học viên: " + hoTen);
    }
    
    /**
     * Cho phép học viên tra cứu điểm số các môn học đã tham gia
     */
    public void xemDiem() {
        // Thực hiện logic xem điểm
        System.out.println("Xem điểm của học viên: " + hoTen);
    }
    
    // Getter và Setter
    public String getMaHocVien() {
        return maHocVien;
    }
    
    public void setMaHocVien(String maHocVien) {
        this.maHocVien = maHocVien;
    }
    
    public String getHoTen() {
        return hoTen;
    }
    
    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }
    
    public LocalDate getNgaySinh() {
        return ngaySinh;
    }
    
    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }
    
    public String getGioiTinh() {
        return gioiTinh;
    }
    
    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }
    
    public String getDiaChi() {
        return diaChi;
    }
    
    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }
    
    public String getSoDienThoai() {
        return soDienThoai;
    }
    
    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getThongTinNguoiGiamHo() {
        return thongTinNguoiGiamHo;
    }
    
    public void setThongTinNguoiGiamHo(String thongTinNguoiGiamHo) {
        this.thongTinNguoiGiamHo = thongTinNguoiGiamHo;
    }
}