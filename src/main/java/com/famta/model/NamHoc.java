package com.famta.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class NamHoc {
    private final String maNamHoc;
    private String tenNamHoc;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private final List<HocKy> danhSachHocKy = new ArrayList<>();
    private final List<TietHoc> khungGioTietHoc = new ArrayList<>();

    public NamHoc(String maNamHoc, String tenNamHoc, LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        this.maNamHoc = Objects.requireNonNull(maNamHoc, "maNamHoc");
        this.tenNamHoc = tenNamHoc;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
    }

    public String getMaNamHoc() {
        return maNamHoc;
    }

    public String getTenNamHoc() {
        return tenNamHoc;
    }

    public void setTenNamHoc(String tenNamHoc) {
        this.tenNamHoc = tenNamHoc;
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public List<HocKy> layDanhSachHocKy() {
        return Collections.unmodifiableList(danhSachHocKy);
    }

    public List<TietHoc> getKhungGioTietHoc() {
        return Collections.unmodifiableList(khungGioTietHoc);
    }

    public void themHocKy(HocKy hocKy) {
        if (hocKy != null && !danhSachHocKy.contains(hocKy)) {
            danhSachHocKy.add(hocKy);
            hocKy.setNamHoc(this);
        }
    }

    void themHocKyInternal(HocKy hocKy) {
        if (!danhSachHocKy.contains(hocKy)) {
            danhSachHocKy.add(hocKy);
        }
    }

    void xoaHocKyInternal(HocKy hocKy) {
        danhSachHocKy.remove(hocKy);
    }

    public void themTietHoc(TietHoc tietHoc) {
        if (tietHoc != null && !khungGioTietHoc.contains(tietHoc)) {
            khungGioTietHoc.add(tietHoc);
            tietHoc.setNamHoc(this);
        }
    }

    void themTietHocInternal(TietHoc tietHoc) {
        if (!khungGioTietHoc.contains(tietHoc)) {
            khungGioTietHoc.add(tietHoc);
        }
    }

    void xoaTietHocInternal(TietHoc tietHoc) {
        khungGioTietHoc.remove(tietHoc);
    }
}
