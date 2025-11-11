-- Tạo bảng HOCSINH
CREATE TABLE HOCSINH(
    MaHocSinh VARCHAR(10) PRIMARY KEY,
    Ho        NVARCHAR(20),
    TenLot    NVARCHAR(20),
    Ten       NVARCHAR(20),
    GioiTinh  NVARCHAR(10),
    NgaySinh  DATE,
    NgayNhapHoc DATE
);

-- Tạo bảng TAIKHOAN
CREATE TABLE TAIKHOAN(
    TenDangNhap NVARCHAR(50) PRIMARY KEY,
    MatKhauHash NVARCHAR(88) NOT NULL,
    Quyen       NVARCHAR(20) NOT NULL
);

-- Tạo bảng NGUOIGIAMHO
CREATE TABLE NGUOIGIAMHO(
    MaNguoiGiamHo VARCHAR(10) PRIMARY KEY,
    Ho            NVARCHAR(20),
    TenLot        NVARCHAR(20),
    Ten           NVARCHAR(20),
    DiaChiEmail   VARCHAR(100),
    SDT           VARCHAR(15)
);

-- Tạo bảng LOAINGUOIGIAMHO
CREATE TABLE LOAINGUOIGIAMHO(
    MaLoaiNguoiGiamHo VARCHAR(10) PRIMARY KEY,
    Ten               NVARCHAR(20)
);

-- Tạo bảng HOCSINH_NGUOIGIAMHO
CREATE TABLE HOCSINH_NGUOIGIAMHO(
    MaHocSinh         VARCHAR(10) NOT NULL,
    MaNguoiGiamHo     VARCHAR(10) NOT NULL,
    MaLoaiNguoiGiamHo VARCHAR(10) NOT NULL,

    PRIMARY KEY (MaHocSinh, MaNguoiGiamHo, MaLoaiNguoiGiamHo),
    FOREIGN KEY (MaHocSinh)         REFERENCES HOCSINH(MaHocSinh),
    FOREIGN KEY (MaNguoiGiamHo)     REFERENCES NGUOIGIAMHO(MaNguoiGiamHo),
    FOREIGN KEY (MaLoaiNguoiGiamHo) REFERENCES LOAINGUOIGIAMHO(MaLoaiNguoiGiamHo)
);

-- Tạo bảng NAMHOC
CREATE TABLE NAMHOC (
    MaNamHoc    VARCHAR(10) PRIMARY KEY,
    TenNamHoc   NVARCHAR(20),
    NgayBatDau  DATE,
    NgayKetThuc DATE
);

-- Tạo bảng HOCKY
CREATE TABLE HOCKY (
    MaHocKy     VARCHAR(10) PRIMARY KEY,
    MaNamHoc    VARCHAR(10),
    ThuTuKy     INT,
    NgayBatDau  DATE,
    NgayKetThuc DATE,
    FOREIGN KEY (MaNamHoc) REFERENCES NAMHOC(MaNamHoc)
);

-- Tạo bảng KHOI
CREATE TABLE KHOI (
    MaKhoi   VARCHAR(10) PRIMARY KEY,
    TenKhoi  NVARCHAR(20),
    SoThuTu  INT
);

-- Tạo bảng KHOA
CREATE TABLE KHOA (
    MaKhoa   VARCHAR(10) PRIMARY KEY,
    TenKhoa  NVARCHAR(50)
);

-- Tạo bảng MONHOC
CREATE TABLE MONHOC (
    MaMonHoc   VARCHAR(10) PRIMARY KEY,
    MaKhoa     VARCHAR(10),
    TenMonHoc  NVARCHAR(50),
    FOREIGN KEY (MaKhoa) REFERENCES KHOA(MaKhoa)
);

-- Tạo bảng GIAOVIEN
CREATE TABLE GIAOVIEN (
    MaGiaoVien   VARCHAR(10) PRIMARY KEY,
    Ho           NVARCHAR(20),
    TenLot       NVARCHAR(20),
    Ten          NVARCHAR(20),
    GioiTinh     NVARCHAR(10),
    DiaChiEmail  VARCHAR(100),
    SDT          VARCHAR(15)
);

-- Tạo bảng LOAIPHONGHOC
CREATE TABLE LOAIPHONGHOC (
    MaLoaiPhongHoc     VARCHAR(10) PRIMARY KEY,
    TenLoaiPhongHoc    NVARCHAR(50)
);

-- Tạo bảng PHONGHOC
CREATE TABLE PHONGHOC (
    MaPhongHoc      VARCHAR(10) PRIMARY KEY,
    MaLoaiPhongHoc  VARCHAR(10),
    TenPhongHoc     NVARCHAR(20),
    FOREIGN KEY (MaLoaiPhongHoc) REFERENCES LOAIPHONGHOC(MaLoaiPhongHoc)
);

-- Tạo bảng TIETHOC
CREATE TABLE TIETHOC (
    MaTietHoc        VARCHAR(10) PRIMARY KEY,
    MaNamHoc         VARCHAR(10),
    TenTietHoc       NVARCHAR(20),
    ThoiGianBatDau   TIME,
    ThoiGianKetThuc  TIME,
    FOREIGN KEY (MaNamHoc) REFERENCES NAMHOC(MaNamHoc)
);

-- Tạo bảng LOPHOC
CREATE TABLE LOPHOC (
    MaLopHoc       VARCHAR(10) PRIMARY KEY,
    MaMonHoc       VARCHAR(10),
    MaGiaoVien     VARCHAR(10),
    MaHocKy        VARCHAR(10),
    TietHocBatDau  VARCHAR(10),
    TietHocKetThuc VARCHAR(10),
    MaPhongHoc     VARCHAR(10),
    TenLopHoc      NVARCHAR(50),
    FOREIGN KEY (MaMonHoc)       REFERENCES MONHOC(MaMonHoc),
    FOREIGN KEY (MaGiaoVien)     REFERENCES GIAOVIEN(MaGiaoVien),
    FOREIGN KEY (MaHocKy)        REFERENCES HOCKY(MaHocKy),
    FOREIGN KEY (TietHocBatDau)  REFERENCES TIETHOC(MaTietHoc),
    FOREIGN KEY (TietHocKetThuc) REFERENCES TIETHOC(MaTietHoc),
    FOREIGN KEY (MaPhongHoc)     REFERENCES PHONGHOC(MaPhongHoc)
);

-- Tạo bảng HOCSINH_LOPHOC
CREATE TABLE HOCSINH_LOPHOC (
    MaHocSinh  VARCHAR(10),
    MaLopHoc   VARCHAR(10),
    DiemThuongXuyen FLOAT,
    DiemGiuaKy      FLOAT,
    DiemCuoiKy      FLOAT,
    PRIMARY KEY (MaHocSinh, MaLopHoc),
    FOREIGN KEY (MaHocSinh) REFERENCES HOCSINH(MaHocSinh),
    FOREIGN KEY (MaLopHoc)  REFERENCES LOPHOC(MaLopHoc)
);

-- Tạo bảng HOCSINH_NAMHOC_KHOI_LOPHOC
CREATE TABLE HOCSINH_NAMHOC_KHOI_LOPHOC (
    MaHocSinh  VARCHAR(10),
    MaNamHoc   VARCHAR(10),
    MaKhoi     VARCHAR(10),
    MaLopHoc   VARCHAR(10),
    DiemThuongXuyen FLOAT,
    DiemGiuaKy      FLOAT,
    DiemCuoiKy      FLOAT,
    PRIMARY KEY (MaHocSinh, MaNamHoc, MaKhoi, MaLopHoc),
    FOREIGN KEY (MaHocSinh) REFERENCES HOCSINH(MaHocSinh),
    FOREIGN KEY (MaNamHoc)  REFERENCES NAMHOC(MaNamHoc),
    FOREIGN KEY (MaKhoi)    REFERENCES KHOI(MaKhoi),
    FOREIGN KEY (MaLopHoc)  REFERENCES LOPHOC(MaLopHoc)
);
