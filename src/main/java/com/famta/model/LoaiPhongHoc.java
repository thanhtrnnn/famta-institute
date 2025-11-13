package com.famta.model;

public class LoaiPhongHoc {
    private String maLoaiPhongHoc;
    private String tenLoaiPhongHoc;

    public LoaiPhongHoc() {
    }

    public LoaiPhongHoc(String maLoaiPhongHoc, String tenLoaiPhongHoc) {
        this.maLoaiPhongHoc = maLoaiPhongHoc;
        this.tenLoaiPhongHoc = tenLoaiPhongHoc;
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
    
    @Override
    public String toString() {
        return tenLoaiPhongHoc;
    }
}
