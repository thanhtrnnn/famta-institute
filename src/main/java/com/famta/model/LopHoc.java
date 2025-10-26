package com.famta.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class LopHoc {
    private final String maLopHoc;
    private String tenLopHoc;
    private MonHoc monHoc;
    private GiaoVien giaoVienPhuTrach;
    private HocKy hocKy;
    private PhongHoc phongHoc;
    private TietHoc tietHocBatDau;
    private TietHoc tietHocKetThuc;
    private final List<DangKyHoc> danhSachDangKy = new ArrayList<>();
    private final Map<HocSinh, String> trangThaiDiemDanh = new HashMap<>();

    public LopHoc(String maLopHoc, String tenLopHoc, MonHoc monHoc, GiaoVien giaoVienPhuTrach, HocKy hocKy,
                  PhongHoc phongHoc, TietHoc tietHocBatDau, TietHoc tietHocKetThuc) {
        this.maLopHoc = Objects.requireNonNull(maLopHoc, "maLopHoc");
        this.tenLopHoc = tenLopHoc;
        this.monHoc = monHoc;
        setGiaoVienPhuTrach(giaoVienPhuTrach);
        this.hocKy = hocKy;
        this.phongHoc = phongHoc;
        this.tietHocBatDau = tietHocBatDau;
        this.tietHocKetThuc = tietHocKetThuc;
    }

    public String getMaLopHoc() {
        return maLopHoc;
    }

    public String getTenLopHoc() {
        return tenLopHoc;
    }

    public void setTenLopHoc(String tenLopHoc) {
        this.tenLopHoc = tenLopHoc;
    }

    public MonHoc getMonHoc() {
        return monHoc;
    }

    public void setMonHoc(MonHoc monHoc) {
        this.monHoc = monHoc;
    }

    public GiaoVien getGiaoVienPhuTrach() {
        return giaoVienPhuTrach;
    }

    public void setGiaoVienPhuTrach(GiaoVien giaoVienPhuTrach) {
        if (this.giaoVienPhuTrach != null) {
            this.giaoVienPhuTrach.removeLopDangDayInternal(this);
        }
        this.giaoVienPhuTrach = giaoVienPhuTrach;
        if (giaoVienPhuTrach != null) {
            giaoVienPhuTrach.addLopDangDayInternal(this);
        }
    }

    public HocKy getHocKy() {
        return hocKy;
    }

    public void setHocKy(HocKy hocKy) {
        this.hocKy = hocKy;
    }

    public PhongHoc getPhongHoc() {
        return phongHoc;
    }

    public void setPhongHoc(PhongHoc phongHoc) {
        this.phongHoc = phongHoc;
    }

    public TietHoc getTietHocBatDau() {
        return tietHocBatDau;
    }

    public void setTietHocBatDau(TietHoc tietHocBatDau) {
        this.tietHocBatDau = tietHocBatDau;
    }

    public TietHoc getTietHocKetThuc() {
        return tietHocKetThuc;
    }

    public void setTietHocKetThuc(TietHoc tietHocKetThuc) {
        this.tietHocKetThuc = tietHocKetThuc;
    }

    public List<DangKyHoc> getDanhSachDangKy() {
        return Collections.unmodifiableList(danhSachDangKy);
    }

    public List<HocSinh> layDanhSachHocSinh() {
        return danhSachDangKy.stream()
                .map(DangKyHoc::getHocSinh)
                .collect(Collectors.toUnmodifiableList());
    }

    public DangKyHoc themHocSinh(HocSinh hocSinh, Khoi khoi) {
        return new DangKyHoc(Objects.requireNonNull(hocSinh, "hocSinh"), this, Objects.requireNonNull(khoi, "khoi"));
    }

    void themDangKyNoCascade(DangKyHoc dangKyHoc) {
        if (!danhSachDangKy.contains(dangKyHoc)) {
            danhSachDangKy.add(dangKyHoc);
        }
    }

    void xoaDangKyNoCascade(DangKyHoc dangKyHoc) {
        danhSachDangKy.remove(dangKyHoc);
        trangThaiDiemDanh.remove(dangKyHoc.getHocSinh());
    }

    public void ghiNhanDiemDanh(HocSinh hocSinh, String trangThai) {
        trangThaiDiemDanh.put(hocSinh, trangThai);
    }

    public String xemTrangThaiDiemDanh(HocSinh hocSinh) {
        return trangThaiDiemDanh.get(hocSinh);
    }
}