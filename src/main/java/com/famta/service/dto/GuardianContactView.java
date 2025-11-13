package com.famta.service.dto;

import java.util.Objects;

/**
 * View model that pairs a student with their guardian contact details and
 * relationship information, as surfaced in the account management UI.
 */
public record GuardianContactView(
    String maHocSinh,
    String tenHocSinh,
    String maNguoiGiamHo,
    String tenNguoiGiamHo,
    String loaiQuanHe,
    String email,
    String soDienThoai
) {
    public GuardianContactView {
        Objects.requireNonNull(maHocSinh, "maHocSinh");
        Objects.requireNonNull(tenHocSinh, "tenHocSinh");
        Objects.requireNonNull(maNguoiGiamHo, "maNguoiGiamHo");
    }

    public String quanHeDisplay() {
        return (loaiQuanHe == null || loaiQuanHe.isBlank()) ? "Chưa cập nhật" : loaiQuanHe;
    }
}
