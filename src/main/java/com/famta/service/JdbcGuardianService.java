package com.famta.service;

import com.famta.database.DatabaseManager;
import com.famta.model.NguoiGiamHo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcGuardianService {

    public List<NguoiGiamHo> getAllGuardians() throws SQLException {
        List<NguoiGiamHo> list = new ArrayList<>();
        String sql = "SELECT * FROM NguoiGiamHo";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToGuardian(rs));
            }
        }
        return list;
    }

    public List<NguoiGiamHo> searchGuardians(String keyword) throws SQLException {
        List<NguoiGiamHo> list = new ArrayList<>();
        String sql = "SELECT * FROM NguoiGiamHo WHERE Ho LIKE ? OR Ten LIKE ? OR SDT LIKE ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToGuardian(rs));
                }
            }
        }
        return list;
    }

    public void createGuardian(NguoiGiamHo guardian) throws SQLException {
        String sql = "INSERT INTO NguoiGiamHo (MaNguoiGiamHo, Ho, TenLot, Ten, DiaChiEmail, SDT) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, guardian.getMaNguoiGiamHo());
            pstmt.setString(2, guardian.getHo());
            pstmt.setString(3, guardian.getTenLot());
            pstmt.setString(4, guardian.getTen());
            pstmt.setString(5, guardian.getDiaChiEmail());
            pstmt.setString(6, guardian.getSdt());
            pstmt.executeUpdate();
        }
    }

    public void updateGuardian(NguoiGiamHo guardian) throws SQLException {
        String sql = "UPDATE NguoiGiamHo SET Ho = ?, TenLot = ?, Ten = ?, DiaChiEmail = ?, SDT = ? WHERE MaNguoiGiamHo = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, guardian.getHo());
            pstmt.setString(2, guardian.getTenLot());
            pstmt.setString(3, guardian.getTen());
            pstmt.setString(4, guardian.getDiaChiEmail());
            pstmt.setString(5, guardian.getSdt());
            pstmt.setString(6, guardian.getMaNguoiGiamHo());
            pstmt.executeUpdate();
        }
    }

    public void deleteGuardian(String maNguoiGiamHo) throws SQLException {
        String sql = "DELETE FROM NguoiGiamHo WHERE MaNguoiGiamHo = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maNguoiGiamHo);
            pstmt.executeUpdate();
        }
    }

    private NguoiGiamHo mapResultSetToGuardian(ResultSet rs) throws SQLException {
        return new NguoiGiamHo(
            rs.getString("MaNguoiGiamHo"),
            rs.getString("Ho"),
            rs.getString("TenLot"),
            rs.getString("Ten"),
            rs.getString("DiaChiEmail"),
            rs.getString("SDT")
        );
    }
}
