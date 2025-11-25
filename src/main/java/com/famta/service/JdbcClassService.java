package com.famta.service;

import com.famta.database.DatabaseManager;
import com.famta.service.dto.ClassSummary;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * JDBC backed query helper for class (lớp học) dashboards.
 */
public class JdbcClassService {

    private static final String CLASS_OVERVIEW_SQL = """
        SELECT
            l.MaLopHoc,
            l.TenLopHoc,
            COALESCE(gv.Ho, '') AS Ho,
            COALESCE(gv.TenLot, '') AS TenLot,
            COALESCE(gv.Ten, '') AS Ten,
            mh.TenMonHoc,
            k.TenKhoi,
            nh.TenNamHoc,
            hk.ThuTuKy,
            tb.TenTietHoc AS TietBatDau,
            tk.TenTietHoc AS TietKetThuc,
            ph.TenPhongHoc,
            COUNT(DISTINCT hsl.MaHocSinh) AS SiSo
        FROM LOPHOC l
        LEFT JOIN GIAOVIEN gv ON gv.MaGiaoVien = l.MaGiaoVien
        LEFT JOIN MONHOC mh ON mh.MaMonHoc = l.MaMonHoc
        LEFT JOIN HOCKY hk ON hk.MaHocKy = l.MaHocKy
        LEFT JOIN NAMHOC nh ON nh.MaNamHoc = hk.MaNamHoc
        LEFT JOIN HOCSINH_NAMHOC_KHOI_LOPHOC hnk ON hnk.MaLopHoc = l.MaLopHoc
        LEFT JOIN KHOI k ON k.MaKhoi = hnk.MaKhoi
        LEFT JOIN TIETHOC tb ON tb.MaTietHoc = l.TietHocBatDau
        LEFT JOIN TIETHOC tk ON tk.MaTietHoc = l.TietHocKetThuc
        LEFT JOIN PHONGHOC ph ON ph.MaPhongHoc = l.MaPhongHoc
        LEFT JOIN HOCSINH_LOPHOC hsl ON hsl.MaLopHoc = l.MaLopHoc
        GROUP BY l.MaLopHoc, l.TenLopHoc, gv.Ho, gv.TenLot, gv.Ten,
                 mh.TenMonHoc, k.TenKhoi, nh.TenNamHoc, hk.ThuTuKy,
                 tb.TenTietHoc, tk.TenTietHoc, ph.TenPhongHoc
        ORDER BY l.MaLopHoc
        """;

    private static final String CLASS_BY_SUBJECT_SQL = """
        SELECT
            l.MaLopHoc,
            l.TenLopHoc,
            COALESCE(gv.Ho, '') AS Ho,
            COALESCE(gv.TenLot, '') AS TenLot,
            COALESCE(gv.Ten, '') AS Ten,
            mh.TenMonHoc,
            k.TenKhoi,
            nh.TenNamHoc,
            hk.ThuTuKy,
            tb.TenTietHoc AS TietBatDau,
            tk.TenTietHoc AS TietKetThuc,
            ph.TenPhongHoc,
            COUNT(DISTINCT hsl.MaHocSinh) AS SiSo
        FROM LOPHOC l
        LEFT JOIN GIAOVIEN gv ON gv.MaGiaoVien = l.MaGiaoVien
        LEFT JOIN MONHOC mh ON mh.MaMonHoc = l.MaMonHoc
        LEFT JOIN HOCKY hk ON hk.MaHocKy = l.MaHocKy
        LEFT JOIN NAMHOC nh ON nh.MaNamHoc = hk.MaNamHoc
        LEFT JOIN HOCSINH_NAMHOC_KHOI_LOPHOC hnk ON hnk.MaLopHoc = l.MaLopHoc
        LEFT JOIN KHOI k ON k.MaKhoi = hnk.MaKhoi
        LEFT JOIN TIETHOC tb ON tb.MaTietHoc = l.TietHocBatDau
        LEFT JOIN TIETHOC tk ON tk.MaTietHoc = l.TietHocKetThuc
        LEFT JOIN PHONGHOC ph ON ph.MaPhongHoc = l.MaPhongHoc
        LEFT JOIN HOCSINH_LOPHOC hsl ON hsl.MaLopHoc = l.MaLopHoc
        WHERE l.MaMonHoc = ?
        GROUP BY l.MaLopHoc, l.TenLopHoc, gv.Ho, gv.TenLot, gv.Ten,
                 mh.TenMonHoc, k.TenKhoi, nh.TenNamHoc, hk.ThuTuKy,
                 tb.TenTietHoc, tk.TenTietHoc, ph.TenPhongHoc
        ORDER BY l.MaLopHoc
        """;

    public List<ClassSummary> fetchClassSummaries() {
        List<ClassSummary> classes = new ArrayList<>();
        Connection connection = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(CLASS_OVERVIEW_SQL)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    classes.add(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải danh sách lớp học", ex);
        }
        return classes;
    }

    public List<ClassSummary> fetchClassesBySubject(String maMonHoc) {
        List<ClassSummary> classes = new ArrayList<>();
        Connection connection = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(CLASS_BY_SUBJECT_SQL)) {
            ps.setString(1, maMonHoc);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    classes.add(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải danh sách lớp học theo môn", ex);
        }
        return classes;
    }

    private ClassSummary mapRow(ResultSet rs) throws SQLException {
        String maLop = safeTrim(rs.getString("MaLopHoc"));
        String tenLop = safeTrim(rs.getString("TenLopHoc"));
        String mon = safeTrim(rs.getString("TenMonHoc"));
        String khoi = safeTrim(rs.getString("TenKhoi"));
        String namHoc = safeTrim(rs.getString("TenNamHoc"));
        int hocKy = rs.getInt("ThuTuKy");
        String tietBatDau = safeTrim(rs.getString("TietBatDau"));
        String tietKetThuc = safeTrim(rs.getString("TietKetThuc"));
        String phong = safeTrim(rs.getString("TenPhongHoc"));
        int siSo = rs.getInt("SiSo");
        if (rs.wasNull()) {
            siSo = 0;
        }
        String giaoVien = buildTeacherName(
            safeTrim(rs.getString("Ho")),
            safeTrim(rs.getString("TenLot")),
            safeTrim(rs.getString("Ten"))
        );
        return new ClassSummary(maLop, tenLop, giaoVien, khoi, namHoc, hocKy, mon, tietBatDau, tietKetThuc, phong, siSo);
    }

    public void deleteClass(String maLopHoc) throws SQLException {
        String sql = "DELETE FROM LOPHOC WHERE MaLopHoc = ?";
        Connection connection = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, maLopHoc);
            ps.executeUpdate();
        }
    }

    public void createClass(com.famta.model.LopHoc lopHoc) throws SQLException {
        String sql = "INSERT INTO LOPHOC (MaLopHoc, TenLopHoc, MaMonHoc, MaGiaoVien, MaHocKy, MaPhongHoc, TietHocBatDau, TietHocKetThuc) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection connection = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, lopHoc.getMaLopHoc());
            ps.setString(2, lopHoc.getTenLopHoc());
            ps.setString(3, lopHoc.getMonHoc().getMaMonHoc());
            ps.setString(4, lopHoc.getGiaoVienPhuTrach().getMaGiaoVien());
            ps.setString(5, lopHoc.getHocKy().getMaHocKy());
            ps.setString(6, lopHoc.getPhongHoc().getMaPhongHoc());
            ps.setString(7, lopHoc.getTietHocBatDau().getMaTietHoc());
            ps.setString(8, lopHoc.getTietHocKetThuc().getMaTietHoc());
            ps.executeUpdate();
        }
    }

    public void updateClass(com.famta.model.LopHoc lopHoc) throws SQLException {
        String sql = "UPDATE LOPHOC SET TenLopHoc=?, MaMonHoc=?, MaGiaoVien=?, MaHocKy=?, MaPhongHoc=?, TietHocBatDau=?, TietHocKetThuc=? WHERE MaLopHoc=?";
        Connection connection = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, lopHoc.getTenLopHoc());
            ps.setString(2, lopHoc.getMonHoc().getMaMonHoc());
            ps.setString(3, lopHoc.getGiaoVienPhuTrach().getMaGiaoVien());
            ps.setString(4, lopHoc.getHocKy().getMaHocKy());
            ps.setString(5, lopHoc.getPhongHoc().getMaPhongHoc());
            ps.setString(6, lopHoc.getTietHocBatDau().getMaTietHoc());
            ps.setString(7, lopHoc.getTietHocKetThuc().getMaTietHoc());
            ps.setString(8, lopHoc.getMaLopHoc());
            ps.executeUpdate();
        }
    }

    private static String buildTeacherName(String ho, String tenLot, String ten) {
        StringBuilder builder = new StringBuilder();
        if (ho != null && !ho.isBlank()) {
            builder.append(ho);
        }
        if (tenLot != null && !tenLot.isBlank()) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(tenLot);
        }
        if (ten != null && !ten.isBlank()) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(ten);
        }
        String fullName = builder.toString().trim();
        return fullName.isBlank() ? null : capitalize(fullName);
    }

    private static String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        String lower = value.toLowerCase(Locale.getDefault());
        char[] chars = lower.toCharArray();
        boolean upperNext = true;
        for (int i = 0; i < chars.length; i++) {
            if (Character.isLetter(chars[i]) && upperNext) {
                chars[i] = Character.toUpperCase(chars[i]);
                upperNext = false;
            } else if (Character.isWhitespace(chars[i])) {
                upperNext = true;
            }
        }
        return new String(chars);
    }

    private static String safeTrim(String value) {
        return value == null ? null : value.trim();
    }
}
