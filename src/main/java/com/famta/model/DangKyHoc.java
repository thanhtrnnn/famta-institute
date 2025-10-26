package com.famta.model;

import java.util.Objects;

public class DangKyHoc {
    private final HocSinh hocSinh;
    private final LopHoc lopHoc;
    private final Khoi khoi;
    private float diemSo;

    public DangKyHoc(HocSinh hocSinh, LopHoc lopHoc, Khoi khoi) {
        this.hocSinh = Objects.requireNonNull(hocSinh, "hocSinh");
        this.lopHoc = Objects.requireNonNull(lopHoc, "lopHoc");
        this.khoi = Objects.requireNonNull(khoi, "khoi");
        hocSinh.addDangKyInternal(this);
        lopHoc.themDangKyNoCascade(this);
    }

    public HocSinh getHocSinh() {
        return hocSinh;
    }

    public LopHoc getLopHoc() {
        return lopHoc;
    }

    public Khoi getKhoi() {
        return khoi;
    }

    public float getDiemSo() {
        return diemSo;
    }

    public void capNhatDiem(float diemMoi) {
        this.diemSo = diemMoi;
    }

    void huyDangKy() {
        lopHoc.xoaDangKyNoCascade(this);
        hocSinh.removeDangKyInternal(this);
    }
}
