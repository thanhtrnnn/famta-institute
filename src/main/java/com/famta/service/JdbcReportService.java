package com.famta.service;

import com.famta.database.DatabaseManager;
import com.famta.service.dto.ReportSummary;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides aggregate metrics directly from SQL Server.
 */
public class JdbcReportService {

    private static final String COUNT_STUDENTS = "SELECT COUNT(*) AS Total FROM HOCSINH";
    private static final String COUNT_TEACHERS = "SELECT COUNT(*) AS Total FROM GIAOVIEN";
    private static final String COUNT_CLASSES = "SELECT COUNT(*) AS Total FROM LOPHOC";
    private static final String AVG_SCORE = "SELECT AVG(CAST(DiemSo AS DECIMAL(5,2))) AS AvgScore FROM HOCSINH_LOPHOC";

    private static final String TOP_CLASSES = """
        SELECT TOP 3 l.TenLopHoc, COALESCE(AVG(CAST(hsl.DiemSo AS DECIMAL(5,2))), 0) AS AvgScore
        FROM LOPHOC l
        LEFT JOIN HOCSINH_LOPHOC hsl ON hsl.MaLopHoc = l.MaLopHoc
        GROUP BY l.TenLopHoc
        ORDER BY AvgScore DESC
        """;

    public ReportSummary loadOverview(LocalDate from, LocalDate to) {
        Connection connection = DatabaseManager.getInstance().getConnection();
        int students = runScalarInt(connection, COUNT_STUDENTS);
        int teachers = runScalarInt(connection, COUNT_TEACHERS);
        int classes = runScalarInt(connection, COUNT_CLASSES);
        double averageScore = runScalarDouble(connection, AVG_SCORE);
        List<String> highlights = fetchTopClasses(connection);
        return new ReportSummary(students, teachers, classes, averageScore, highlights);
    }

    private int runScalarInt(Connection connection, String sql) {
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể đọc dữ liệu thống kê", ex);
        }
    }

    private double runScalarDouble(Connection connection, String sql) {
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                double value = rs.getDouble(1);
                return rs.wasNull() ? 0d : value;
            }
            return 0d;
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể đọc dữ liệu thống kê", ex);
        }
    }

    private List<String> fetchTopClasses(Connection connection) {
        List<String> highlights = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(TOP_CLASSES); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String tenLop = rs.getString(1);
                double avgScore = rs.getDouble(2);
                if (rs.wasNull()) {
                    avgScore = 0d;
                }
                String label = String.format("%s: %.2f điểm", tenLop != null ? tenLop.trim() : "Lớp chưa đặt tên", avgScore);
                highlights.add(label);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể đọc danh sách lớp", ex);
        }
        return highlights;
    }
}
