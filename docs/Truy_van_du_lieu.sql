-- Lấy danh sách tất cả học sinh
SELECT* FROM NGUOIGIAMHO

-- Lấy danh cách tất cả học sinh có tên chứa chữ 'Anh'
SELECT* FROM HOCSINH
WHERE Ten LIKE N'Anh'

-- Lấy danh sách tất cả giáo viên có họ là 'Phan'
SELECT* FROM GIAOVIEN
WHERE Ho = 'Phan'

-- Lấy thông tin học sinh cùng người giám hộ của học sinh đó
SELECT HS.MaHocSinh, HS.Ho, HS.TenLot, HS.Ten, 
       NGH.Ho AS HoNguoiGiamHo, NGH.TenLot AS TenLotNguoiGiamHo,
       NGH.Ten AS TenNguoiGiamHo, LNGH.Ten AS LoaiNguoiGiamHo
FROM HOCSINH HS
JOIN HOCSINH_NGUOIGIAMHO HSNGH ON HS.MaHocSinh = HSNGH.MaHocSinh
JOIN NGUOIGIAMHO NGH ON HSNGH.MaNguoiGiamHo = NGH.MaNguoiGiamHo
JOIN LOAINGUOIGIAMHO LNGH ON HSNGH.MaLoaiNguoiGiamHo = LNGH.MaLoaiNguoiGiamHo;

--  Liệt kê học sinh và lớp học mà học sinh đang theo học
SELECT HS.MaHocSinh, HS.Ho, HS.TenLot, HS.Ten,
       LH.MaLopHoc, LH.TenLopHoc
FROM HOCSINH HS
JOIN HOCSINH_LOPHOC HSLH ON HS.MaHocSinh = HSLH.MaHocSinh
JOIN LOPHOC LH ON HSLH.MaLopHoc = LH.MaLopHoc;

-- Tìm thông tin lớp học bao gồm giáo viên, môn học, phòng học
SELECT LH.MaLopHoc, LH.TenLopHoc,
       MH.TenMonHoc,
       GV.Ho + ' ' + GV.TenLot + ' ' + GV.Ten AS TenGiaoVien,
       PH.TenPhongHoc
FROM LOPHOC LH
JOIN MONHOC MH ON LH.MaMonHoc = MH.MaMonHoc
JOIN GIAOVIEN GV ON LH.MaGiaoVien = GV.MaGiaoVien
JOIN PHONGHOC PH ON LH.MaPhongHoc = PH.MaPhongHoc;

-- Tính điểm trung bình của học sinh trong từng năm học và khối
SELECT MaHocSinh, MaNamHoc, MaKhoi,
       AVG(DiemSo) AS DiemTrungBinh
FROM HOCSINH_NAMHOC_KHOI_LOPHOC
GROUP BY MaHocSinh, MaNamHoc, MaKhoi;


