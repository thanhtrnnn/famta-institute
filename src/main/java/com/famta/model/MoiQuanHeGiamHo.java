package com.famta.model;

import java.util.Objects;

public class MoiQuanHeGiamHo {
    private final HocSinh hocSinh;
    private final NguoiGiamHo nguoiGiamHo;
    private final LoaiNguoiGiamHo loaiNguoiGiamHo;

    public MoiQuanHeGiamHo(HocSinh hocSinh, NguoiGiamHo nguoiGiamHo, LoaiNguoiGiamHo loaiNguoiGiamHo) {
        this.hocSinh = Objects.requireNonNull(hocSinh, "hocSinh");
        this.nguoiGiamHo = Objects.requireNonNull(nguoiGiamHo, "nguoiGiamHo");
        this.loaiNguoiGiamHo = Objects.requireNonNull(loaiNguoiGiamHo, "loaiNguoiGiamHo");
    }

    public HocSinh getHocSinh() {
        return hocSinh;
    }

    public NguoiGiamHo getNguoiGiamHo() {
        return nguoiGiamHo;
    }

    public LoaiNguoiGiamHo getLoaiNguoiGiamHo() {
        return loaiNguoiGiamHo;
    }
}
