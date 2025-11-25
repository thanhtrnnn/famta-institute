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
        WHERE hs.TenDangNhap = ?
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
        WHERE gv.TenDangNhap = ?
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
        WHERE ngh.TenDangNhap = ?
        ORDER BY lh.Thu, tb.ThoiGianBatDau
    """;

    public List<TimetableEntry> fetchStudentTimetable(String studentId) {
        return fetchTimetable(STUDENT_TIMETABLE_SQL, studentId);
    }

    public List<TimetableEntry> fetchTeacherTimetable(String teacherId) {
        return fetchTimetable(TEACHER_TIMETABLE_SQL, teacherId);
    }

    public List<TimetableEntry> fetchGuardianTimetable(String guardianId) {
        return fetchTimetable(GUARDIAN_TIMETABLE_SQL, guardianId);
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
