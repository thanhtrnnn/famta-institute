package com.famta.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Khoa {
    private final String maKhoa;
    private String tenKhoa;
    private final List<MonHoc> danhSachMonHoc = new ArrayList<>();

    public Khoa(String maKhoa, String tenKhoa) {
        this.maKhoa = Objects.requireNonNull(maKhoa, "maKhoa");
        this.tenKhoa = tenKhoa;
    }

    public String getMaKhoa() {
        return maKhoa;
    }

    public String getTenKhoa() {
        return tenKhoa;
    }

    public void setTenKhoa(String tenKhoa) {
        this.tenKhoa = tenKhoa;
    }

    public List<MonHoc> layDanhSachMonHoc() {
        return Collections.unmodifiableList(danhSachMonHoc);
    }

    public void themMonHoc(MonHoc monHoc) {
        if (monHoc != null && !danhSachMonHoc.contains(monHoc)) {
            danhSachMonHoc.add(monHoc);
            monHoc.setKhoa(this);
        }
    }

    void themMonHocInternal(MonHoc monHoc) {
        if (!danhSachMonHoc.contains(monHoc)) {
            danhSachMonHoc.add(monHoc);
        }
    }

    void xoaMonHocInternal(MonHoc monHoc) {
        danhSachMonHoc.remove(monHoc);
    }
}
