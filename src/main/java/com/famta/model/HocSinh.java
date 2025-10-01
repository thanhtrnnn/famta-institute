package com.famta.model;

import java.time.LocalDate;
import java.util.List;

public class HocSinh {
    private String maHocSinh;
    private String ho;
    private String tenLot;
    private String ten;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private LocalDate ngayNhapHoc;
    private List<MoiQuanHeGiamHo> danhSachNguoiGiamHo;
    private List<DangKyHoc> quaTrinhHocTap;

    public void dangKyLopHoc(LopHoc lopHoc) {
        
    }

    public List<DiemSo> layBangDiem() {
        return null;
    }
}
