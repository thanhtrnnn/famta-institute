package com.famta.service;

import com.famta.database.DatabaseManager;
import com.famta.model.HocSinh;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * JDBC-backed implementation that reads students from the SQL Server database.
 */
public class JdbcHocVienService implements HocVienService {

    private static final boolean HAS_GENDER_COLUMN = detectGenderColumn();

    private static final String BASE_SELECT =
        "SELECT MaHocSinh, Ho, TenLot, Ten, NgaySinh, NgayNhapHoc" +
            (HAS_GENDER_COLUMN ? ", GioiTinh" : "") +
            " FROM HOCSINH";

    private static final String INSERT_WITH_GENDER =
        "INSERT INTO HOCSINH (MaHocSinh, Ho, TenLot, Ten, NgaySinh, NgayNhapHoc, GioiTinh) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String INSERT_WITHOUT_GENDER =
        "INSERT INTO HOCSINH (MaHocSinh, Ho, TenLot, Ten, NgaySinh, NgayNhapHoc) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

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
        Objects.requireNonNull(hocVien, "hocVien");
        if (hocVien.getMaHocSinh() == null || hocVien.getMaHocSinh().isBlank()) {
            throw new IllegalArgumentException("Mã học viên không được để trống");
        }
        if (getHocVienById(hocVien.getMaHocSinh()) != null) {
            return false;
        }

        Connection connection = DatabaseManager.getInstance().getConnection();
        String sql = HAS_GENDER_COLUMN ? INSERT_WITH_GENDER : INSERT_WITHOUT_GENDER;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, hocVien.getMaHocSinh().trim());
            statement.setString(2, trimToNull(hocVien.getHo()));
            statement.setString(3, trimToNull(hocVien.getTenLot()));
            statement.setString(4, trimToNull(hocVien.getTen()));
            setDate(statement, 5, hocVien.getNgaySinh());
            setDate(statement, 6, hocVien.getNgayNhapHoc());
            if (HAS_GENDER_COLUMN) {
                statement.setString(7, trimToNull(hocVien.getGioiTinh()));
            }
            return statement.executeUpdate() == 1;
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể thêm học viên mới", ex);
        }
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
        String gioiTinh = HAS_GENDER_COLUMN ? readOptionalString(resultSet, "GioiTinh") : null;

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

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static void setDate(PreparedStatement statement, int index, LocalDate date) throws SQLException {
        if (date == null) {
            statement.setNull(index, Types.DATE);
        } else {
            statement.setDate(index, Date.valueOf(date));
        }
    }

    private static boolean detectGenderColumn() {
        Connection connection = DatabaseManager.getInstance().getConnection();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet rs = metaData.getColumns(connection.getCatalog(), null, "HOCSINH", "GioiTinh")) {
                return rs.next();
            }
        } catch (SQLException ex) {
            return false;
        }
    }
}
