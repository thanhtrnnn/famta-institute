package com.famta.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class NguoiGiamHo {
    private final String maNguoiGiamHo;
    private String ho;
    private String tenLot;
    private String ten;
    private String diaChiEmail;
    private String sdt;
    private TaiKhoan taiKhoan;
    private final List<MoiQuanHeGiamHo> quanHeVoiHocSinh = new ArrayList<>();

    public NguoiGiamHo(String maNguoiGiamHo, String ho, String tenLot, String ten, String diaChiEmail, String sdt) {
        this.maNguoiGiamHo = Objects.requireNonNull(maNguoiGiamHo, "maNguoiGiamHo");
        this.ho = ho;
        this.tenLot = tenLot;
        this.ten = ten;
        this.diaChiEmail = diaChiEmail;
        this.sdt = sdt;
    }

    public String getMaNguoiGiamHo() {
        return maNguoiGiamHo;
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

    public String getDiaChiEmail() {
        return diaChiEmail;
    }

    public void setDiaChiEmail(String diaChiEmail) {
        this.diaChiEmail = diaChiEmail;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public List<MoiQuanHeGiamHo> getQuanHeVoiHocSinh() {
        return Collections.unmodifiableList(quanHeVoiHocSinh);
    }

    public String getHoTenDayDu() {
        return ho + " " + (tenLot != null && !tenLot.isBlank() ? tenLot + " " : "") + ten;
    }

    public void guiThongBao(String noiDung) {
        // In a real system this would push a notification
        System.out.printf("Gui thong bao toi %s: %s%n", getHoTenDayDu(), noiDung);
    }

    public void guiNhacNhoHocPhi() {
        guiThongBao("Vui long hoan tat hoc phi cho hoc vien dang duoc ban giam ho.");
    }

    void addMoiQuanHeInternal(MoiQuanHeGiamHo quanHe) {
        if (!quanHeVoiHocSinh.contains(quanHe)) {
            quanHeVoiHocSinh.add(quanHe);
        }
    }
}
