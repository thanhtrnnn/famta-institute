package com.famta.service.dto;

import java.util.Objects;

/**
 * Projection used by the account management view to summarize lớp học
 * information that administrators typically audit.
 */
public record AdminClassOverview(
    String maLopHoc,
    String tenLopHoc,
    String monHoc,
    String giaoVien,
    String phongHoc
) {
    public AdminClassOverview {
        Objects.requireNonNull(maLopHoc, "maLopHoc");
        Objects.requireNonNull(tenLopHoc, "tenLopHoc");
    }

    public String monHocDisplay() {
        return isBlank(monHoc) ? "Chưa xác định" : monHoc;
    }

    public String giaoVienDisplay() {
        return isBlank(giaoVien) ? "Chưa phân công" : giaoVien;
    }

    public String phongHocDisplay() {
        return isBlank(phongHoc) ? "Đang cập nhật" : phongHoc;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
