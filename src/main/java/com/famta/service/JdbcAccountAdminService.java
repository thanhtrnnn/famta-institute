package com.famta.service;

import com.famta.database.DatabaseManager;
import com.famta.model.QuyenTruyCap;
import com.famta.model.TaiKhoan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC-backed account administration service.
 */
public class JdbcAccountAdminService implements AccountAdminService {

    private static final String SELECT_SQL = "SELECT TenDangNhap, MatKhauHash, Quyen FROM TAIKHOAN ORDER BY TenDangNhap";
    private static final String INSERT_SQL = "INSERT INTO TAIKHOAN (TenDangNhap, MatKhauHash, Quyen) VALUES (?, ?, ?)";
    private static final String UPDATE_ROLE_SQL = "UPDATE TAIKHOAN SET Quyen = ? WHERE TenDangNhap = ?";
    private static final String UPDATE_PASSWORD_SQL = "UPDATE TAIKHOAN SET MatKhauHash = ? WHERE TenDangNhap = ?";
    private static final String DELETE_SQL = "DELETE FROM TAIKHOAN WHERE TenDangNhap = ?";

    private final DatabaseManager databaseManager;

    public JdbcAccountAdminService() {
        this(DatabaseManager.getInstance());
    }

    public JdbcAccountAdminService(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public List<TaiKhoan> findAll() {
        List<TaiKhoan> accounts = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_SQL);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                accounts.add(mapAccount(rs));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải danh sách tài khoản", ex);
        }
        return accounts;
    }

    @Override
    public void createAccount(String username, String plainPassword, QuyenTruyCap role) {
        String normalizedUsername = normalize(username);
        validatePassword(plainPassword);
        QuyenTruyCap validatedRole = requireRole(role);
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setString(1, normalizedUsername);
            statement.setString(2, TaiKhoan.hash(plainPassword));
            statement.setString(3, validatedRole.name());
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tạo tài khoản " + normalizedUsername, ex);
        }
    }

    @Override
    public void updateRole(String username, QuyenTruyCap role) {
        String normalizedUsername = normalize(username);
        QuyenTruyCap validatedRole = requireRole(role);
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_ROLE_SQL)) {
            statement.setString(1, validatedRole.name());
            statement.setString(2, normalizedUsername);
            int updated = statement.executeUpdate();
            if (updated == 0) {
                throw new IllegalStateException("Không tìm thấy tài khoản " + normalizedUsername);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể cập nhật quyền cho tài khoản " + normalizedUsername, ex);
        }
    }

    @Override
    public void resetPassword(String username, String plainPassword) {
        String normalizedUsername = normalize(username);
        validatePassword(plainPassword);
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_PASSWORD_SQL)) {
            statement.setString(1, TaiKhoan.hash(plainPassword));
            statement.setString(2, normalizedUsername);
            int updated = statement.executeUpdate();
            if (updated == 0) {
                throw new IllegalStateException("Không tìm thấy tài khoản " + normalizedUsername);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể đặt lại mật khẩu cho " + normalizedUsername, ex);
        }
    }

    @Override
    public void deleteAccount(String username) {
        String normalizedUsername = normalize(username);
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setString(1, normalizedUsername);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể xóa tài khoản " + normalizedUsername, ex);
        }
    }

    private TaiKhoan mapAccount(ResultSet rs) throws SQLException {
        String username = rs.getString("TenDangNhap");
        String hash = rs.getString("MatKhauHash");
        String roleValue = rs.getString("Quyen");
        QuyenTruyCap role = QuyenTruyCap.fromDatabaseValue(roleValue)
            .orElseThrow(() -> new IllegalStateException("Không xác định được quyền " + roleValue));
        return TaiKhoan.fromHash(username, hash, role);
    }

    private String normalize(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        }
        return username.trim();
    }

    private void validatePassword(String plainPassword) {
        if (plainPassword == null || plainPassword.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự");
        }
    }

    private QuyenTruyCap requireRole(QuyenTruyCap role) {
        if (role == null) {
            throw new IllegalArgumentException("Quyền truy cập không hợp lệ");
        }
        return role;
    }
}
