package com.famta.model;

import java.util.Objects;

public class MonHoc {
    private final String maMonHoc;
    private String tenMonHoc;
    private Khoa khoa;

    public MonHoc(String maMonHoc, String tenMonHoc, Khoa khoa) {
        this.maMonHoc = Objects.requireNonNull(maMonHoc, "maMonHoc");
        this.tenMonHoc = tenMonHoc;
        setKhoa(khoa);
    }

    public String getMaMonHoc() {
        return maMonHoc;
    }

    public String getTenMonHoc() {
        return tenMonHoc;
    }

    public void setTenMonHoc(String tenMonHoc) {
        this.tenMonHoc = tenMonHoc;
    }

    public Khoa getKhoa() {
        return khoa;
    }

    public void setKhoa(Khoa khoa) {
        if (this.khoa != null) {
            this.khoa.xoaMonHocInternal(this);
        }
        this.khoa = khoa;
        if (khoa != null) {
            khoa.themMonHocInternal(this);
        }
    }
}
