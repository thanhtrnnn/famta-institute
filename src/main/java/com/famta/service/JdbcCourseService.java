package com.famta.service;

import com.famta.database.DatabaseManager;
import com.famta.service.dto.SubjectSummary;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcCourseService {

    private static final String SUBJECT_SQL = """
        SELECT
            mh.MaMonHoc,
            mh.TenMonHoc,
            COALESCE(k.TenKhoa, 'Chưa phân khoa') AS TenKhoa
        FROM MONHOC mh
        LEFT JOIN KHOA k ON k.MaKhoa = mh.MaKhoa
        ORDER BY mh.TenMonHoc
        """;

    public List<SubjectSummary> fetchSubjects() {
        Connection connection = DatabaseManager.getInstance().getConnection();
        List<SubjectSummary> subjects = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SUBJECT_SQL); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                subjects.add(new SubjectSummary(
                    rs.getString("MaMonHoc"),
                    rs.getString("TenMonHoc"),
                    rs.getString("TenKhoa")
                ));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải danh sách môn học", ex);
        }
        return subjects;
    }

    public void deleteSubject(String maMonHoc) throws SQLException {
        String sql = "DELETE FROM MONHOC WHERE MaMonHoc = ?";
        Connection connection = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, maMonHoc);
            ps.executeUpdate();
        }
    }
}
