package com.famta.model;

/**
 * Enum định nghĩa các loại điểm
 */
enum LoaiDiem {
    DIEM_THUONG_XUYEN,     // Điểm thường xuyên
    DIEM_GIUA_KY,          // Điểm kiểm tra giữa kỳ
    DIEM_CUOI_KY,          // Điểm thi cuối kỳ
    DIEM_BAI_TAP,          // Điểm bài tập
    DIEM_THUC_HANH         // Điểm thực hành
}

/**
 * Lớp DiemSo (Score) - Đại diện cho điểm số của một học viên trong một môn học
 */
public class DiemSo {
    // Thuộc tính (Dữ liệu)
    private String maHocVien;
    private String maMonHoc;
    private double diem;
    private LoaiDiem loaiDiem;
    private String ghiChu; // Ghi chú thêm về điểm
    
    // Constructor
    public DiemSo() {
    }
    
    public DiemSo(String maHocVien, String maMonHoc, double diem, LoaiDiem loaiDiem) {
        this.maHocVien = maHocVien;
        this.maMonHoc = maMonHoc;
        this.diem = diem;
        this.loaiDiem = loaiDiem;
    }
    
    public DiemSo(String maHocVien, String maMonHoc, double diem, LoaiDiem loaiDiem, String ghiChu) {
        this.maHocVien = maHocVien;
        this.maMonHoc = maMonHoc;
        this.diem = diem;
        this.loaiDiem = loaiDiem;
        this.ghiChu = ghiChu;
    }
    
    // Phương thức (Hành vi)
    /**
     * Thêm điểm mới cho học viên
     * @param diem Điểm số cần thêm
     */
    public void themDiem(double diem) {
        if (diem >= 0 && diem <= 10) {
            this.diem = diem;
            System.out.println("Đã thêm điểm " + diem + " cho học viên " + maHocVien + 
                             " môn " + maMonHoc + " loại điểm " + loaiDiem);
        } else {
            System.out.println("Điểm phải nằm trong khoảng từ 0 đến 10");
        }
    }
    
    /**
     * Cập nhật điểm đã có
     * @param diemMoi Điểm số mới
     */
    public void capNhatDiem(double diemMoi) {
        if (diemMoi >= 0 && diemMoi <= 10) {
            double diemCu = this.diem;
            this.diem = diemMoi;
            System.out.println("Đã cập nhật điểm từ " + diemCu + " thành " + diemMoi + 
                             " cho học viên " + maHocVien + " môn " + maMonHoc);
        } else {
            System.out.println("Điểm phải nằm trong khoảng từ 0 đến 10");
        }
    }
    
    /**
     * Kiểm tra điểm có đạt yêu cầu không (>= 5.0)
     * @return true nếu đạt, false nếu không đạt
     */
    public boolean daDat() {
        return this.diem >= 5.0;
    }
    
    /**
     * Xếp loại điểm
     * @return Loại điểm dưới dạng String
     */
    public String xepLoai() {
        if (diem >= 9.0) return "Xuất sắc";
        else if (diem >= 8.0) return "Giỏi";
        else if (diem >= 7.0) return "Khá";
        else if (diem >= 5.0) return "Trung bình";
        else return "Yếu";
    }
    
    // Getter và Setter
    public String getMaHocVien() {
        return maHocVien;
    }
    
    public void setMaHocVien(String maHocVien) {
        this.maHocVien = maHocVien;
    }
    
    public String getMaMonHoc() {
        return maMonHoc;
    }
    
    public void setMaMonHoc(String maMonHoc) {
        this.maMonHoc = maMonHoc;
    }
    
    public double getDiem() {
        return diem;
    }
    
    public void setDiem(double diem) {
        this.diem = diem;
    }
    
    public LoaiDiem getLoaiDiem() {
        return loaiDiem;
    }
    
    public void setLoaiDiem(LoaiDiem loaiDiem) {
        this.loaiDiem = loaiDiem;
    }
    
    public String getGhiChu() {
        return ghiChu;
    }
    
    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
    
    @Override
    public String toString() {
        return "DiemSo{" +
                "maHocVien='" + maHocVien + '\'' +
                ", maMonHoc='" + maMonHoc + '\'' +
                ", diem=" + diem +
                ", loaiDiem=" + loaiDiem +
                ", xepLoai='" + xepLoai() + '\'' +
                '}';
    }
}