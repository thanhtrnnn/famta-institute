-- Cập nhật địa chỉ Email của người giám hộ
UPDATE NGUOIGIAMHO
SET DiaChiEmail = 'thanhchan_update@gmail.com'
WHERE MaNguoiGiamHo = 'GH00000001'

-- Cập nhật tên lớp học
UPDATE LOPHOC
SET TenLopHoc = N'Lớp Toán nâng cao'
WHERE MaLopHoc = 'LH00000001'

-- Cập nhật điểm cho học sinh
UPDATE HOCSINH_LOPHOC
SET DiemThuongXuyen = 9.0
WHERE MaHocSinh = 'HS00000001' AND MaLopHoc = 'LH00000001';

-- Xoá học sinh khỏi lớp học
DELETE FROM HOCSINH_LOPHOC
WHERE MaHocSinh = 'HS00000001' AND MaLopHoc = 'LH00000001';

-- Xoá học sinh khỏi hệ thống
-- Bước 1: Xoá các bản ghi liên quan 
DELETE FROM HOCSINH_NAMHOC_KHOI_LOPHOC WHERE MaHocSinh = 'HS00000001';
DELETE FROM HOCSINH_LOPHOC WHERE MaHocSinh = 'HS00000001';
DELETE FROM HOCSINH_NGUOIGIAMHO WHERE MaHocSinh = 'HS00000001';
-- Bước 2: Xoá học sinh khỏi bảng chính
DELETE FROM HOCSINH WHERE MaHocSinh = 'HS00000001';
