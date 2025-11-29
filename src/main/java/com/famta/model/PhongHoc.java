package com.famta.model;

import java.util.Objects;

public class PhongHoc {
    private final String maPhongHoc;
    private String tenPhongHoc;
    private String maLoaiPhongHoc;

    public PhongHoc(String maPhongHoc, String tenPhongHoc, String maLoaiPhongHoc) {
        this.maPhongHoc = Objects.requireNonNull(maPhongHoc, "maPhongHoc");
        this.tenPhongHoc = tenPhongHoc;
        this.maLoaiPhongHoc = maLoaiPhongHoc;
    }

    public PhongHoc(String maPhongHoc, String tenPhongHoc, LoaiPhongHoc loaiPhongHoc) {
        this(maPhongHoc, tenPhongHoc,
                loaiPhongHoc != null ? loaiPhongHoc.getMaLoaiPhongHoc() : null);
    }

    public String getMaPhongHoc() {
        return maPhongHoc;
    }

    public String getTenPhongHoc() {
        return tenPhongHoc;
    }

    public void setTenPhongHoc(String tenPhongHoc) {
        this.tenPhongHoc = tenPhongHoc;
    }

    public String getMaLoaiPhongHoc() {
        return maLoaiPhongHoc;
    }

    public void setMaLoaiPhongHoc(String maLoaiPhongHoc) {
        this.maLoaiPhongHoc = maLoaiPhongHoc;
    }
}
