package com.famta.service;

import com.famta.database.DatabaseManager;
import com.famta.model.QuyenTruyCap;
import com.famta.model.TaiKhoan;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

/**
 * Performs credential validation against the TAIKHOAN table.
 */
public class AuthService {

    private static final String LOOKUP_SQL = "SELECT TenDangNhap, MatKhauHash, Quyen FROM TAIKHOAN WHERE TenDangNhap = ?";

    private final DatabaseManager databaseManager;

    public AuthService(DatabaseManager databaseManager) {
        this.databaseManager = Objects.requireNonNull(databaseManager, "databaseManager");
    }

    public Optional<TaiKhoan> authenticate(String username, String password) {
        if (username == null || password == null) {
            return Optional.empty();
        }
        String normalizedUsername = username.trim();
        if (normalizedUsername.isEmpty()) {
            return Optional.empty();
        }
        try (PreparedStatement statement = databaseManager.getConnection().prepareStatement(LOOKUP_SQL)) {
            statement.setString(1, normalizedUsername);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                String storedHash = resultSet.getString("MatKhauHash");
                String roleValue = resultSet.getString("Quyen");
                QuyenTruyCap role = QuyenTruyCap.fromDatabaseValue(roleValue)
                    .orElseThrow(() -> new IllegalStateException("Unknown role " + roleValue));
                String storedUsername = resultSet.getString("TenDangNhap");
                TaiKhoan account = TaiKhoan.fromHash(storedUsername, storedHash, role);
                return account.dangNhap(password) ? Optional.of(account) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Không thể xác thực tài khoản", e);
        }
    }
}
