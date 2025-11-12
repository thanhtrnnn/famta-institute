package com.famta.service.dto;

import java.util.Objects;

/**
 * Lightweight projection that links a student with the lớp học they are
 * currently enrolled in.
 */
public record StudentClassEnrollment(
    String maHocSinh,
    String tenHocSinh,
    String maLopHoc,
    String tenLopHoc
) {
    public StudentClassEnrollment {
        Objects.requireNonNull(maHocSinh, "maHocSinh");
        Objects.requireNonNull(tenHocSinh, "tenHocSinh");
    }

    public String lopDisplay() {
        return (tenLopHoc == null || tenLopHoc.isBlank()) ? "Chưa xếp lớp" : tenLopHoc;
    }
}
