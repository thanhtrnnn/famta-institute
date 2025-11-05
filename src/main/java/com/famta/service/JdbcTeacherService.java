package com.famta.service;

import com.famta.database.DatabaseManager;
import com.famta.model.GiaoVien;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcTeacherService implements TeacherService {

    private static final String SELECT_ALL = "SELECT MaGiaoVien, Ho, TenLot, Ten, GioiTinh, DiaChiEmail, SDT FROM GIAOVIEN";

    @Override
    public List<GiaoVien> findAll() {
        List<GiaoVien> result = new ArrayList<>();
        Connection conn = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String ma = rs.getString("MaGiaoVien");
                    String ho = rs.getString("Ho");
                    String tenLot = rs.getString("TenLot");
                    String ten = rs.getString("Ten");
                    String gioiTinh = rs.getString("GioiTinh");
                    String email = rs.getString("DiaChiEmail");
                    String sdt = rs.getString("SDT");
                    GiaoVien gv = new GiaoVien(ma != null ? ma.trim() : "", ho, tenLot, ten, gioiTinh, email, sdt);
                    result.add(gv);
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to query teachers", ex);
        }
        return result;
    }
}
