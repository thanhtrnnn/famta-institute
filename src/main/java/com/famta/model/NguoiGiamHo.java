package com.famta.model;

public class NguoiGiamHo {
    private String maNguoiGiamHo;
    private String ho;
    private String tenLot;
    private String ten;
    private String diaChiEmail;    private String sdt;

    // Constructors
    public NguoiGiamHo() {}
    public NguoiGiamHo(String maNguoiGiamHo, String ho, String tenLot, String ten, String diaChiEmail, String sdt) { this.maNguoiGiamHo = maNguoiGiamHo; this.ho = ho; this.tenLot = tenLot; this.ten = ten; this.diaChiEmail = diaChiEmail; this.sdt = sdt; }

    // Getters
    public String getMaNguoiGiamHo() { return maNguoiGiamHo; }
    public String getHo() { return ho; }
    public String getTenLot() { return tenLot; }
    public String getTen() { return ten; }
    public String getDiaChiEmail() { return diaChiEmail; }
    public String getSdt() { return sdt; }

    // Setters
    public void setMaNguoiGiamHo(String maNguoiGiamHo) { this.maNguoiGiamHo = maNguoiGiamHo; }
    public void setHo(String ho) { this.ho = ho; }
    public void setTenLot(String tenLot) { this.tenLot = tenLot; }
    public void setTen(String ten) { this.ten = ten; }
    public void setDiaChiEmail(String diaChiEmail) { this.diaChiEmail = diaChiEmail; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    // Utility methods
    public String getHoTenDayDu() { return ho + " " + (tenLot != null ? tenLot + " " : "") + ten; }

    public void guiThongBao(String noiDung) {
    }
}
