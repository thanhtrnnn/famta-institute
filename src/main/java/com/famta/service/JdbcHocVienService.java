package com.famta.service;

import com.famta.database.DatabaseManager;
import com.famta.model.HocSinh;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * JDBC-backed implementation that reads students from the SQL Server database.
 */
public class JdbcHocVienService implements HocVienService {

    private static final String BASE_SELECT =
        "SELECT MaHocSinh, Ho, TenLot, Ten, NgaySinh, NgayNhapHoc FROM HOCSINH";

    @Override
    public List<HocSinh> getAllHocVien() {
        List<HocSinh> students = new ArrayList<>();
        Connection connection = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(BASE_SELECT + " ORDER BY MaHocSinh")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    students.add(mapHocSinh(resultSet));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải danh sách học viên từ cơ sở dữ liệu", ex);
        }
        return students;
    }

    @Override
    public HocSinh getHocVienById(String maHocVien) {
        if (maHocVien == null || maHocVien.isBlank()) {
            return null;
        }
        Connection connection = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(BASE_SELECT + " WHERE MaHocSinh = ?")) {
            statement.setString(1, maHocVien.trim());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapHocSinh(resultSet);
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể truy vấn học viên: " + maHocVien, ex);
        }
        return null;
    }

    @Override
    public boolean addHocVien(HocSinh hocVien) {
        throw new UnsupportedOperationException("Chức năng thêm học viên chưa được triển khai cho JDBC service");
    }

    @Override
    public boolean updateHocVien(HocSinh hocVien) {
        throw new UnsupportedOperationException("Chức năng cập nhật học viên chưa được triển khai cho JDBC service");
    }

    @Override
    public boolean deleteHocVien(String maHocVien) {
        throw new UnsupportedOperationException("Chức năng xóa học viên chưa được triển khai cho JDBC service");
    }

    @Override
    public List<HocSinh> searchHocVienByName(String hoTen) {
        String keyword = hoTen == null ? "" : hoTen.trim().toLowerCase(Locale.getDefault());
        if (keyword.isEmpty()) {
            return getAllHocVien();
        }
        List<HocSinh> matched = new ArrayList<>();
        for (HocSinh hocSinh : getAllHocVien()) {
            String fullName = hocSinh.getHoTenDayDu().toLowerCase(Locale.getDefault());
            if (fullName.contains(keyword)) {
                matched.add(hocSinh);
            }
        }
        return matched;
    }

    private HocSinh mapHocSinh(ResultSet resultSet) throws SQLException {
        String maHocSinh = safeTrim(resultSet.getString("MaHocSinh"));
        String ho = safeTrim(resultSet.getString("Ho"));
        String tenLot = safeTrim(resultSet.getString("TenLot"));
        String ten = safeTrim(resultSet.getString("Ten"));
        LocalDate ngaySinh = toLocalDate(resultSet, "NgaySinh");
        LocalDate ngayNhapHoc = toLocalDate(resultSet, "NgayNhapHoc");
        String gioiTinh = readOptionalString(resultSet, "GioiTinh");

        HocSinh hocSinh = new HocSinh(
            maHocSinh,
            ho,
            tenLot,
            ten,
            ngaySinh,
            normalizeGender(gioiTinh),
            ngayNhapHoc
        );
        return hocSinh;
    }

    private static LocalDate toLocalDate(ResultSet resultSet, String column) throws SQLException {
        Date date = resultSet.getDate(column);
        return date == null ? null : date.toLocalDate();
    }

    private static String safeTrim(String value) {
        return value == null ? null : value.trim();
    }

    private static String readOptionalString(ResultSet resultSet, String column) throws SQLException {
        try {
            resultSet.findColumn(column);
        } catch (SQLException missingColumn) {
            return null;
        }
        String value = resultSet.getString(column);
        return value == null ? null : value.trim();
    }

    private static String normalizeGender(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim().toLowerCase(Locale.getDefault());
        if (normalized.startsWith("nam")) {
            return "Nam";
        }
        if (normalized.startsWith("nữ") || normalized.startsWith("nu")) {
            return "Nữ";
        }
        return value.trim();
    }
}
