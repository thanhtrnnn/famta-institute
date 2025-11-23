package com.famta.model;

import java.util.Objects;

public class LichHoc {
    private final String maLichHoc;
    private LopHoc lopHoc;
    private int thu; // 2-8
    private TietHoc tietBatDau;
    private TietHoc tietKetThuc;
    private PhongHoc phongHoc;

    public LichHoc(String maLichHoc, LopHoc lopHoc, int thu, TietHoc tietBatDau, TietHoc tietKetThuc, PhongHoc phongHoc) {
        this.maLichHoc = Objects.requireNonNull(maLichHoc, "maLichHoc");
        this.lopHoc = lopHoc;
        this.thu = thu;
        this.tietBatDau = tietBatDau;
        this.tietKetThuc = tietKetThuc;
        this.phongHoc = phongHoc;
    }

    public String getMaLichHoc() {
        return maLichHoc;
    }

    public LopHoc getLopHoc() {
        return lopHoc;
    }

    public void setLopHoc(LopHoc lopHoc) {
        this.lopHoc = lopHoc;
    }

    public int getThu() {
        return thu;
    }

    public void setThu(int thu) {
        this.thu = thu;
    }

    public TietHoc getTietBatDau() {
        return tietBatDau;
    }

    public void setTietBatDau(TietHoc tietBatDau) {
        this.tietBatDau = tietBatDau;
    }

    public TietHoc getTietKetThuc() {
        return tietKetThuc;
    }

    public void setTietKetThuc(TietHoc tietKetThuc) {
        this.tietKetThuc = tietKetThuc;
    }

    public PhongHoc getPhongHoc() {
        return phongHoc;
    }

    public void setPhongHoc(PhongHoc phongHoc) {
        this.phongHoc = phongHoc;
    }
    
    @Override
    public String toString() {
        return "LichHoc{" +
                "maLichHoc='" + maLichHoc + '\'' +
                ", thu=" + thu +
                ", tietBatDau=" + (tietBatDau != null ? tietBatDau.getTenTietHoc() : "null") +
                ", tietKetThuc=" + (tietKetThuc != null ? tietKetThuc.getTenTietHoc() : "null") +
                ", phongHoc=" + (phongHoc != null ? phongHoc.getTenPhongHoc() : "null") +
                '}';
    }
}
