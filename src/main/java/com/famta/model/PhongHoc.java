package com.famta.model;

public class PhongHoc {    private String maPhongHoc;
    private String tenPhongHoc;
    private LoaiPhongHoc loaiPhongHoc;

    // Constructors
    public PhongHoc() {}
    public PhongHoc(String maPhongHoc, String tenPhongHoc, LoaiPhongHoc loaiPhongHoc) { this.maPhongHoc = maPhongHoc; this.tenPhongHoc = tenPhongHoc; this.loaiPhongHoc = loaiPhongHoc; }

    // Getters
    public String getMaPhongHoc() { return maPhongHoc; }
    public String getTenPhongHoc() { return tenPhongHoc; }
    public LoaiPhongHoc getLoaiPhongHoc() { return loaiPhongHoc; }

    // Setters
    public void setMaPhongHoc(String maPhongHoc) { this.maPhongHoc = maPhongHoc; }
    public void setTenPhongHoc(String tenPhongHoc) { this.tenPhongHoc = tenPhongHoc; }
    public void setLoaiPhongHoc(LoaiPhongHoc loaiPhongHoc) { this.loaiPhongHoc = loaiPhongHoc; }
}
