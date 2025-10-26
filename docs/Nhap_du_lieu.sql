-- Nhập dữ liệu vào bảng HOCSINH
INSERT INTO HOCSINH (MaHocSinh, Ho, TenLot, Ten, NgaySinh, NgayNhapHoc)
VALUES ('HS00000001', N'Pham', N'Tuan', N'Anh', '2005-01-14', '2025-07-04');

-- Nhập dữ liệu vào bảng NGUOIGIAMHO
INSERT INTO NGUOIGIAMHO (MaNguoiGiamHo, Ho, TenLot, Ten, DiaChiEmail)
VALUES ('GH00000001', N'Tran', N'Xuan', N'Thanh', 'thanhchan@gmail.com');

-- Nhập dữ liệu vào bảng LOAINGUOIGIAMHO
INSERT INTO LOAINGUOIGIAMHO (MaLoaiNguoiGiamHo, Ten)
VALUES ('LGH0000001', N'Cha');

-- Nhập dữ liệu vào bảng HOCSINH_NGUOIGIAMHO
INSERT INTO HOCSINH_NGUOIGIAMHO (MaHocSinh, MaNguoiGiamHo, MaLoaiNguoiGiamHo)
VALUES ('HS00000001', 'GH00000001', 'LGH0000001');

-- Nhập dữ liệu vào bảng NAMHOC
INSERT INTO NAMHOC (MaNamHoc, TenNamHoc, NgayBatDau, NgayKetThuc)
VALUES ('NH00000001', N'Năm học 2024-2025', '2024-09-01', '2025-05-31');

-- Nhập dữ liệu vào bảng HOCKY
INSERT INTO HOCKY (MaHocKy, MaNamHoc, ThuTuKy, NgayBatDau, NgayKetThuc)
VALUES ('HK00000001', 'NH00000001', 1, '2024-09-01', '2025-01-15');

-- Nhập dữ liệu vào bảng KHOI
INSERT INTO KHOI (MaKhoi, TenKhoi, SoThuTu)
VALUES ('KH00000001', N'Lớp 6', 6);

-- Nhập dữ liệu vào bảng KHOA
INSERT INTO KHOA (MaKhoa, TenKhoa)
VALUES ('K000000001', N'Khoa Tự nhiên');

-- Nhập dữ liệu vào bảng MONHOC
INSERT INTO MONHOC (MaMonHoc, MaKhoa, TenMonHoc)
VALUES ('MH00000001', 'K000000001', N'Toán');

-- Nhập dữ liệu vào bảng GIAOVIEN
INSERT INTO GIAOVIEN (MaGiaoVien, Ho, TenLot, Ten, GioiTinh, DiaChiEmail, SDT)
VALUES ('GV00000001', N'Phan', N'Da', N'Thi', N'Nu', 'phanphan@gmail.com', '0909123456');

-- Nhập dữ liệu vào bảng LOAIPHONGHOC
INSERT INTO LOAIPHONGHOC (MaLoaiPhongHoc, TenLoaiPhongHoc)
VALUES ('LPH0000001', N'Phòng học lý thuyết');

-- Nhập dữ liệu vào bảng PHONGHOC
INSERT INTO PHONGHOC (MaPhongHoc, MaLoaiPhongHoc, TenPhongHoc)
VALUES ('PH00000001', 'LPH0000001', N'P101');

-- Nhập dữ liệu vào bảng TIETHOC
INSERT INTO TIETHOC (MaTietHoc, MaNamHoc, TenTietHoc, ThoiGianBatDau, ThoiGianKetThuc)
VALUES ('TH00000001', 'NH00000001', N'Tiết 1', '07:00:00', '07:45:00');

-- Nhập dữ liệu vào bảng LOPHOC
INSERT INTO LOPHOC (MaLopHoc, MaMonHoc, MaGiaoVien, MaHocKy, TietHocBatDau, TietHocKetThuc, MaPhongHoc, TenLopHoc)
VALUES ('LH00000001', 'MH00000001', 'GV00000001', 'HK00000001', 'TH00000001', 'TH00000001', 'PH00000001', N'Lớp tài năng');

-- Nhập dữ liệu vào bảng HOCSINH_LOPHOC
INSERT INTO HOCSINH_LOPHOC (MaHocSinh, MaLopHoc, DiemSo)
VALUES ('HS00000001', 'LH00000001', 8.5);

-- Nhập dữ liệu vào bảng HOCSINH_NAMHOC_KHOI_LOPHOC
INSERT INTO HOCSINH_NAMHOC_KHOI_LOPHOC (MaHocSinh, MaNamHoc, MaKhoi, MaLopHoc, DiemSo)
VALUES ('HS00000001', 'NH00000001', 'KH00000001', 'LH00000001', 8.5);
