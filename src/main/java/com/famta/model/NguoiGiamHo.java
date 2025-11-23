package com.famta.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class NguoiGiamHo extends NguoiDung {
    private final String maNguoiGiamHo;
    private String diaChiEmail;
    private String sdt;
    private final List<MoiQuanHeGiamHo> quanHeVoiHocSinh = new ArrayList<>();

    public NguoiGiamHo(String maNguoiGiamHo, String ho, String tenLot, String ten, String diaChiEmail, String sdt) {
        super(ho, tenLot, ten);
        this.maNguoiGiamHo = Objects.requireNonNull(maNguoiGiamHo, "maNguoiGiamHo");
        this.diaChiEmail = diaChiEmail;
        this.sdt = sdt;
    }

    public String getMaNguoiGiamHo() {
        return maNguoiGiamHo;
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

    public List<MoiQuanHeGiamHo> getQuanHeVoiHocSinh() {
        return Collections.unmodifiableList(quanHeVoiHocSinh);
    }

    public void guiThongBao(String noiDung) {
        // In a real system this would push a notification
        System.out.printf("Gui thong bao toi %s: %s%n", getHoTenDayDu(), noiDung);
    }

    public void addMoiQuanHe(MoiQuanHeGiamHo quanHe) {
        if (!quanHeVoiHocSinh.contains(quanHe)) {
            quanHeVoiHocSinh.add(quanHe);
        }
    }
}
