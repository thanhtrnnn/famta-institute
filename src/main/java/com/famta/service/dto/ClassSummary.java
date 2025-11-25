package com.famta.service.dto;

import java.util.Objects;

/**
 * Lightweight projection of a class (lớp học) for list views.
 */
public record ClassSummary(
    String maLopHoc,
    String tenLopHoc,
    String giaoVien,
    String khoi,
    String namHoc,
    int hocKy,
    String monHoc,
    String tietBatDau,
    String tietKetThuc,
    String phongHoc,
    int siSo
) {
    public ClassSummary {
        Objects.requireNonNull(maLopHoc, "maLopHoc");
        Objects.requireNonNull(tenLopHoc, "tenLopHoc");
    }

    public String lopVaMon() {
        return monHoc == null || monHoc.isBlank()
            ? tenLopHoc
            : tenLopHoc + " (" + monHoc + ")";
    }

    public String khoiDisplay() {
        return khoi == null || khoi.isBlank() ? "N/A" : khoi;
    }

    public String giaoVienDisplay() {
        return giaoVien == null || giaoVien.isBlank() ? "Chưa phân công" : giaoVien;
    }
}
