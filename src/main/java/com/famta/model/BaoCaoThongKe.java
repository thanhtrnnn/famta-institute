package com.famta.model;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BaoCaoThongKe {
    public String taoBaoCaoDanhSachHocVien(List<LopHoc> danhSachLop) {
        Objects.requireNonNull(danhSachLop, "danhSachLop");
        StringBuilder builder = new StringBuilder("Bao cao danh sach hoc vien\n");
        for (LopHoc lopHoc : danhSachLop.stream().sorted(Comparator.comparing(LopHoc::getTenLopHoc))
                .collect(Collectors.toList())) {
            builder.append("- Lop: ").append(lopHoc.getTenLopHoc()).append(" (Ma: ")
                    .append(lopHoc.getMaLopHoc()).append(")\n");
            lopHoc.layDanhSachHocSinh().forEach(hocSinh -> builder.append("   * ")
                    .append(hocSinh.getHoTenDayDu()).append(" - Ma: ")
                    .append(hocSinh.getMaHocSinh()).append('\n'));
        }
        return builder.toString();
    }

    public String xuatBangDiemLop(LopHoc lopHoc) {
        Objects.requireNonNull(lopHoc, "lopHoc");
        StringBuilder builder = new StringBuilder("Bang diem lop ")
                .append(lopHoc.getTenLopHoc()).append('\n');
        for (DangKyHoc dangKyHoc : lopHoc.getDanhSachDangKy()) {
            builder.append("- ").append(dangKyHoc.getHocSinh().getHoTenDayDu())
                    .append(" (" + dangKyHoc.getHocSinh().getMaHocSinh() + ")")
                    .append(": ").append(dangKyHoc.getDiemSo()).append('\n');
        }
        return builder.toString();
    }

    public String thongKeHocVienMoi(List<HocSinh> danhSachHocSinh, LocalDateTime tuNgay, LocalDateTime denNgay) {
        Objects.requireNonNull(danhSachHocSinh, "danhSachHocSinh");
        Objects.requireNonNull(tuNgay, "tuNgay");
        Objects.requireNonNull(denNgay, "denNgay");
        return danhSachHocSinh.stream()
                .filter(hocSinh -> hocSinh.getNgayNhapHoc() != null
                        && !hocSinh.getNgayNhapHoc().atStartOfDay().isBefore(tuNgay)
                        && !hocSinh.getNgayNhapHoc().atStartOfDay().isAfter(denNgay))
                .map(hocSinh -> hocSinh.getHoTenDayDu() + " (" + hocSinh.getMaHocSinh() + ")")
                .collect(Collectors.joining("\n", "Hoc vien moi:\n", ""));
    }

    public String thongKeHocVienXuatSac(List<DangKyHoc> danhSachDangKy, float dieuKienDiem) {
        Objects.requireNonNull(danhSachDangKy, "danhSachDangKy");
        return danhSachDangKy.stream()
                .filter(dk -> dk.getDiemSo() >= dieuKienDiem)
                .sorted(Comparator.comparing(dk -> dk.getHocSinh().getHoTenDayDu()))
                .map(dk -> dk.getHocSinh().getHoTenDayDu() + " - " + dk.getDiemSo())
                .collect(Collectors.joining("\n", "Hoc vien xuat sac:\n", ""));
    }
}
