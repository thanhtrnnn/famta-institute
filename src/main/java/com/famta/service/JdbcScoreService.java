package com.famta.service;

import com.famta.database.DatabaseManager;
import com.famta.service.dto.ScoreClassOption;
import com.famta.service.dto.ScoreEntry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * JDBC-backed implementation that reads and writes student scores.
 */
public class JdbcScoreService implements ScoreService {

    private static final String CLASS_SQL = """
        SELECT l.MaLopHoc, l.TenLopHoc, m.MaMonHoc, m.TenMonHoc
        FROM LOPHOC l
        LEFT JOIN MONHOC m ON m.MaMonHoc = l.MaMonHoc
        ORDER BY l.TenLopHoc
        """;

    private static final String CLASS_BY_SEMESTER_SQL = """
        SELECT l.MaLopHoc, l.TenLopHoc, m.MaMonHoc, m.TenMonHoc
        FROM LOPHOC l
        LEFT JOIN MONHOC m ON m.MaMonHoc = l.MaMonHoc
        WHERE l.MaHocKy = ?
        ORDER BY l.TenLopHoc
        """;

    private static final String CLASS_BY_TEACHER_SQL = """
        SELECT l.MaLopHoc, l.TenLopHoc, m.MaMonHoc, m.TenMonHoc
        FROM LOPHOC l
        LEFT JOIN MONHOC m ON m.MaMonHoc = l.MaMonHoc
        INNER JOIN GIAOVIEN gv ON gv.MaGiaoVien = l.MaGiaoVien
        WHERE l.MaHocKy = ? AND gv.TenDangNhap = ?
        ORDER BY l.TenLopHoc
        """;

    private static final String CLASS_BY_STUDENT_SQL = """
        SELECT l.MaLopHoc, l.TenLopHoc, m.MaMonHoc, m.TenMonHoc
        FROM LOPHOC l
        LEFT JOIN MONHOC m ON m.MaMonHoc = l.MaMonHoc
        INNER JOIN HOCSINH_LOPHOC hsl ON hsl.MaLopHoc = l.MaLopHoc
        INNER JOIN HOCSINH hs ON hs.MaHocSinh = hsl.MaHocSinh
        WHERE l.MaHocKy = ? AND hs.TenDangNhap = ?
        ORDER BY l.TenLopHoc
        """;

    private static final String CLASS_BY_GUARDIAN_SQL = """
        SELECT DISTINCT l.MaLopHoc, l.TenLopHoc, m.MaMonHoc, m.TenMonHoc
        FROM LOPHOC l
        LEFT JOIN MONHOC m ON m.MaMonHoc = l.MaMonHoc
        INNER JOIN HOCSINH_LOPHOC hsl ON hsl.MaLopHoc = l.MaLopHoc
        INNER JOIN HOCSINH_NGUOIGIAMHO nghs ON nghs.MaHocSinh = hsl.MaHocSinh
        INNER JOIN NGUOIGIAMHO ngh ON ngh.MaNguoiGiamHo = nghs.MaNguoiGiamHo
        WHERE l.MaHocKy = ? AND ngh.TenDangNhap = ?
        ORDER BY l.TenLopHoc
        """;

    private static final String SCORE_SQL = """
        SELECT hs.MaHocSinh,
               LTRIM(RTRIM(CONCAT(
                    COALESCE(hs.Ho, ''),
                    CASE WHEN hs.TenLot IS NULL OR hs.TenLot = '' THEN '' ELSE ' ' + hs.TenLot END,
                    CASE WHEN hs.Ten IS NULL OR hs.Ten = '' THEN '' ELSE ' ' + hs.Ten END
               ))) AS HoTen,
               hsl.DiemThuongXuyen,
               hsl.DiemGiuaKy,
               hsl.DiemCuoiKy
        FROM HOCSINH_LOPHOC hsl
        INNER JOIN HOCSINH hs ON hs.MaHocSinh = hsl.MaHocSinh
        WHERE hsl.MaLopHoc = ?
        ORDER BY hs.Ho, hs.TenLot, hs.Ten, hs.MaHocSinh
        """;

    private static final String SCORE_BY_GUARDIAN_SQL = """
        SELECT hs.MaHocSinh,
               LTRIM(RTRIM(CONCAT(
                    COALESCE(hs.Ho, ''),
                    CASE WHEN hs.TenLot IS NULL OR hs.TenLot = '' THEN '' ELSE ' ' + hs.TenLot END,
                    CASE WHEN hs.Ten IS NULL OR hs.Ten = '' THEN '' ELSE ' ' + hs.Ten END
               ))) AS HoTen,
               hsl.DiemThuongXuyen,
               hsl.DiemGiuaKy,
               hsl.DiemCuoiKy
        FROM HOCSINH_LOPHOC hsl
        INNER JOIN HOCSINH hs ON hs.MaHocSinh = hsl.MaHocSinh
        INNER JOIN HOCSINH_NGUOIGIAMHO nghs ON nghs.MaHocSinh = hs.MaHocSinh
        INNER JOIN NGUOIGIAMHO ngh ON ngh.MaNguoiGiamHo = nghs.MaNguoiGiamHo
        WHERE hsl.MaLopHoc = ? AND ngh.TenDangNhap = ?
        ORDER BY hs.Ho, hs.TenLot, hs.Ten, hs.MaHocSinh
        """;

    private static final String SCORE_BY_STUDENT_SQL = """
        SELECT hs.MaHocSinh,
               LTRIM(RTRIM(CONCAT(
                    COALESCE(hs.Ho, ''),
                    CASE WHEN hs.TenLot IS NULL OR hs.TenLot = '' THEN '' ELSE ' ' + hs.TenLot END,
                    CASE WHEN hs.Ten IS NULL OR hs.Ten = '' THEN '' ELSE ' ' + hs.Ten END
               ))) AS HoTen,
               hsl.DiemThuongXuyen,
               hsl.DiemGiuaKy,
               hsl.DiemCuoiKy
        FROM HOCSINH_LOPHOC hsl
        INNER JOIN HOCSINH hs ON hs.MaHocSinh = hsl.MaHocSinh
        WHERE hsl.MaLopHoc = ? AND hs.TenDangNhap = ?
        ORDER BY hs.Ho, hs.TenLot, hs.Ten, hs.MaHocSinh
        """;

    private static final String UPDATE_SQL = """
        UPDATE HOCSINH_LOPHOC
        SET DiemThuongXuyen = ?, DiemGiuaKy = ?, DiemCuoiKy = ?
        WHERE MaHocSinh = ? AND MaLopHoc = ?
        """;

    private static final String INSERT_SQL = """
        INSERT INTO HOCSINH_LOPHOC (MaHocSinh, MaLopHoc, DiemThuongXuyen, DiemGiuaKy, DiemCuoiKy)
        VALUES (?, ?, ?, ?, ?)
        """;

    private final DatabaseManager databaseManager = DatabaseManager.getInstance();

    @Override
    public List<ScoreClassOption> findClassOptions() {
        List<ScoreClassOption> options = new ArrayList<>();
        Connection connection = databaseManager.getConnection();
        try (PreparedStatement ps = connection.prepareStatement(CLASS_SQL)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    options.add(mapClass(rs));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải danh sách lớp phục vụ nhập điểm", ex);
        }
        return options;
    }

    @Override
    public List<ScoreClassOption> findClassOptions(String semesterId) {
        List<ScoreClassOption> options = new ArrayList<>();
        Connection connection = databaseManager.getConnection();
        try (PreparedStatement ps = connection.prepareStatement(CLASS_BY_SEMESTER_SQL)) {
            ps.setString(1, semesterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    options.add(mapClass(rs));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải danh sách lớp cho học kỳ " + semesterId, ex);
        }
        return options;
    }

    @Override
    public List<ScoreClassOption> findClassOptionsForTeacher(String semesterId, String teacherId) {
        List<ScoreClassOption> options = new ArrayList<>();
        Connection connection = databaseManager.getConnection();
        try (PreparedStatement ps = connection.prepareStatement(CLASS_BY_TEACHER_SQL)) {
            ps.setString(1, semesterId);
            ps.setString(2, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    options.add(mapClass(rs));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải danh sách lớp cho giáo viên " + teacherId, ex);
        }
        return options;
    }

    @Override
    public List<ScoreClassOption> findClassOptionsForStudent(String semesterId, String studentId) {
        List<ScoreClassOption> options = new ArrayList<>();
        Connection connection = databaseManager.getConnection();
        try (PreparedStatement ps = connection.prepareStatement(CLASS_BY_STUDENT_SQL)) {
            ps.setString(1, semesterId);
            ps.setString(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    options.add(mapClass(rs));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải danh sách lớp cho học sinh " + studentId, ex);
        }
        return options;
    }

    @Override
    public List<ScoreClassOption> findClassOptionsForGuardian(String semesterId, String guardianId) {
        List<ScoreClassOption> options = new ArrayList<>();
        Connection connection = databaseManager.getConnection();
        try (PreparedStatement ps = connection.prepareStatement(CLASS_BY_GUARDIAN_SQL)) {
            ps.setString(1, semesterId);
            ps.setString(2, guardianId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    options.add(mapClass(rs));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải danh sách lớp cho phụ huynh " + guardianId, ex);
        }
        return options;
    }

    @Override
    public List<ScoreEntry> findScoresByClass(String classId) {
        if (classId == null || classId.isBlank()) {
            return List.of();
        }
        List<ScoreEntry> scores = new ArrayList<>();
        Connection connection = databaseManager.getConnection();
        try (PreparedStatement ps = connection.prepareStatement(SCORE_SQL)) {
            ps.setString(1, classId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    scores.add(mapScore(rs));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải bảng điểm cho lớp " + classId, ex);
        }
        return scores;
    }

    @Override
    public List<ScoreEntry> findScoresByClassForGuardian(String classId, String guardianId) {
        if (classId == null || classId.isBlank()) {
            return List.of();
        }
        List<ScoreEntry> scores = new ArrayList<>();
        Connection connection = databaseManager.getConnection();
        try (PreparedStatement ps = connection.prepareStatement(SCORE_BY_GUARDIAN_SQL)) {
            ps.setString(1, classId.trim());
            ps.setString(2, guardianId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    scores.add(mapScore(rs));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải bảng điểm cho lớp " + classId + " cho phụ huynh " + guardianId, ex);
        }
        return scores;
    }

    @Override
    public List<ScoreEntry> findScoresByClassForStudent(String classId, String studentId) {
        if (classId == null || classId.isBlank()) {
            return List.of();
        }
        List<ScoreEntry> scores = new ArrayList<>();
        Connection connection = databaseManager.getConnection();
        try (PreparedStatement ps = connection.prepareStatement(SCORE_BY_STUDENT_SQL)) {
            ps.setString(1, classId.trim());
            ps.setString(2, studentId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    scores.add(mapScore(rs));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải bảng điểm cho lớp " + classId + " cho học sinh " + studentId, ex);
        }
        return scores;
    }

    @Override
    public void updateScore(String classId, String studentId, Double regularScore, Double midtermScore, Double finalScore) {
        if (classId == null || classId.isBlank()) {
            throw new IllegalArgumentException("classId is required");
        }
        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException("studentId is required");
        }
        Connection connection = databaseManager.getConnection();
        try (PreparedStatement update = connection.prepareStatement(UPDATE_SQL)) {
            setDoubleOrNull(update, 1, regularScore);
            setDoubleOrNull(update, 2, midtermScore);
            setDoubleOrNull(update, 3, finalScore);
            update.setString(4, studentId.trim());
            update.setString(5, classId.trim());
            int rows = update.executeUpdate();
            if (rows == 0) {
                try (PreparedStatement insert = connection.prepareStatement(INSERT_SQL)) {
                    insert.setString(1, studentId.trim());
                    insert.setString(2, classId.trim());
                    setDoubleOrNull(insert, 3, regularScore);
                    setDoubleOrNull(insert, 4, midtermScore);
                    setDoubleOrNull(insert, 5, finalScore);
                    insert.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể lưu điểm cho học sinh " + studentId, ex);
        }
    }

    private void setDoubleOrNull(PreparedStatement ps, int index, Double value) throws SQLException {
        if (value == null) {
            ps.setNull(index, java.sql.Types.FLOAT);
        } else {
            ps.setDouble(index, value);
        }
    }

    private ScoreClassOption mapClass(ResultSet rs) throws SQLException {
        String classId = rs.getString("MaLopHoc");
        String className = safeTrim(rs.getString("TenLopHoc"));
        String subjectId = rs.getString("MaMonHoc");
        String subjectName = safeTrim(rs.getString("TenMonHoc"));
        return new ScoreClassOption(classId, className, subjectId, subjectName);
    }

    private ScoreEntry mapScore(ResultSet rs) throws SQLException {
        String studentId = rs.getString("MaHocSinh");
        String fullName = normalizeName(rs.getString("HoTen"));
        Double regular = rs.getObject("DiemThuongXuyen") == null ? null : rs.getDouble("DiemThuongXuyen");
        Double midterm = rs.getObject("DiemGiuaKy") == null ? null : rs.getDouble("DiemGiuaKy");
        Double finalScore = rs.getObject("DiemCuoiKy") == null ? null : rs.getDouble("DiemCuoiKy");
        return new ScoreEntry(studentId, fullName, regular, midterm, finalScore);
    }

    private String safeTrim(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeName(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        String lower = raw.toLowerCase(Locale.getDefault()).trim();
        StringBuilder builder = new StringBuilder(lower.length());
        boolean uppercase = true;
        for (char c : lower.toCharArray()) {
            if (Character.isWhitespace(c)) {
                uppercase = true;
                builder.append(' ');
                continue;
            }
            if (uppercase) {
                builder.append(Character.toUpperCase(c));
                uppercase = false;
            } else {
                builder.append(c);
            }
        }
        return builder.toString().replaceAll("\\s+", " ").trim();
    }
}
