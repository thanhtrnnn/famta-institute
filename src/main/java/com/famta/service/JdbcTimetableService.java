package com.famta.service;

import com.famta.database.DatabaseManager;
import com.famta.service.dto.TimetableEntry;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcTimetableService {
    
    private static final String STUDENT_TIMETABLE_SQL = """
        SELECT 
            lh.MaLichHoc, lh.Thu, 
            tb.TenTietHoc as TenTietBatDau, tb.ThoiGianBatDau,
            tk.TenTietHoc as TenTietKetThuc, tk.ThoiGianKetThuc,
            ph.TenPhongHoc,
            l.TenLopHoc,
            mh.TenMonHoc
        FROM LICH_HOC lh
        JOIN LOPHOC l ON l.MaLopHoc = lh.MaLopHoc
        JOIN HOCSINH_LOPHOC hsl ON hsl.MaLopHoc = l.MaLopHoc
        JOIN HOCSINH hs ON hs.MaHocSinh = hsl.MaHocSinh
        LEFT JOIN TIETHOC tb ON tb.MaTietHoc = lh.TietBatDau
        LEFT JOIN TIETHOC tk ON tk.MaTietHoc = lh.TietKetThuc
        LEFT JOIN PHONGHOC ph ON ph.MaPhongHoc = lh.MaPhongHoc
        LEFT JOIN MONHOC mh ON mh.MaMonHoc = l.MaMonHoc
        WHERE hs.MaHocSinh = ?
        ORDER BY lh.Thu, tb.ThoiGianBatDau
    """;

    private static final String TEACHER_TIMETABLE_SQL = """
        SELECT 
            lh.MaLichHoc, lh.Thu, 
            tb.TenTietHoc as TenTietBatDau, tb.ThoiGianBatDau,
            tk.TenTietHoc as TenTietKetThuc, tk.ThoiGianKetThuc,
            ph.TenPhongHoc,
            l.TenLopHoc,
            mh.TenMonHoc
        FROM LICH_HOC lh
        JOIN LOPHOC l ON l.MaLopHoc = lh.MaLopHoc
        JOIN GIAOVIEN gv ON gv.MaGiaoVien = l.MaGiaoVien
        LEFT JOIN TIETHOC tb ON tb.MaTietHoc = lh.TietBatDau
        LEFT JOIN TIETHOC tk ON tk.MaTietHoc = lh.TietKetThuc
        LEFT JOIN PHONGHOC ph ON ph.MaPhongHoc = lh.MaPhongHoc
        LEFT JOIN MONHOC mh ON mh.MaMonHoc = l.MaMonHoc
        WHERE gv.MaGiaoVien = ?
        ORDER BY lh.Thu, tb.ThoiGianBatDau
    """;

    private static final String GUARDIAN_TIMETABLE_SQL = """
        SELECT 
            lh.MaLichHoc, lh.Thu, 
            tb.TenTietHoc as TenTietBatDau, tb.ThoiGianBatDau,
            tk.TenTietHoc as TenTietKetThuc, tk.ThoiGianKetThuc,
            ph.TenPhongHoc,
            l.TenLopHoc,
            mh.TenMonHoc,
            CONCAT(hs.Ho, ' ', hs.Ten) as TenHocSinh
        FROM LICH_HOC lh
        JOIN LOPHOC l ON l.MaLopHoc = lh.MaLopHoc
        JOIN HOCSINH_LOPHOC hsl ON hsl.MaLopHoc = l.MaLopHoc
        JOIN HOCSINH hs ON hs.MaHocSinh = hsl.MaHocSinh
        JOIN HOCSINH_NGUOIGIAMHO nghs ON nghs.MaHocSinh = hs.MaHocSinh
        JOIN NGUOIGIAMHO ngh ON ngh.MaNguoiGiamHo = nghs.MaNguoiGiamHo
        LEFT JOIN TIETHOC tb ON tb.MaTietHoc = lh.TietBatDau
        LEFT JOIN TIETHOC tk ON tk.MaTietHoc = lh.TietKetThuc
        LEFT JOIN PHONGHOC ph ON ph.MaPhongHoc = lh.MaPhongHoc
        LEFT JOIN MONHOC mh ON mh.MaMonHoc = l.MaMonHoc
        WHERE ngh.MaNguoiGiamHo = ?
        ORDER BY lh.Thu, tb.ThoiGianBatDau
    """;

    public List<TimetableEntry> fetchStudentTimetable(String username) {
        return fetchTimetable(STUDENT_TIMETABLE_SQL, resolveStudentId(username));
    }

    public List<TimetableEntry> fetchTeacherTimetable(String username) {
        return fetchTimetable(TEACHER_TIMETABLE_SQL, resolveTeacherId(username));
    }

    public List<TimetableEntry> fetchGuardianTimetable(String username) {
        return fetchTimetable(GUARDIAN_TIMETABLE_SQL, resolveGuardianId(username));
    }

    private String resolveStudentId(String username) {
        if ("student".equals(username)) return "HS00000001";
        return resolveIdByName(username, "HOCSINH", "MaHocSinh", "hs.");
    }

    private String resolveTeacherId(String username) {
        if ("teacher".equals(username)) return "GV00000001";
        return resolveIdByName(username, "GIAOVIEN", "MaGiaoVien", "gv.");
    }

    private String resolveGuardianId(String username) {
        if ("guardian".equals(username)) return "GH00000001";
        return resolveIdByName(username, "NGUOIGIAMHO", "MaNguoiGiamHo", "gh.");
    }

    private String resolveIdByName(String username, String tableName, String idColumn, String prefix) {
        if (!username.startsWith(prefix)) return username;
        
        String sql = "SELECT " + idColumn + ", Ho, TenLot, Ten FROM " + tableName;
        Connection connection = DatabaseManager.getInstance().getConnection();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String id = rs.getString(idColumn);
                String ho = rs.getString("Ho");
                String tenLot = rs.getString("TenLot");
                String ten = rs.getString("Ten");
                
                if (matchesUsername(username, prefix, ho, tenLot, ten)) {
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }

    private boolean matchesUsername(String username, String prefix, String ho, String tenLot, String ten) {
        String normalizedTen = unaccent(ten).toLowerCase();
        String normalizedHo = unaccent(ho).toLowerCase();
        String normalizedTenLot = unaccent(tenLot).toLowerCase();
        
        String initialsHo = getInitials(normalizedHo);
        String initialsTenLot = getInitials(normalizedTenLot);
        
        // Pattern 1: prefix + ten + initials(ho) + initials(tenLot) (e.g. hs.tungnv)
        String candidate1 = prefix + normalizedTen + initialsHo + initialsTenLot;
        
        // Pattern 2: prefix + ten + initials(tenLot) + initials(ho) (e.g. hs.anvn)
        String candidate2 = prefix + normalizedTen + initialsTenLot + initialsHo;
        
        return username.equals(candidate1) || username.equals(candidate2);
    }
    
    private String getInitials(String text) {
        if (text == null || text.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String word : text.split("\\s+")) {
            if (!word.isEmpty()) {
                sb.append(word.charAt(0));
            }
        }
        return sb.toString();
    }

    private String unaccent(String s) {
        if (s == null) return "";
        String temp = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    }

    private List<TimetableEntry> fetchTimetable(String sql, String id) {
        List<TimetableEntry> entries = new ArrayList<>();
        Connection connection = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    entries.add(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải thời khóa biểu", ex);
        }
        return entries;
    }

    private TimetableEntry mapRow(ResultSet rs) throws SQLException {
        String studentName = null;
        try {
            studentName = rs.getString("TenHocSinh");
        } catch (SQLException ignored) {
            // Column might not exist for other queries
        }
        
        return new TimetableEntry(
            rs.getString("MaLichHoc"),
            rs.getInt("Thu"),
            rs.getString("TenTietBatDau"),
            rs.getString("TenTietKetThuc"),
            rs.getString("ThoiGianBatDau"),
            rs.getString("ThoiGianKetThuc"),
            rs.getString("TenPhongHoc"),
            rs.getString("TenLopHoc"),
            rs.getString("TenMonHoc"),
            studentName
        );
    }
}
