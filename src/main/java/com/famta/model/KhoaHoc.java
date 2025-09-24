package com.famta.model;

import java.util.List;
import java.util.ArrayList;

/**
 * Lớp KhoaHoc (Course) - Đại diện cho một khóa học duy nhất của trung tâm
 */
public class KhoaHoc {
    // Thuộc tính (Dữ liệu)
    private String maKhoaHoc;
    private String tenKhoaHoc;
    private String moTa;
    private double hocPhi;
    private int thoiLuong; // Thời lượng tính bằng số giờ
    private List<LopHoc> danhSachLopHoc;
    
    // Constructor
    public KhoaHoc() {
        this.danhSachLopHoc = new ArrayList<>();
    }
    
    public KhoaHoc(String maKhoaHoc, String tenKhoaHoc, String moTa, double hocPhi, int thoiLuong) {
        this.maKhoaHoc = maKhoaHoc;
        this.tenKhoaHoc = tenKhoaHoc;
        this.moTa = moTa;
        this.hocPhi = hocPhi;
        this.thoiLuong = thoiLuong;
        this.danhSachLopHoc = new ArrayList<>();
    }
    
    // Phương thức (Hành vi)
    /**
     * Mở một khóa học mới
     */
    public void moKhoaHoc() {
        // Thực hiện logic mở khóa học
        System.out.println("Mở khóa học: " + tenKhoaHoc);
        // Có thể thêm logic khởi tạo các lớp học, thông báo học viên, etc.
    }
    
    /**
     * Kết thúc một khóa học đã hoàn thành
     */
    public void dongKhoaHoc() {
        // Thực hiện logic đóng khóa học
        System.out.println("Đóng khóa học: " + tenKhoaHoc);
        // Có thể thêm logic lưu trữ kết quả, chứng chỉ, báo cáo, etc.
    }
    
    /**
     * Thêm một lớp học vào khóa học
     * @param lopHoc Lớp học cần thêm
     */
    public void themLopHoc(LopHoc lopHoc) {
        if (lopHoc != null && !danhSachLopHoc.contains(lopHoc)) {
            danhSachLopHoc.add(lopHoc);
            System.out.println("Đã thêm lớp " + lopHoc.getTenLop() + " vào khóa học " + tenKhoaHoc);
        } else {
            System.out.println("Không thể thêm lớp học vào khóa học");
        }
    }
    
    // Getter và Setter
    public String getMaKhoaHoc() {
        return maKhoaHoc;
    }
    
    public void setMaKhoaHoc(String maKhoaHoc) {
        this.maKhoaHoc = maKhoaHoc;
    }
    
    public String getTenKhoaHoc() {
        return tenKhoaHoc;
    }
    
    public void setTenKhoaHoc(String tenKhoaHoc) {
        this.tenKhoaHoc = tenKhoaHoc;
    }
    
    public String getMoTa() {
        return moTa;
    }
    
    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
    
    public double getHocPhi() {
        return hocPhi;
    }
    
    public void setHocPhi(double hocPhi) {
        this.hocPhi = hocPhi;
    }
    
    public int getThoiLuong() {
        return thoiLuong;
    }
    
    public void setThoiLuong(int thoiLuong) {
        this.thoiLuong = thoiLuong;
    }
    
    public List<LopHoc> getDanhSachLopHoc() {
        return danhSachLopHoc;
    }
    
    public void setDanhSachLopHoc(List<LopHoc> danhSachLopHoc) {
        this.danhSachLopHoc = danhSachLopHoc;
    }
}