package com.famta.model;

import java.time.LocalDate;

/**
 * Lớp GiaoVien (Teacher) - Đại diện cho các giáo viên giảng dạy tại trung tâm
 */
public class GiaoVien {
    // Thuộc tính (Dữ liệu)
    private String maGiaoVien;
    private String hoTen;
    private LocalDate ngaySinh;
    private String diaChi;
    private String soDienThoai;
    private String email;
    private String chuyenMon;
    
    // Constructor
    public GiaoVien() {
    }
    
    public GiaoVien(String maGiaoVien, String hoTen, LocalDate ngaySinh, String diaChi,
                    String soDienThoai, String email, String chuyenMon) {
        this.maGiaoVien = maGiaoVien;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.chuyenMon = chuyenMon;
    }
    
    // Phương thức (Hành vi)
    /**
     * Cho phép giáo viên điểm danh học viên của lớp mình phụ trách
     * @param lopHoc Lớp học cần điểm danh
     */
    public void diemDanhHocVien(LopHoc lopHoc) {
        // Thực hiện logic điểm danh học viên
        System.out.println("Giáo viên " + hoTen + " điểm danh lớp: " + lopHoc.getTenLop());
    }
    
    /**
     * Cho phép giáo viên nhập điểm cho học viên
     * @param lopHoc Lớp học cần nhập điểm
     * @param diem Điểm số cần nhập
     */
    public void nhapDiem(LopHoc lopHoc, DiemSo diem) {
        // Thực hiện logic nhập điểm
        System.out.println("Giáo viên " + hoTen + " nhập điểm cho lớp: " + lopHoc.getTenLop());
    }
    
    /**
     * Cho phép giáo viên xem lịch giảng dạy của bản thân
     */
    public void xemLichDay() {
        // Thực hiện logic xem lịch dạy
        System.out.println("Xem lịch dạy của giáo viên: " + hoTen);
    }
    
    // Getter và Setter
    public String getMaGiaoVien() {
        return maGiaoVien;
    }
    
    public void setMaGiaoVien(String maGiaoVien) {
        this.maGiaoVien = maGiaoVien;
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
    
    public String getChuyenMon() {
        return chuyenMon;
    }
    
    public void setChuyenMon(String chuyenMon) {
        this.chuyenMon = chuyenMon;
    }
}