package com.famta.model;

import java.util.Objects;

public class DangKyHoc {
    private final HocSinh hocSinh;
    private final LopHoc lopHoc;
    private final Khoi khoi;
    private Float diemThuongXuyen;
    private Float diemGiuaKy;
    private Float diemCuoiKy;

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

    public Float getDiemThuongXuyen() {
        return diemThuongXuyen;
    }

    public Float getDiemGiuaKy() {
        return diemGiuaKy;
    }

    public Float getDiemCuoiKy() {
        return diemCuoiKy;
    }

    public void capNhatDiem(Float tx, Float gk, Float ck) {
        this.diemThuongXuyen = tx;
        this.diemGiuaKy = gk;
        this.diemCuoiKy = ck;
    }

    /**
     * Calculates the final score based on a standard formula (20-30-50).
     * Returns 0 if any score is missing.
     */
    public float getDiemTongKet() {
        if (diemThuongXuyen == null || diemGiuaKy == null || diemCuoiKy == null) {
            return 0.0f;
        }
        return (float) (diemThuongXuyen * 0.2 + diemGiuaKy * 0.3 + diemCuoiKy * 0.5);
    }

    void huyDangKy() {
        lopHoc.xoaDangKyNoCascade(this);
        hocSinh.removeDangKyInternal(this);
    }
}
