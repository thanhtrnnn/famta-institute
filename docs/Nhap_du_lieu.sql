SET NOCOUNT ON;

DECLARE @AdminPasswordHash NVARCHAR(64) = 'JAvlGPq9JyTdtvBO6x2llnRI1+gxwIyPqCKAn3THIKk=';
DECLARE @TeacherPasswordHash NVARCHAR(64) = 'zeOD7ujuekQArfehX3FvF5ouuXZGs34InrjW0E5mNBY=';
DECLARE @GuardianPasswordHash NVARCHAR(64) = 'NdQ0j3NBEJow80GZ6H/fG6ynqDFI9vpEjtcfc3UztHU=';
DECLARE @StudentPasswordHash NVARCHAR(64) = 'cDsKPWrXW2SaKK3efYPGJR2kV1SSY7x/9F7HCbCoRIs=';

-- Nam hoc
INSERT INTO NAMHOC (MaNamHoc, TenNamHoc, NgayBatDau, NgayKetThuc)
SELECT src.MaNamHoc, src.TenNamHoc, src.NgayBatDau, src.NgayKetThuc
FROM (VALUES
	('NH00000001', N'Năm học 2024-2025', '2024-09-01', '2025-05-31'),
	('NH00000002', N'Năm học 2023-2024', '2023-09-01', '2024-05-31')
) AS src (MaNamHoc, TenNamHoc, NgayBatDau, NgayKetThuc)
WHERE NOT EXISTS (
	SELECT 1 FROM NAMHOC target WHERE target.MaNamHoc = src.MaNamHoc
);

-- Hoc ky
INSERT INTO HOCKY (MaHocKy, MaNamHoc, ThuTuKy, NgayBatDau, NgayKetThuc)
SELECT src.MaHocKy, src.MaNamHoc, src.ThuTuKy, src.NgayBatDau, src.NgayKetThuc
FROM (VALUES
	('HK00000001', 'NH00000001', 1, '2024-09-01', '2025-01-15'),
	('HK00000002', 'NH00000001', 2, '2025-01-16', '2025-05-31'),
	('HK00000003', 'NH00000002', 1, '2023-09-01', '2024-01-15')
) AS src (MaHocKy, MaNamHoc, ThuTuKy, NgayBatDau, NgayKetThuc)
WHERE NOT EXISTS (
	SELECT 1 FROM HOCKY target WHERE target.MaHocKy = src.MaHocKy
);

-- Khoi
INSERT INTO KHOI (MaKhoi, TenKhoi, SoThuTu)
SELECT src.MaKhoi, src.TenKhoi, src.SoThuTu
FROM (VALUES
	('KH00000001', N'Lớp 6', 6),
	('KH00000002', N'Lớp 7', 7),
	('KH00000003', N'Lớp 8', 8)
) AS src (MaKhoi, TenKhoi, SoThuTu)
WHERE NOT EXISTS (
	SELECT 1 FROM KHOI target WHERE target.MaKhoi = src.MaKhoi
);

-- Khoa
INSERT INTO KHOA (MaKhoa, TenKhoa)
SELECT src.MaKhoa, src.TenKhoa
FROM (VALUES
	('K000000001', N'Khoa Tự nhiên'),
	('K000000002', N'Khoa Khoa học xã hội'),
	('K000000003', N'Khoa Ngoại ngữ')
) AS src (MaKhoa, TenKhoa)
WHERE NOT EXISTS (
	SELECT 1 FROM KHOA target WHERE target.MaKhoa = src.MaKhoa
);

-- Mon hoc
INSERT INTO MONHOC (MaMonHoc, MaKhoa, TenMonHoc)
SELECT src.MaMonHoc, src.MaKhoa, src.TenMonHoc
FROM (VALUES
	('MH00000001', 'K000000001', N'Toán nâng cao'),
	('MH00000002', 'K000000001', N'Vật lý cơ bản'),
	('MH00000003', 'K000000002', N'Ngữ văn ứng dụng'),
	('MH00000004', 'K000000003', N'Tiếng Anh giao tiếp')
) AS src (MaMonHoc, MaKhoa, TenMonHoc)
WHERE NOT EXISTS (
	SELECT 1 FROM MONHOC target WHERE target.MaMonHoc = src.MaMonHoc
);

-- Giao vien
INSERT INTO GIAOVIEN (MaGiaoVien, Ho, TenLot, Ten, GioiTinh, DiaChiEmail, SDT)
SELECT src.MaGiaoVien, src.Ho, src.TenLot, src.Ten, src.GioiTinh, src.DiaChiEmail, src.SDT
FROM (VALUES
	('GV00000001', N'Phan', N'Da', N'Thi', N'Nữ', 'phanphan@gmail.com', '0909123456'),
	('GV00000002', N'Nguyễn', N'Văn', N'Hoàng', N'Nam', 'hoang.nguyen@famta.edu.vn', '0909345678'),
	('GV00000003', N'Trần', N'Thị', N'Lan', N'Nữ', 'lan.tran@famta.edu.vn', '0909456123'),
	('GV00000004', N'Lê', N'Quang', N'Minh', N'Nam', 'minh.le@famta.edu.vn', '0912345678')
) AS src (MaGiaoVien, Ho, TenLot, Ten, GioiTinh, DiaChiEmail, SDT)
WHERE NOT EXISTS (
	SELECT 1 FROM GIAOVIEN target WHERE target.MaGiaoVien = src.MaGiaoVien
);

-- Loai phong hoc
INSERT INTO LOAIPHONGHOC (MaLoaiPhongHoc, TenLoaiPhongHoc)
SELECT src.MaLoaiPhongHoc, src.TenLoaiPhongHoc
FROM (VALUES
	('LPH0000001', N'Phòng học lý thuyết'),
	('LPH0000002', N'Phòng thí nghiệm'),
	('LPH0000003', N'Phòng ngoại ngữ')
) AS src (MaLoaiPhongHoc, TenLoaiPhongHoc)
WHERE NOT EXISTS (
	SELECT 1 FROM LOAIPHONGHOC target WHERE target.MaLoaiPhongHoc = src.MaLoaiPhongHoc
);

-- Phong hoc
INSERT INTO PHONGHOC (MaPhongHoc, MaLoaiPhongHoc, TenPhongHoc)
SELECT src.MaPhongHoc, src.MaLoaiPhongHoc, src.TenPhongHoc
FROM (VALUES
	('PH00000001', 'LPH0000001', N'P101'),
	('PH00000002', 'LPH0000001', N'P202'),
	('PH00000003', 'LPH0000002', N'LAB1'),
	('PH00000004', 'LPH0000003', N'P305')
) AS src (MaPhongHoc, MaLoaiPhongHoc, TenPhongHoc)
WHERE NOT EXISTS (
	SELECT 1 FROM PHONGHOC target WHERE target.MaPhongHoc = src.MaPhongHoc
);

-- Tiet hoc
INSERT INTO TIETHOC (MaTietHoc, MaNamHoc, TenTietHoc, ThoiGianBatDau, ThoiGianKetThuc)
SELECT src.MaTietHoc, src.MaNamHoc, src.TenTietHoc, src.ThoiGianBatDau, src.ThoiGianKetThuc
FROM (VALUES
	('TH00000001', 'NH00000001', N'Tiết 1', '07:00:00', '07:45:00'),
	('TH00000002', 'NH00000001', N'Tiết 2', '07:55:00', '08:40:00'),
	('TH00000003', 'NH00000001', N'Tiết 3', '08:50:00', '09:35:00'),
	('TH00000004', 'NH00000001', N'Tiết 4', '09:45:00', '10:30:00')
) AS src (MaTietHoc, MaNamHoc, TenTietHoc, ThoiGianBatDau, ThoiGianKetThuc)
WHERE NOT EXISTS (
	SELECT 1 FROM TIETHOC target WHERE target.MaTietHoc = src.MaTietHoc
);

-- Lop hoc
INSERT INTO LOPHOC (MaLopHoc, MaMonHoc, MaGiaoVien, MaHocKy, TietHocBatDau, TietHocKetThuc, MaPhongHoc, TenLopHoc)
SELECT src.MaLopHoc, src.MaMonHoc, src.MaGiaoVien, src.MaHocKy, src.TietHocBatDau, src.TietHocKetThuc, src.MaPhongHoc, src.TenLopHoc
FROM (VALUES
	('LH00000001', 'MH00000001', 'GV00000001', 'HK00000001', 'TH00000001', 'TH00000001', 'PH00000001', N'Lớp tài năng Toán 6A1'),
	('LH00000002', 'MH00000002', 'GV00000002', 'HK00000001', 'TH00000002', 'TH00000002', 'PH00000003', N'Vật lý cơ bản 6A1'),
	('LH00000003', 'MH00000003', 'GV00000003', 'HK00000001', 'TH00000003', 'TH00000003', 'PH00000002', N'Ngữ văn ứng dụng 7A1'),
	('LH00000004', 'MH00000004', 'GV00000004', 'HK00000002', 'TH00000004', 'TH00000004', 'PH00000004', N'Tiếng Anh giao tiếp 8A1')
) AS src (MaLopHoc, MaMonHoc, MaGiaoVien, MaHocKy, TietHocBatDau, TietHocKetThuc, MaPhongHoc, TenLopHoc)
WHERE NOT EXISTS (
	SELECT 1 FROM LOPHOC target WHERE target.MaLopHoc = src.MaLopHoc
);

-- Hoc sinh
INSERT INTO HOCSINH (MaHocSinh, Ho, TenLot, Ten, NgaySinh, GioiTinh, NgayNhapHoc)
SELECT src.MaHocSinh, src.Ho, src.TenLot, src.Ten, src.NgaySinh, src.GioiTinh, src.NgayNhapHoc
FROM (VALUES
	('HS00000001', N'Phạm', N'Tuấn', N'Anh', '2012-01-14', N'Nam', '2024-08-15'),
	('HS00000002', N'Nguyễn', N'Bảo', N'Châu', '2012-03-25', N'Nữ', '2024-08-15'),
	('HS00000003', N'Lê', N'Minh', N'Khôi', '2011-10-02', N'Nam', '2024-08-15'),
	('HS00000004', N'Đỗ', N'Thị', N'Hồng', '2012-12-12', N'Nữ', '2024-08-15'),
	('HS00000005', N'Trương', N'Gia', N'Phúc', '2011-08-05', N'Nam', '2023-08-15'),
	('HS00000006', N'Võ', N'Khánh', N'Linh', '2012-04-18', N'Nữ', '2023-08-15')
) AS src (MaHocSinh, Ho, TenLot, Ten, NgaySinh, GioiTinh, NgayNhapHoc)
WHERE NOT EXISTS (
	SELECT 1 FROM HOCSINH target WHERE target.MaHocSinh = src.MaHocSinh
);

-- Loai nguoi giam ho
INSERT INTO LOAINGUOIGIAMHO (MaLoaiNguoiGiamHo, Ten)
SELECT src.MaLoaiNguoiGiamHo, src.Ten
FROM (VALUES
	('LGH0000001', N'Cha'),
	('LGH0000002', N'Mẹ'),
	('LGH0000003', N'Ông'),
	('LGH0000004', N'Bà')
) AS src (MaLoaiNguoiGiamHo, Ten)
WHERE NOT EXISTS (
	SELECT 1 FROM LOAINGUOIGIAMHO target WHERE target.MaLoaiNguoiGiamHo = src.MaLoaiNguoiGiamHo
);

-- Nguoi giam ho
INSERT INTO NGUOIGIAMHO (MaNguoiGiamHo, Ho, TenLot, Ten, DiaChiEmail)
SELECT src.MaNguoiGiamHo, src.Ho, src.TenLot, src.Ten, src.DiaChiEmail
FROM (VALUES
	('GH00000001', N'Trần', N'Xuân', N'Thành', 'thanhchan@gmail.com'),
	('GH00000002', N'Lê', N'Thị', N'Mai', 'le.mai@gmail.com'),
	('GH00000003', N'Nguyễn', N'Đức', N'Phúc', 'phuc.nguyen@gmail.com'),
	('GH00000004', N'Phạm', N'Thị', N'Hương', 'huong.pham@gmail.com'),
	('GH00000005', N'Đỗ', N'Minh', N'Đức', 'duc.do@gmail.com'),
	('GH00000006', N'Võ', N'Thị', N'Thanh', 'thanh.vo@gmail.com')
) AS src (MaNguoiGiamHo, Ho, TenLot, Ten, DiaChiEmail)
WHERE NOT EXISTS (
	SELECT 1 FROM NGUOIGIAMHO target WHERE target.MaNguoiGiamHo = src.MaNguoiGiamHo
);

-- Lien ket hoc sinh - nguoi giam ho
INSERT INTO HOCSINH_NGUOIGIAMHO (MaHocSinh, MaNguoiGiamHo, MaLoaiNguoiGiamHo)
SELECT src.MaHocSinh, src.MaNguoiGiamHo, src.MaLoaiNguoiGiamHo
FROM (VALUES
	('HS00000001', 'GH00000001', 'LGH0000001'),
	('HS00000001', 'GH00000002', 'LGH0000002'),
	('HS00000002', 'GH00000003', 'LGH0000001'),
	('HS00000002', 'GH00000004', 'LGH0000002'),
	('HS00000003', 'GH00000005', 'LGH0000001'),
	('HS00000004', 'GH00000006', 'LGH0000002'),
	('HS00000005', 'GH00000005', 'LGH0000001'),
	('HS00000006', 'GH00000004', 'LGH0000002')
) AS src (MaHocSinh, MaNguoiGiamHo, MaLoaiNguoiGiamHo)
WHERE NOT EXISTS (
	SELECT 1
	FROM HOCSINH_NGUOIGIAMHO target
	WHERE target.MaHocSinh = src.MaHocSinh
		AND target.MaNguoiGiamHo = src.MaNguoiGiamHo
		AND target.MaLoaiNguoiGiamHo = src.MaLoaiNguoiGiamHo
);

-- Tai khoan demo theo vai tro
INSERT INTO TAIKHOAN (TenDangNhap, MatKhauHash, Quyen)
SELECT src.TenDangNhap, src.MatKhauHash, src.Quyen
FROM (VALUES
	('admin', @AdminPasswordHash, 'ADMIN'),
	('teacher', @TeacherPasswordHash, 'GIAO_VIEN'),
	('guardian', @GuardianPasswordHash, 'PHU_HUYNH'),
	('student', @StudentPasswordHash, 'HOC_VIEN'),
	('teacher.hoang', @TeacherPasswordHash, 'GIAO_VIEN'),
	('teacher.lan', @TeacherPasswordHash, 'GIAO_VIEN'),
	('teacher.minh', @TeacherPasswordHash, 'GIAO_VIEN'),
	('guardian.thanh', @GuardianPasswordHash, 'PHU_HUYNH'),
	('guardian.mai', @GuardianPasswordHash, 'PHU_HUYNH'),
	('guardian.phuc', @GuardianPasswordHash, 'PHU_HUYNH'),
	('guardian.huong', @GuardianPasswordHash, 'PHU_HUYNH'),
	('guardian.duc', @GuardianPasswordHash, 'PHU_HUYNH'),
	('guardian.thanhvo', @GuardianPasswordHash, 'PHU_HUYNH'),
	('student.anh', @StudentPasswordHash, 'HOC_VIEN'),
	('student.chau', @StudentPasswordHash, 'HOC_VIEN'),
	('student.khoi', @StudentPasswordHash, 'HOC_VIEN'),
	('student.hong', @StudentPasswordHash, 'HOC_VIEN'),
	('student.phuc', @StudentPasswordHash, 'HOC_VIEN'),
	('student.linh', @StudentPasswordHash, 'HOC_VIEN')
) AS src (TenDangNhap, MatKhauHash, Quyen)
WHERE NOT EXISTS (
	SELECT 1 FROM TAIKHOAN target WHERE target.TenDangNhap = src.TenDangNhap
);

-- Bang diem theo lop
INSERT INTO HOCSINH_LOPHOC (MaHocSinh, MaLopHoc, DiemSo)
SELECT src.MaHocSinh, src.MaLopHoc, src.DiemSo
FROM (VALUES
	('HS00000001', 'LH00000001', 8.5),
	('HS00000001', 'LH00000002', 8.8),
	('HS00000002', 'LH00000001', 9.1),
	('HS00000002', 'LH00000002', 8.4),
	('HS00000003', 'LH00000003', 7.9),
	('HS00000004', 'LH00000003', 8.7),
	('HS00000005', 'LH00000004', 8.2),
	('HS00000006', 'LH00000004', 9.0)
) AS src (MaHocSinh, MaLopHoc, DiemSo)
WHERE NOT EXISTS (
	SELECT 1
	FROM HOCSINH_LOPHOC target
	WHERE target.MaHocSinh = src.MaHocSinh
		AND target.MaLopHoc = src.MaLopHoc
);

-- Tong hop hoc sinh theo nam hoc/khoi/lop
INSERT INTO HOCSINH_NAMHOC_KHOI_LOPHOC (MaHocSinh, MaNamHoc, MaKhoi, MaLopHoc, DiemSo)
SELECT src.MaHocSinh, src.MaNamHoc, src.MaKhoi, src.MaLopHoc, src.DiemSo
FROM (VALUES
	('HS00000001', 'NH00000001', 'KH00000001', 'LH00000001', 8.5),
	('HS00000001', 'NH00000001', 'KH00000001', 'LH00000002', 8.8),
	('HS00000002', 'NH00000001', 'KH00000001', 'LH00000001', 9.1),
	('HS00000002', 'NH00000001', 'KH00000001', 'LH00000002', 8.4),
	('HS00000003', 'NH00000001', 'KH00000002', 'LH00000003', 7.9),
	('HS00000004', 'NH00000001', 'KH00000002', 'LH00000003', 8.7),
	('HS00000005', 'NH00000001', 'KH00000003', 'LH00000004', 8.2),
	('HS00000006', 'NH00000001', 'KH00000003', 'LH00000004', 9.0)
) AS src (MaHocSinh, MaNamHoc, MaKhoi, MaLopHoc, DiemSo)
WHERE NOT EXISTS (
	SELECT 1
	FROM HOCSINH_NAMHOC_KHOI_LOPHOC target
	WHERE target.MaHocSinh = src.MaHocSinh
		AND target.MaNamHoc = src.MaNamHoc
		AND target.MaKhoi = src.MaKhoi
		AND target.MaLopHoc = src.MaLopHoc
);
