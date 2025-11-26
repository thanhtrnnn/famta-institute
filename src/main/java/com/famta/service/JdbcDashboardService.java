package com.famta.service;

import com.famta.database.DatabaseManager;
import com.famta.service.dto.DashboardSummary;
import com.famta.service.dto.ScheduleItem;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides aggregate metrics and small helper lists for the dashboard view.
 */
public class JdbcDashboardService {

    private static final String COUNT_STUDENTS = "SELECT COUNT(*) FROM HOCSINH";
    private static final String COUNT_TEACHERS = "SELECT COUNT(*) FROM GIAOVIEN";
    private static final String COUNT_CLASSES = "SELECT COUNT(*) FROM LOPHOC";

    private static final String SCHEDULE_SQL = """
        SELECT TOP 5
            COALESCE(l.TenLopHoc, 'Lớp chưa đặt tên') AS TenLop,
            COALESCE(gv.Ho, '') AS Ho,
            COALESCE(gv.TenLot, '') AS TenLot,
            COALESCE(gv.Ten, '') AS Ten,
            COALESCE(tb.TenTietHoc, 'Tiết ?') AS BatDau,
            COALESCE(tk.TenTietHoc, 'Tiết ?') AS KetThuc,
            COALESCE(ph.TenPhongHoc, 'Chưa xếp phòng') AS Phong
        FROM LOPHOC l
        LEFT JOIN GIAOVIEN gv ON gv.MaGiaoVien = l.MaGiaoVien
        LEFT JOIN TIETHOC tb ON tb.MaTietHoc = l.TietHocBatDau
        LEFT JOIN TIETHOC tk ON tk.MaTietHoc = l.TietHocKetThuc
        LEFT JOIN PHONGHOC ph ON ph.MaPhongHoc = l.MaPhongHoc
        ORDER BY tb.MaTietHoc, l.TenLopHoc
        """;

    public DashboardSummary loadDashboard() {
        Connection connection = DatabaseManager.getInstance().getConnection();
        int students = runScalar(connection, COUNT_STUDENTS);
        int teachers = runScalar(connection, COUNT_TEACHERS);
        int classes = runScalar(connection, COUNT_CLASSES);
        List<ScheduleItem> schedule = loadSchedule(connection);
        List<String> announcements = buildAnnouncements(students, classes, schedule.size());
        return new DashboardSummary(students, teachers, classes, announcements, schedule, LocalDateTime.now());
    }

    private int runScalar(Connection connection, String sql) {
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể đọc dữ liệu tổng quan", ex);
        }
    }

    private List<ScheduleItem> loadSchedule(Connection connection) {
        List<ScheduleItem> items = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SCHEDULE_SQL); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String className = rs.getString("TenLop").trim();
                String teacher = formatTeacherName(rs.getString("Ho"), rs.getString("TenLot"), rs.getString("Ten"));
                String time = rs.getString("BatDau") + " - " + rs.getString("KetThuc");
                String room = rs.getString("Phong");
                items.add(new ScheduleItem(className, teacher, time, room));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải lịch giảng dạy", ex);
        }
        return items;
    }

    private List<String> buildAnnouncements(int students, int classes, int scheduleCount) {
        List<String> messages = new ArrayList<>();
        messages.add("Hiện có " + students + " học sinh đang theo học.");
        messages.add("" + classes + " lớp được xếp lịch trong học kỳ hiện tại.");
        messages.add(scheduleCount + " phiên học đang diễn ra trong ngày.");
        return messages;
    }

    private String formatTeacherName(String ho, String tenLot, String ten) {
        StringBuilder builder = new StringBuilder();
        appendPart(builder, ho);
        appendPart(builder, tenLot);
        appendPart(builder, ten);
        String value = builder.toString().trim();
        return value.isBlank() ? "Chưa phân công" : value;
    }

    private void appendPart(StringBuilder builder, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (builder.length() > 0) {
            builder.append(' ');
        }
        builder.append(value.trim());
    }
}
