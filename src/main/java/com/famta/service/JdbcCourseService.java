package com.famta.service;

import com.famta.database.DatabaseManager;
import com.famta.service.dto.CourseSummary;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Provides card-friendly projections of courses/classes for the courses screen.
 */
public class JdbcCourseService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());

    private static final String COURSE_SQL = """
        SELECT
            l.MaLopHoc,
            COALESCE(l.TenLopHoc, 'Lớp chưa đặt tên') AS TenLopHoc,
            COALESCE(mh.TenMonHoc, 'Chưa có môn học') AS TenMonHoc,
            COALESCE(k.TenKhoa, 'Chưa phân khoa') AS TenKhoa,
            COALESCE(kh.TenKhoi, 'Chưa xác định') AS TenKhoi,
            COALESCE(ph.TenPhongHoc, 'Chưa xếp phòng') AS PhongHoc,
            tb.ThoiGianBatDau AS BatDau,
            tk.ThoiGianKetThuc AS KetThuc,
            COALESCE(gv.Ho, '') AS Ho,
            COALESCE(gv.TenLot, '') AS TenLot,
            COALESCE(gv.Ten, '') AS Ten,
            COUNT(DISTINCT hsl.MaHocSinh) AS SoHocVien,
            DATEDIFF(MINUTE, tb.ThoiGianBatDau, tk.ThoiGianKetThuc) AS DurationMinutes
        FROM LOPHOC l
        LEFT JOIN MONHOC mh ON mh.MaMonHoc = l.MaMonHoc
        LEFT JOIN KHOA k ON k.MaKhoa = mh.MaKhoa
        LEFT JOIN HOCSINH_LOPHOC hsl ON hsl.MaLopHoc = l.MaLopHoc
        LEFT JOIN GIAOVIEN gv ON gv.MaGiaoVien = l.MaGiaoVien
        LEFT JOIN TIETHOC tb ON tb.MaTietHoc = l.TietHocBatDau
        LEFT JOIN TIETHOC tk ON tk.MaTietHoc = l.TietHocKetThuc
        LEFT JOIN PHONGHOC ph ON ph.MaPhongHoc = l.MaPhongHoc
        LEFT JOIN HOCSINH_NAMHOC_KHOI_LOPHOC hnk ON hnk.MaLopHoc = l.MaLopHoc
        LEFT JOIN KHOI kh ON kh.MaKhoi = hnk.MaKhoi
        GROUP BY
            l.MaLopHoc, l.TenLopHoc, mh.TenMonHoc, k.TenKhoa, kh.TenKhoi,
            ph.TenPhongHoc, tb.ThoiGianBatDau, tk.ThoiGianKetThuc,
            gv.Ho, gv.TenLot, gv.Ten
        ORDER BY l.TenLopHoc;""";

    public List<CourseSummary> fetchCourses() {
        Connection connection = DatabaseManager.getInstance().getConnection();
        List<CourseSummary> courses = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(COURSE_SQL); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                courses.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải danh sách khóa học", ex);
        }
        return courses;
    }

    private CourseSummary mapRow(ResultSet rs) throws SQLException {
        String code = safeTrim(rs.getString("MaLopHoc"));
        String className = safeTrim(rs.getString("TenLopHoc"));
        String subject = safeTrim(rs.getString("TenMonHoc"));
        String department = safeTrim(rs.getString("TenKhoa"));
        String level = safeTrim(rs.getString("TenKhoi"));
        String room = safeTrim(rs.getString("PhongHoc"));
        String teacher = buildTeacherName(safeTrim(rs.getString("Ho")), safeTrim(rs.getString("TenLot")), safeTrim(rs.getString("Ten")));
        int enrolled = rs.getInt("SoHocVien");
        if (rs.wasNull()) {
            enrolled = 0;
        }
        int duration = rs.getInt("DurationMinutes");
        if (rs.wasNull() || duration <= 0) {
            duration = 45;
        }
        String start = formatTime(rs.getTime("BatDau"));
        String end = formatTime(rs.getTime("KetThuc"));
        String timeRange = start + " - " + end;
        String status = enrolled > 0 ? "Đang giảng dạy" : "Đang mở đăng ký";
        String resolvedClassName = className != null && !className.isBlank() ? className : (subject != null ? subject : "Lớp chưa đặt tên");
        String resolvedTeacher = teacher.isBlank() ? "Chưa phân công" : teacher;
        return new CourseSummary(
            code,
            resolvedClassName,
            subject == null ? "Chưa có môn" : subject,
            department == null ? "Chưa phân khoa" : department,
            level == null ? "Chưa xác định" : level,
            resolvedTeacher,
            timeRange,
            room == null ? "Chưa xếp" : room,
            enrolled,
            duration,
            status
        );
    }

    private static String safeTrim(String value) {
        return value == null ? null : value.trim();
    }

    private static String buildTeacherName(String ho, String tenLot, String ten) {
        StringBuilder builder = new StringBuilder();
        append(builder, ho);
        append(builder, tenLot);
        append(builder, ten);
        return builder.toString().trim();
    }

    private static void append(StringBuilder builder, String part) {
        if (part == null || part.isBlank()) {
            return;
        }
        if (builder.length() > 0) {
            builder.append(' ');
        }
        builder.append(part);
    }

    private static String formatTime(Time value) {
        if (value == null) {
            return "--:--";
        }
        return TIME_FORMATTER.format(value.toLocalTime());
    }
}
