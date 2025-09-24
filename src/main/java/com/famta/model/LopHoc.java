package com.famta.model;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

/**
 * Lớp LopHoc (Class) - Đại diện cho một lớp học cụ thể
 */
public class LopHoc {
    // Thuộc tính (Dữ liệu)
    private String maLop;
    private String tenLop;
    private String maKhoaHoc;
    private GiaoVien giaoVienPhuTrach;
    private List<HocVien> danhSachHocVien;
    private List<LocalDateTime> lichHoc;
    
    // Constructor
    public LopHoc() {
        this.danhSachHocVien = new ArrayList<>();
        this.lichHoc = new ArrayList<>();
    }
    
    public LopHoc(String maLop, String tenLop, String maKhoaHoc, GiaoVien giaoVienPhuTrach) {
        this.maLop = maLop;
        this.tenLop = tenLop;
        this.maKhoaHoc = maKhoaHoc;
        this.giaoVienPhuTrach = giaoVienPhuTrach;
        this.danhSachHocVien = new ArrayList<>();
        this.lichHoc = new ArrayList<>();
    }
    
    // Phương thức (Hành vi)
    /**
     * Thêm một học viên vào lớp
     * @param hocVien Học viên cần thêm
     */
    public void themHocVien(HocVien hocVien) {
        if (hocVien != null && !danhSachHocVien.contains(hocVien)) {
            danhSachHocVien.add(hocVien);
            System.out.println("Đã thêm học viên " + hocVien.getHoTen() + " vào lớp " + tenLop);
        } else {
            System.out.println("Không thể thêm học viên vào lớp");
        }
    }
    
    /**
     * Xóa một học viên khỏi lớp
     * @param hocVien Học viên cần xóa
     */
    public void xoaHocVien(HocVien hocVien) {
        if (hocVien != null && danhSachHocVien.contains(hocVien)) {
            danhSachHocVien.remove(hocVien);
            System.out.println("Đã xóa học viên " + hocVien.getHoTen() + " khỏi lớp " + tenLop);
        } else {
            System.out.println("Không tìm thấy học viên trong lớp");
        }
    }
    
    /**
     * Cập nhật thông tin của lớp học (ví dụ: thay đổi giáo viên phụ trách)
     */
    public void capNhatThongTin() {
        // Thực hiện logic cập nhật thông tin lớp học
        System.out.println("Cập nhật thông tin lớp học: " + tenLop);
    }
    
    // Getter và Setter
    public String getMaLop() {
        return maLop;
    }
    
    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }
    
    public String getTenLop() {
        return tenLop;
    }
    
    public void setTenLop(String tenLop) {
        this.tenLop = tenLop;
    }
    
    public String getMaKhoaHoc() {
        return maKhoaHoc;
    }
    
    public void setMaKhoaHoc(String maKhoaHoc) {
        this.maKhoaHoc = maKhoaHoc;
    }
    
    public GiaoVien getGiaoVienPhuTrach() {
        return giaoVienPhuTrach;
    }
    
    public void setGiaoVienPhuTrach(GiaoVien giaoVienPhuTrach) {
        this.giaoVienPhuTrach = giaoVienPhuTrach;
    }
    
    public List<HocVien> getDanhSachHocVien() {
        return danhSachHocVien;
    }
    
    public void setDanhSachHocVien(List<HocVien> danhSachHocVien) {
        this.danhSachHocVien = danhSachHocVien;
    }
    
    public List<LocalDateTime> getLichHoc() {
        return lichHoc;
    }
    
    public void setLichHoc(List<LocalDateTime> lichHoc) {
        this.lichHoc = lichHoc;
    }
}