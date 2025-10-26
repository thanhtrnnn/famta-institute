package com.famta.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HocSinh {
    private final String maHocSinh;
    private String ho;
    private String tenLot;
    private String ten;
    private LocalDate ngaySinh;
    private LocalDate ngayNhapHoc;
    private String gioiTinh;
    private TaiKhoan taiKhoan;
    private final List<MoiQuanHeGiamHo> danhSachGiamHo = new ArrayList<>();
    private final List<DangKyHoc> quaTrinhHocTap = new ArrayList<>();

    public HocSinh(String maHocSinh, String ho, String tenLot, String ten, LocalDate ngaySinh, String gioiTinh, LocalDate ngayNhapHoc) {
        this.maHocSinh = Objects.requireNonNull(maHocSinh, "maHocSinh");
        this.ho = ho;
        this.tenLot = tenLot;
        this.ten = ten;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.ngayNhapHoc = ngayNhapHoc;
    }

    public String getMaHocSinh() {
        return maHocSinh;
    }

    public String getHo() {
        return ho;
    }

    public void setHo(String ho) {
        this.ho = ho;
    }

    public String getTenLot() {
        return tenLot;
    }

    public void setTenLot(String tenLot) {
        this.tenLot = tenLot;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public LocalDate getNgayNhapHoc() {
        return ngayNhapHoc;
    }

    public void setNgayNhapHoc(LocalDate ngayNhapHoc) {
        this.ngayNhapHoc = ngayNhapHoc;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public List<MoiQuanHeGiamHo> getDanhSachNguoiGiamHo() {
        return Collections.unmodifiableList(danhSachGiamHo);
    }

    public List<DangKyHoc> getQuaTrinhHocTap() {
        return Collections.unmodifiableList(quaTrinhHocTap);
    }

    public String getHoTenDayDu() {
        return ho + " " + (tenLot != null && !tenLot.isBlank() ? tenLot + " " : "") + ten;
    }

    public void themMoiQuanHeGiamHo(NguoiGiamHo nguoiGiamHo, LoaiNguoiGiamHo loaiNguoiGiamHo) {
        MoiQuanHeGiamHo quanHe = new MoiQuanHeGiamHo(this, nguoiGiamHo, loaiNguoiGiamHo);
        danhSachGiamHo.add(quanHe);
        if (nguoiGiamHo != null) {
            nguoiGiamHo.addMoiQuanHeInternal(quanHe);
        }
    }

    public void dangKyLopHoc(LopHoc lopHoc, Khoi khoi) {
    new DangKyHoc(this, Objects.requireNonNull(lopHoc, "lopHoc"), Objects.requireNonNull(khoi, "khoi"));
    }

    public List<DangKyHoc> layBangDiem() {
        return Collections.unmodifiableList(quaTrinhHocTap);
    }

    public String xemLichHoc() {
        return quaTrinhHocTap.stream()
                .map(DangKyHoc::getLopHoc)
                .map(lop -> {
                    String batDau = lop.getTietHocBatDau() != null ? lop.getTietHocBatDau().getTenTietHoc() : "?";
                    String ketThuc = lop.getTietHocKetThuc() != null ? lop.getTietHocKetThuc().getTenTietHoc() : "?";
                    return lop.getTenLopHoc() + " - " + batDau + " -> " + ketThuc;
                })
                .collect(Collectors.joining("\n"));
    }

    void addDangKyInternal(DangKyHoc dangKyHoc) {
        if (!quaTrinhHocTap.contains(dangKyHoc)) {
            quaTrinhHocTap.add(dangKyHoc);
        }
    }

    void removeDangKyInternal(DangKyHoc dangKyHoc) {
        quaTrinhHocTap.remove(dangKyHoc);
    }
}
