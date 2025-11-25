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
    private static final String AVG_SCORE = "SELECT AVG(CAST((ISNULL(DiemThuongXuyen, 0) * 0.2 + ISNULL(DiemGiuaKy, 0) * 0.3 + ISNULL(DiemCuoiKy, 0) * 0.5) AS DECIMAL(5,2))) AS AvgScore FROM HOCSINH_LOPHOC";

    private static final String TOP_CLASSES = """
        SELECT TOP 3 l.TenLopHoc, COALESCE(AVG(CAST((ISNULL(hsl.DiemThuongXuyen, 0) * 0.2 + ISNULL(hsl.DiemGiuaKy, 0) * 0.3 + ISNULL(hsl.DiemCuoiKy, 0) * 0.5) AS DECIMAL(5,2))), 0) AS AvgScore
        FROM LOPHOC l
        LEFT JOIN HOCSINH_LOPHOC hsl ON hsl.MaLopHoc = l.MaLopHoc
        GROUP BY l.TenLopHoc
        ORDER BY AvgScore DESC
        """;

    private static final String STUDENTS_PER_CLASS = """
        SELECT l.TenLopHoc, COUNT(hsl.MaHocSinh) as Count
        FROM LOPHOC l
        LEFT JOIN HOCSINH_LOPHOC hsl ON l.MaLopHoc = hsl.MaLopHoc
        GROUP BY l.TenLopHoc
        HAVING COUNT(hsl.MaHocSinh) > 0
        ORDER BY Count DESC
        """;

    private static final String SCORE_DISTRIBUTION = """
        WITH CalculatedScores AS (
            SELECT MaLopHoc, (ISNULL(DiemThuongXuyen, 0) * 0.2 + ISNULL(DiemGiuaKy, 0) * 0.3 + ISNULL(DiemCuoiKy, 0) * 0.5) as DiemSo
            FROM HOCSINH_LOPHOC
        )
        SELECT 
            CASE 
                WHEN DiemSo < 5 THEN 'Yếu (<5)'
                WHEN DiemSo >= 5 AND DiemSo < 7 THEN 'Trung bình (5-7)'
                WHEN DiemSo >= 7 AND DiemSo < 9 THEN 'Khá (7-9)'
                ELSE 'Giỏi (>=9)'
            END as Range,
            COUNT(*) as Count
        FROM CalculatedScores
        WHERE MaLopHoc = ? AND DiemSo IS NOT NULL
        GROUP BY 
            CASE 
                WHEN DiemSo < 5 THEN 'Yếu (<5)'
                WHEN DiemSo >= 5 AND DiemSo < 7 THEN 'Trung bình (5-7)'
                WHEN DiemSo >= 7 AND DiemSo < 9 THEN 'Khá (7-9)'
                ELSE 'Giỏi (>=9)'
            END
        """;

    private static final String NEW_STUDENTS_TREND = """
        SELECT FORMAT(NgayNhapHoc, 'yyyy-MM') as Month, COUNT(*) as Count
        FROM HOCSINH
        WHERE NgayNhapHoc BETWEEN ? AND ?
        GROUP BY FORMAT(NgayNhapHoc, 'yyyy-MM')
        ORDER BY Month
        """;

    private static final String EXCELLENT_RATIO = """
        WITH CalculatedScores AS (
            SELECT (ISNULL(DiemThuongXuyen, 0) * 0.2 + ISNULL(DiemGiuaKy, 0) * 0.3 + ISNULL(DiemCuoiKy, 0) * 0.5) as DiemSo
            FROM HOCSINH_LOPHOC
        )
        SELECT 
            CASE 
                WHEN DiemSo >= ? THEN 'Xuất sắc'
                ELSE 'Khác'
            END as Type,
            COUNT(*) as Count
        FROM CalculatedScores
        WHERE DiemSo IS NOT NULL
        GROUP BY 
            CASE 
                WHEN DiemSo >= ? THEN 'Xuất sắc'
                ELSE 'Khác'
            END
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

    public java.util.Map<String, Integer> getStudentsPerClass() {
        return runMapQuery(STUDENTS_PER_CLASS);
    }

    public java.util.Map<String, Integer> getScoreDistribution(String classId) {
        java.util.Map<String, Integer> result = new java.util.LinkedHashMap<>();
        // Initialize order
        result.put("Yếu (<5)", 0);
        result.put("Trung bình (5-7)", 0);
        result.put("Khá (7-9)", 0);
        result.put("Giỏi (>=9)", 0);
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(SCORE_DISTRIBUTION)) {
            ps.setString(1, classId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString(1), rs.getInt(2));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public java.util.Map<String, Integer> getNewStudentsTrend(LocalDate from, LocalDate to) {
        java.util.Map<String, Integer> result = new java.util.LinkedHashMap<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(NEW_STUDENTS_TREND)) {
            ps.setDate(1, java.sql.Date.valueOf(from));
            ps.setDate(2, java.sql.Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString(1), rs.getInt(2));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public java.util.Map<String, Integer> getExcellentRatio(double threshold) {
        java.util.Map<String, Integer> result = new java.util.HashMap<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(EXCELLENT_RATIO)) {
            ps.setDouble(1, threshold);
            ps.setDouble(2, threshold);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString(1), rs.getInt(2));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private java.util.Map<String, Integer> runMapQuery(String sql) {
        java.util.Map<String, Integer> result = new java.util.LinkedHashMap<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
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
