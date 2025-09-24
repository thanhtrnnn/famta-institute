package com.famta.model;

/**
 * Enum định nghĩa các quyền truy cập của người dùng
 */
enum QuyenTruyCap {
    ADMIN, GIAO_VU, GIAO_VIEN, KE_TOAN, PHU_HUYNH, HOC_VIEN
}

/**
 * Lớp TaiKhoan (Account) - Đại diện cho tài khoản đăng nhập của người dùng
 */
public class TaiKhoan {
    // Thuộc tính (Dữ liệu)
    private String tenDangNhap;
    private String matKhau;
    private QuyenTruyCap quyenTruyCap;
    private boolean daLuuTruyCap; // Trạng thái đăng nhập
    
    // Constructor
    public TaiKhoan() {
        this.daLuuTruyCap = false;
    }
    
    public TaiKhoan(String tenDangNhap, String matKhau, QuyenTruyCap quyenTruyCap) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.quyenTruyCap = quyenTruyCap;
        this.daLuuTruyCap = false;
    }
    
    // Phương thức (Hành vi)
    /**
     * Xác thực thông tin đăng nhập của người dùng
     * @param ten Tên đăng nhập
     * @param matKhau Mật khẩu
     * @return true nếu đăng nhập thành công, false nếu thất bại
     */
    public boolean dangNhap(String ten, String matKhau) {
        // Thực hiện logic xác thực đăng nhập
        if (this.tenDangNhap != null && this.tenDangNhap.equals(ten) && 
            this.matKhau != null && this.matKhau.equals(matKhau)) {
            this.daLuuTruyCap = true;
            System.out.println("Đăng nhập thành công cho tài khoản: " + ten);
            return true;
        } else {
            System.out.println("Thông tin đăng nhập không chính xác");
            return false;
        }
    }
    
    /**
     * Đăng xuất khỏi hệ thống
     */
    public void dangXuat() {
        // Thực hiện logic đăng xuất
        if (this.daLuuTruyCap) {
            this.daLuuTruyCap = false;
            System.out.println("Đăng xuất thành công cho tài khoản: " + tenDangNhap);
        } else {
            System.out.println("Tài khoản chưa đăng nhập");
        }
    }
    
    /**
     * Kiểm tra xem tài khoản có đang đăng nhập không
     * @return true nếu đang đăng nhập, false nếu chưa đăng nhập
     */
    public boolean daDangNhap() {
        return this.daLuuTruyCap;
    }
    
    // Getter và Setter
    public String getTenDangNhap() {
        return tenDangNhap;
    }
    
    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }
    
    public String getMatKhau() {
        return matKhau;
    }
    
    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }
    
    public QuyenTruyCap getQuyenTruyCap() {
        return quyenTruyCap;
    }
    
    public void setQuyenTruyCap(QuyenTruyCap quyenTruyCap) {
        this.quyenTruyCap = quyenTruyCap;
    }
}