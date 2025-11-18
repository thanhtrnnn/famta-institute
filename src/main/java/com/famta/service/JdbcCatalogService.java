package com.famta.service;

import com.famta.database.DatabaseManager;
import com.famta.model.Khoa;
import com.famta.model.LoaiPhongHoc;
import com.famta.model.MonHoc;
import com.famta.model.NamHoc;
import com.famta.model.PhongHoc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcCatalogService {

    private Connection getConnection() {
        return DatabaseManager.getInstance().getConnection();
    }

    // --- NAMHOC ---
    public List<NamHoc> getAllNamHoc() throws SQLException {
        List<NamHoc> list = new ArrayList<>();
        String sql = "SELECT * FROM NAMHOC ORDER BY MaNamHoc DESC";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                NamHoc nh = new NamHoc(
                    rs.getString("MaNamHoc"),
                    rs.getString("TenNamHoc"),
                    rs.getDate("NgayBatDau").toLocalDate(),
                    rs.getDate("NgayKetThuc").toLocalDate()
                );
                list.add(nh);
            }
        }
        return list;
    }

    public void createNamHoc(NamHoc nh) throws SQLException {
        String sql = "INSERT INTO NAMHOC (MaNamHoc, TenNamHoc, NgayBatDau, NgayKetThuc) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, nh.getMaNamHoc());
            stmt.setString(2, nh.getTenNamHoc());
            stmt.setDate(3, Date.valueOf(nh.getNgayBatDau()));
            stmt.setDate(4, Date.valueOf(nh.getNgayKetThuc()));
            stmt.executeUpdate();
        }
    }

    public void updateNamHoc(NamHoc nh) throws SQLException {
        String sql = "UPDATE NAMHOC SET TenNamHoc=?, NgayBatDau=?, NgayKetThuc=? WHERE MaNamHoc=?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, nh.getTenNamHoc());
            stmt.setDate(2, Date.valueOf(nh.getNgayBatDau()));
            stmt.setDate(3, Date.valueOf(nh.getNgayKetThuc()));
            stmt.setString(4, nh.getMaNamHoc());
            stmt.executeUpdate();
        }
    }

    public void deleteNamHoc(String maNamHoc) throws SQLException {
        String sql = "DELETE FROM NAMHOC WHERE MaNamHoc=?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, maNamHoc);
            stmt.executeUpdate();
        }
    }

    // --- KHOA ---
    public List<Khoa> getAllKhoa() throws SQLException {
        List<Khoa> list = new ArrayList<>();
        String sql = "SELECT * FROM KHOA ORDER BY MaKhoa";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Khoa k = new Khoa(
                    rs.getString("MaKhoa"),
                    rs.getString("TenKhoa")
                );
                list.add(k);
            }
        }
        return list;
    }

    public void createKhoa(Khoa k) throws SQLException {
        String sql = "INSERT INTO KHOA (MaKhoa, TenKhoa) VALUES (?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, k.getMaKhoa());
            stmt.setString(2, k.getTenKhoa());
            stmt.executeUpdate();
        }
    }

    public void updateKhoa(Khoa k) throws SQLException {
        String sql = "UPDATE KHOA SET TenKhoa=? WHERE MaKhoa=?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, k.getTenKhoa());
            stmt.setString(2, k.getMaKhoa());
            stmt.executeUpdate();
        }
    }

    public void deleteKhoa(String maKhoa) throws SQLException {
        String sql = "DELETE FROM KHOA WHERE MaKhoa=?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, maKhoa);
            stmt.executeUpdate();
        }
    }

    // --- MONHOC ---
    public List<MonHoc> getAllMonHoc() throws SQLException {
        List<MonHoc> list = new ArrayList<>();
        String sql = "SELECT m.*, k.TenKhoa FROM MONHOC m LEFT JOIN KHOA k ON m.MaKhoa = k.MaKhoa ORDER BY m.MaMonHoc";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String maKhoa = rs.getString("MaKhoa");
                Khoa k = null;
                if (maKhoa != null) {
                    k = new Khoa(maKhoa, rs.getString("TenKhoa"));
                }
                MonHoc mh = new MonHoc(
                    rs.getString("MaMonHoc"),
                    rs.getString("TenMonHoc"),
                    k
                );
                list.add(mh);
            }
        }
        return list;
    }

    public void createMonHoc(MonHoc mh) throws SQLException {
        String sql = "INSERT INTO MONHOC (MaMonHoc, TenMonHoc, MaKhoa) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, mh.getMaMonHoc());
            stmt.setString(2, mh.getTenMonHoc());
            stmt.setString(3, mh.getKhoa() != null ? mh.getKhoa().getMaKhoa() : null);
            stmt.executeUpdate();
        }
    }

    public void updateMonHoc(MonHoc mh) throws SQLException {
        String sql = "UPDATE MONHOC SET TenMonHoc=?, MaKhoa=? WHERE MaMonHoc=?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, mh.getTenMonHoc());
            stmt.setString(2, mh.getKhoa() != null ? mh.getKhoa().getMaKhoa() : null);
            stmt.setString(3, mh.getMaMonHoc());
            stmt.executeUpdate();
        }
    }

    public void deleteMonHoc(String maMonHoc) throws SQLException {
        String sql = "DELETE FROM MONHOC WHERE MaMonHoc=?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, maMonHoc);
            stmt.executeUpdate();
        }
    }

    // --- LOAIPHONGHOC ---
    public List<LoaiPhongHoc> getAllLoaiPhongHoc() throws SQLException {
        List<LoaiPhongHoc> list = new ArrayList<>();
        String sql = "SELECT * FROM LOAIPHONGHOC ORDER BY MaLoaiPhongHoc";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                LoaiPhongHoc lph = new LoaiPhongHoc(
                    rs.getString("MaLoaiPhongHoc"),
                    rs.getString("TenLoaiPhongHoc")
                );
                list.add(lph);
            }
        }
        return list;
    }

    // --- PHONGHOC ---
    public List<PhongHoc> getAllPhongHoc() throws SQLException {
        List<PhongHoc> list = new ArrayList<>();
        String sql = "SELECT p.*, l.TenLoaiPhongHoc FROM PHONGHOC p LEFT JOIN LOAIPHONGHOC l ON p.MaLoaiPhongHoc = l.MaLoaiPhongHoc ORDER BY p.MaPhongHoc";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                PhongHoc ph = new PhongHoc(
                    rs.getString("MaPhongHoc"),
                    rs.getString("TenPhongHoc"),
                    rs.getString("MaLoaiPhongHoc"),
                    rs.getString("TenLoaiPhongHoc")
                );
                list.add(ph);
            }
        }
        return list;
    }

    public void createPhongHoc(PhongHoc ph) throws SQLException {
        String sql = "INSERT INTO PHONGHOC (MaPhongHoc, TenPhongHoc, MaLoaiPhongHoc) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, ph.getMaPhongHoc());
            stmt.setString(2, ph.getTenPhongHoc());
            stmt.setString(3, ph.getMaLoaiPhongHoc());
            stmt.executeUpdate();
        }
    }

    public void updatePhongHoc(PhongHoc ph) throws SQLException {
        String sql = "UPDATE PHONGHOC SET TenPhongHoc=?, MaLoaiPhongHoc=? WHERE MaPhongHoc=?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, ph.getTenPhongHoc());
            stmt.setString(2, ph.getMaLoaiPhongHoc());
            stmt.setString(3, ph.getMaPhongHoc());
            stmt.executeUpdate();
        }
    }

    public void deletePhongHoc(String maPhongHoc) throws SQLException {
        String sql = "DELETE FROM PHONGHOC WHERE MaPhongHoc=?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, maPhongHoc);
            stmt.executeUpdate();
        }
    }
}
