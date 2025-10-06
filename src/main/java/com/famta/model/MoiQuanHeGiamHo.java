package com.famta.model;

public class MoiQuanHeGiamHo {    private NguoiGiamHo nguoiGiamHo;
    private LoaiNguoiGiamHo loaiNguoiGiamHo;

    // Constructors
    public MoiQuanHeGiamHo() {}
    public MoiQuanHeGiamHo(NguoiGiamHo nguoiGiamHo, LoaiNguoiGiamHo loaiNguoiGiamHo) { this.nguoiGiamHo = nguoiGiamHo; this.loaiNguoiGiamHo = loaiNguoiGiamHo; }

    // Getters
    public NguoiGiamHo getNguoiGiamHo() { return nguoiGiamHo; }
    public LoaiNguoiGiamHo getLoaiNguoiGiamHo() { return loaiNguoiGiamHo; }

    // Setters
    public void setNguoiGiamHo(NguoiGiamHo nguoiGiamHo) { this.nguoiGiamHo = nguoiGiamHo; }
    public void setLoaiNguoiGiamHo(LoaiNguoiGiamHo loaiNguoiGiamHo) { this.loaiNguoiGiamHo = loaiNguoiGiamHo; }
}
