package com.famta.model;

public class MonHoc {    private String maMonHoc;
    private String tenMonHoc;
    private Khoa khoa;

    // Constructors
    public MonHoc() {}
    public MonHoc(String maMonHoc, String tenMonHoc, Khoa khoa) { this.maMonHoc = maMonHoc; this.tenMonHoc = tenMonHoc; this.khoa = khoa; }

    // Getters
    public String getMaMonHoc() { return maMonHoc; }
    public String getTenMonHoc() { return tenMonHoc; }
    public Khoa getKhoa() { return khoa; }

    // Setters
    public void setMaMonHoc(String maMonHoc) { this.maMonHoc = maMonHoc; }
    public void setTenMonHoc(String tenMonHoc) { this.tenMonHoc = tenMonHoc; }
    public void setKhoa(Khoa khoa) { this.khoa = khoa; }
}
