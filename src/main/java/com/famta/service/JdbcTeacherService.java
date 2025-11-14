package com.famta.service;

import com.famta.database.DatabaseManager;
import com.famta.model.GiaoVien;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JdbcTeacherService implements TeacherService {

    private static final String SELECT_ALL = "SELECT MaGiaoVien, Ho, TenLot, Ten, GioiTinh, DiaChiEmail, SDT FROM GIAOVIEN";
    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE MaGiaoVien = ?";
    private static final String INSERT_SQL =
        "INSERT INTO GIAOVIEN (MaGiaoVien, Ho, TenLot, Ten, GioiTinh, DiaChiEmail, SDT) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
        "UPDATE GIAOVIEN SET Ho = ?, TenLot = ?, Ten = ?, GioiTinh = ?, DiaChiEmail = ?, SDT = ? WHERE MaGiaoVien = ?";
    private static final String DELETE_SQL = "DELETE FROM GIAOVIEN WHERE MaGiaoVien = ?";

    @Override
    public List<GiaoVien> findAll() {
        List<GiaoVien> result = new ArrayList<>();
        Connection conn = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapTeacher(rs));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to query teachers", ex);
        }
        return result;
    }

    @Override
    public boolean addTeacher(GiaoVien giaoVien) {
        Objects.requireNonNull(giaoVien, "giaoVien");
        if (giaoVien.getMaGiaoVien() == null || giaoVien.getMaGiaoVien().isBlank()) {
            throw new IllegalArgumentException("Mã giáo viên không được để trống");
        }
        if (findById(giaoVien.getMaGiaoVien()) != null) {
            return false;
        }

        Connection connection = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setString(1, giaoVien.getMaGiaoVien().trim());
            statement.setString(2, trimToNull(giaoVien.getHo()));
            statement.setString(3, trimToNull(giaoVien.getTenLot()));
            statement.setString(4, trimToNull(giaoVien.getTen()));
            statement.setString(5, trimToNull(giaoVien.getGioiTinh()));
            statement.setString(6, trimToNull(giaoVien.getDiaChiEmail()));
            statement.setString(7, trimToNull(giaoVien.getSdt()));
            return statement.executeUpdate() == 1;
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể thêm giáo viên", ex);
        }
    }

    @Override
    public boolean updateTeacher(GiaoVien giaoVien) {
        Objects.requireNonNull(giaoVien, "giaoVien");
        if (giaoVien.getMaGiaoVien() == null || giaoVien.getMaGiaoVien().isBlank()) {
            throw new IllegalArgumentException("Mã giáo viên không được để trống");
        }

        Connection connection = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, trimToNull(giaoVien.getHo()));
            statement.setString(2, trimToNull(giaoVien.getTenLot()));
            statement.setString(3, trimToNull(giaoVien.getTen()));
            statement.setString(4, trimToNull(giaoVien.getGioiTinh()));
            statement.setString(5, trimToNull(giaoVien.getDiaChiEmail()));
            statement.setString(6, trimToNull(giaoVien.getSdt()));
            statement.setString(7, giaoVien.getMaGiaoVien().trim());
            return statement.executeUpdate() == 1;
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể cập nhật giáo viên", ex);
        }
    }

    @Override
    public boolean deleteTeacher(String maGiaoVien) {
        if (maGiaoVien == null || maGiaoVien.isBlank()) {
            return false;
        }
        Connection connection = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setString(1, maGiaoVien.trim());
            return statement.executeUpdate() == 1;
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể xóa giáo viên", ex);
        }
    }

    private GiaoVien findById(String maGiaoVien) {
        Connection connection = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)) {
            statement.setString(1, maGiaoVien.trim());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapTeacher(resultSet);
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể truy vấn giáo viên", ex);
        }
        return null;
    }

    private GiaoVien mapTeacher(ResultSet rs) throws SQLException {
        String ma = rs.getString("MaGiaoVien");
        String ho = rs.getString("Ho");
        String tenLot = rs.getString("TenLot");
        String ten = rs.getString("Ten");
        String gioiTinh = rs.getString("GioiTinh");
        String email = rs.getString("DiaChiEmail");
        String sdt = rs.getString("SDT");
        return new GiaoVien(ma != null ? ma.trim() : "", ho, tenLot, ten, gioiTinh, email, sdt);
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
