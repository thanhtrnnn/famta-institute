package com.famta.model;

import java.util.Objects;

public class PhongHoc {
    private final String maPhongHoc;
    private String tenPhongHoc;
    private String maLoaiPhongHoc;
    private String tenLoaiPhongHoc;

    public PhongHoc(String maPhongHoc, String tenPhongHoc, String maLoaiPhongHoc, String tenLoaiPhongHoc) {
        this.maPhongHoc = Objects.requireNonNull(maPhongHoc, "maPhongHoc");
        this.tenPhongHoc = tenPhongHoc;
        this.maLoaiPhongHoc = maLoaiPhongHoc;
        this.tenLoaiPhongHoc = tenLoaiPhongHoc;
    }

    public PhongHoc(String maPhongHoc, String tenPhongHoc, LoaiPhongHoc loaiPhongHoc) {
        this(maPhongHoc, tenPhongHoc,
                loaiPhongHoc != null ? loaiPhongHoc.getMaLoaiPhongHoc() : null,
                loaiPhongHoc != null ? loaiPhongHoc.getTenLoaiPhongHoc() : null);
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

    public String getTenLoaiPhongHoc() {
        return tenLoaiPhongHoc;
    }

    public void setTenLoaiPhongHoc(String tenLoaiPhongHoc) {
        this.tenLoaiPhongHoc = tenLoaiPhongHoc;
    }

    public void setLoaiPhongHoc(LoaiPhongHoc loaiPhongHoc) {
        if (loaiPhongHoc == null) {
            this.maLoaiPhongHoc = null;
            this.tenLoaiPhongHoc = null;
        } else {
            this.maLoaiPhongHoc = loaiPhongHoc.getMaLoaiPhongHoc();
            this.tenLoaiPhongHoc = loaiPhongHoc.getTenLoaiPhongHoc();
        }
    }
}
