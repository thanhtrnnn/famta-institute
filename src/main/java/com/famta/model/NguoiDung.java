package com.famta.model;

public abstract class NguoiDung {
    protected String ho;
    protected String tenLot;
    protected String ten;
    protected TaiKhoan taiKhoan;

    public NguoiDung(String ho, String tenLot, String ten) {
        this.ho = ho;
        this.tenLot = tenLot;
        this.ten = ten;
    }

    public String getHo() {
        return ho;
    }

    public void setHo(String ho) {
        this.ho = ho;
    }

    public String getTenLot() {
        return tenLot;
    }

    public void setTenLot(String tenLot) {
        this.tenLot = tenLot;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public String getHoTenDayDu() {
        return ho + " " + (tenLot != null && !tenLot.isBlank() ? tenLot + " " : "") + ten;
    }
}
