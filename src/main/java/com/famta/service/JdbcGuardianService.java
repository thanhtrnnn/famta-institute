package com.famta.service;

import com.famta.database.DatabaseManager;
import com.famta.model.HocSinh;
import com.famta.model.LoaiNguoiGiamHo;
import com.famta.model.MoiQuanHeGiamHo;
import com.famta.model.NguoiGiamHo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcGuardianService {

    public String getGuardianIdByUsername(String username) throws SQLException {
        String sql = "SELECT MaNguoiGiamHo FROM NGUOIGIAMHO WHERE TenDangNhap = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("MaNguoiGiamHo");
                }
            }
        }
        return null;
    }

    public List<NguoiGiamHo> getAllGuardians() throws SQLException {
        List<NguoiGiamHo> list = new ArrayList<>();
        String sql = "SELECT * FROM NguoiGiamHo";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                NguoiGiamHo guardian = mapResultSetToGuardian(rs);
                loadRelationships(conn, guardian);
                list.add(guardian);
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
                    NguoiGiamHo guardian = mapResultSetToGuardian(rs);
                    loadRelationships(conn, guardian);
                    list.add(guardian);
                }
            }
        }
        return list;
    }

    public List<HocSinh> getStudentsByGuardian(String maNguoiGiamHo) throws SQLException {
        List<HocSinh> list = new ArrayList<>();
        String sql = "SELECT hs.* FROM HOCSINH hs JOIN HOCSINH_NGUOIGIAMHO hng ON hs.MaHocSinh = hng.MaHocSinh WHERE hng.MaNguoiGiamHo = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maNguoiGiamHo);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new HocSinh(
                        rs.getString("MaHocSinh"),
                        rs.getString("Ho"),
                        rs.getString("TenLot"),
                        rs.getString("Ten"),
                        rs.getDate("NgaySinh").toLocalDate(),
                        rs.getString("GioiTinh"),
                        rs.getDate("NgayNhapHoc").toLocalDate()
                    ));
                }
            }
        }
        return list;
    }

    private void loadRelationships(Connection conn, NguoiGiamHo guardian) throws SQLException {
        String sql = "SELECT h.MaHocSinh, h.Ho, h.TenLot, h.Ten, h.NgaySinh, h.GioiTinh, h.NgayNhapHoc, l.Ten as QuanHe " +
                     "FROM HOCSINH_NGUOIGIAMHO hng " +
                     "JOIN HOCSINH h ON hng.MaHocSinh = h.MaHocSinh " +
                     "JOIN LOAINGUOIGIAMHO l ON hng.MaLoaiNguoiGiamHo = l.MaLoaiNguoiGiamHo " +
                     "WHERE hng.MaNguoiGiamHo = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, guardian.getMaNguoiGiamHo());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    HocSinh hs = new HocSinh(
                        rs.getString("MaHocSinh"),
                        rs.getString("Ho"),
                        rs.getString("TenLot"),
                        rs.getString("Ten"),
                        rs.getDate("NgaySinh") != null ? rs.getDate("NgaySinh").toLocalDate() : null,
                        rs.getString("GioiTinh"),
                        rs.getDate("NgayNhapHoc") != null ? rs.getDate("NgayNhapHoc").toLocalDate() : null
                    );
                    String quanHeStr = rs.getString("QuanHe");
                    LoaiNguoiGiamHo loai = mapToLoaiNguoiGiamHo(quanHeStr);
                    
                    MoiQuanHeGiamHo mqh = new MoiQuanHeGiamHo(hs, guardian, loai);
                    guardian.addMoiQuanHe(mqh);
                }
            }
        }
    }

    private LoaiNguoiGiamHo mapToLoaiNguoiGiamHo(String ten) {
        if (ten == null) return LoaiNguoiGiamHo.KHAC;
        try {
            return LoaiNguoiGiamHo.valueOf(ten.toUpperCase());
        } catch (IllegalArgumentException e) {
            switch (ten.toLowerCase()) {
                case "bố": case "cha": return LoaiNguoiGiamHo.CHA;
                case "mẹ": return LoaiNguoiGiamHo.ME;
                case "ông": return LoaiNguoiGiamHo.ONG;
                case "bà": return LoaiNguoiGiamHo.BA;
                case "anh": return LoaiNguoiGiamHo.ANH;
                case "chị": return LoaiNguoiGiamHo.CHI;
                default: return LoaiNguoiGiamHo.KHAC;
            }
        }
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
