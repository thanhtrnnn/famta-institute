package com.famta.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HocSinh extends NguoiDung {
    private final String maHocSinh;
    private LocalDate ngaySinh;
    private LocalDate ngayNhapHoc;
    private String gioiTinh;
    private final List<MoiQuanHeGiamHo> danhSachGiamHo = new ArrayList<>();
    private final List<DangKyHoc> quaTrinhHocTap = new ArrayList<>();

    public HocSinh(String maHocSinh, String ho, String tenLot, String ten, LocalDate ngaySinh, String gioiTinh, LocalDate ngayNhapHoc) {
        super(ho, tenLot, ten);
        this.maHocSinh = Objects.requireNonNull(maHocSinh, "maHocSinh");
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.ngayNhapHoc = ngayNhapHoc;
    }

    public String getMaHocSinh() {
        return maHocSinh;
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

    public List<MoiQuanHeGiamHo> getDanhSachNguoiGiamHo() {
        return Collections.unmodifiableList(danhSachGiamHo);
    }

    public List<DangKyHoc> getQuaTrinhHocTap() {
        return Collections.unmodifiableList(quaTrinhHocTap);
    }

    public void themMoiQuanHeGiamHo(NguoiGiamHo nguoiGiamHo, LoaiNguoiGiamHo loaiNguoiGiamHo) {
        MoiQuanHeGiamHo quanHe = new MoiQuanHeGiamHo(this, nguoiGiamHo, loaiNguoiGiamHo);
        danhSachGiamHo.add(quanHe);
        if (nguoiGiamHo != null) {
            nguoiGiamHo.addMoiQuanHe(quanHe);
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
