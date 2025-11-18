package com.famta.service;

import com.famta.database.DatabaseManager;
import com.famta.service.dto.AdminClassOverview;
import com.famta.service.dto.GuardianContactView;
import com.famta.service.dto.StudentClassEnrollment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation that reads the account projection queries described in
 * docs/Truy_van_du_lieu.sql to keep the UI synchronized with SQL Server.
 */
public class JdbcAccountOverviewService implements AccountOverviewService {

    private static final String ADMIN_QUERY = """
        SELECT LH.MaLopHoc,
               LH.TenLopHoc,
               MH.TenMonHoc,
               GV.Ho   AS GVHo,
               GV.TenLot AS GVTenLot,
               GV.Ten AS GVTen,
               PH.TenPhongHoc
        FROM LOPHOC LH
        LEFT JOIN MONHOC MH ON LH.MaMonHoc = MH.MaMonHoc
        LEFT JOIN GIAOVIEN GV ON LH.MaGiaoVien = GV.MaGiaoVien
        LEFT JOIN PHONGHOC PH ON LH.MaPhongHoc = PH.MaPhongHoc
        ORDER BY LH.MaLopHoc
        """;

    private static final String STUDENT_QUERY = """
        SELECT HS.MaHocSinh,
               HS.Ho      AS HoHS,
               HS.TenLot  AS TenLotHS,
               HS.Ten     AS TenHS,
               LH.MaLopHoc,
               LH.TenLopHoc
        FROM HOCSINH HS
        INNER JOIN HOCSINH_LOPHOC HSLH ON HS.MaHocSinh = HSLH.MaHocSinh
        INNER JOIN LOPHOC LH ON HSLH.MaLopHoc = LH.MaLopHoc
        ORDER BY HS.MaHocSinh, LH.MaLopHoc
        """;

    private static final String GUARDIAN_QUERY = """
        SELECT HS.MaHocSinh,
               HS.Ho      AS HoHS,
               HS.TenLot  AS TenLotHS,
               HS.Ten     AS TenHS,
               NGH.MaNguoiGiamHo,
               NGH.Ho     AS HoNGH,
               NGH.TenLot AS TenLotNGH,
               NGH.Ten    AS TenNGH,
               NGH.DiaChiEmail,
               LNGH.Ten   AS LoaiQuanHe
        FROM HOCSINH HS
        INNER JOIN HOCSINH_NGUOIGIAMHO HSNGH ON HS.MaHocSinh = HSNGH.MaHocSinh
        INNER JOIN NGUOIGIAMHO NGH ON HSNGH.MaNguoiGiamHo = NGH.MaNguoiGiamHo
        INNER JOIN LOAINGUOIGIAMHO LNGH ON HSNGH.MaLoaiNguoiGiamHo = LNGH.MaLoaiNguoiGiamHo
        ORDER BY HS.MaHocSinh, NGH.MaNguoiGiamHo
        """;

    @Override
    public List<AdminClassOverview> fetchAdminClassOverview() {
        List<AdminClassOverview> rows = new ArrayList<>();
        Connection connection = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(ADMIN_QUERY)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String maLop = safeTrim(resultSet.getString("MaLopHoc"));
                    String tenLop = safeTrim(resultSet.getString("TenLopHoc"));
                    String monHoc = safeTrim(resultSet.getString("TenMonHoc"));
                    String giaoVien = buildFullName(
                        resultSet.getString("GVHo"),
                        resultSet.getString("GVTenLot"),
                        resultSet.getString("GVTen"),
                        "Chưa phân công"
                    );
                    String phongHoc = safeTrim(resultSet.getString("TenPhongHoc"));
                    rows.add(new AdminClassOverview(maLop, tenLop, monHoc, giaoVien, phongHoc));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải danh sách lớp học cho admin", ex);
        }
        return rows;
    }

    @Override
    public List<StudentClassEnrollment> fetchStudentClassEnrollments() {
        List<StudentClassEnrollment> rows = new ArrayList<>();
        Connection connection = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(STUDENT_QUERY)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String maHocSinh = safeTrim(resultSet.getString("MaHocSinh"));
                    String tenHocSinh = buildFullName(
                        resultSet.getString("HoHS"),
                        resultSet.getString("TenLotHS"),
                        resultSet.getString("TenHS"),
                        maHocSinh
                    );
                    String maLop = safeTrim(resultSet.getString("MaLopHoc"));
                    String tenLop = safeTrim(resultSet.getString("TenLopHoc"));
                    rows.add(new StudentClassEnrollment(maHocSinh, tenHocSinh, maLop, tenLop));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải danh sách học viên theo lớp", ex);
        }
        return rows;
    }

    @Override
    public List<GuardianContactView> fetchGuardianContacts() {
        List<GuardianContactView> rows = new ArrayList<>();
        Connection connection = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(GUARDIAN_QUERY)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String maHocSinh = safeTrim(resultSet.getString("MaHocSinh"));
                    String tenHocSinh = buildFullName(
                        resultSet.getString("HoHS"),
                        resultSet.getString("TenLotHS"),
                        resultSet.getString("TenHS"),
                        maHocSinh
                    );
                    String maNguoiGiamHo = safeTrim(resultSet.getString("MaNguoiGiamHo"));
                    String tenNguoiGiamHo = buildFullName(
                        resultSet.getString("HoNGH"),
                        resultSet.getString("TenLotNGH"),
                        resultSet.getString("TenNGH"),
                        maNguoiGiamHo
                    );
                    String loaiQuanHe = safeTrim(resultSet.getString("LoaiQuanHe"));
                    String email = safeTrim(resultSet.getString("DiaChiEmail"));
                    String sdt = safeTrim(readOptional(resultSet, "SDT"));
                    rows.add(new GuardianContactView(
                        maHocSinh,
                        tenHocSinh,
                        maNguoiGiamHo,
                        tenNguoiGiamHo,
                        loaiQuanHe,
                        email,
                        sdt
                    ));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải danh sách phụ huynh", ex);
        }
        return rows;
    }

    private String buildFullName(String ho, String tenLot, String ten, String fallback) {
        StringBuilder builder = new StringBuilder();
        if (ho != null && !ho.isBlank()) {
            builder.append(ho.trim());
        }
        if (tenLot != null && !tenLot.isBlank()) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(tenLot.trim());
        }
        if (ten != null && !ten.isBlank()) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(ten.trim());
        }
        return builder.length() == 0 ? fallback : builder.toString();
    }

    private String safeTrim(String value) {
        return value == null ? null : value.trim();
    }

    private String readOptional(ResultSet resultSet, String column) throws SQLException {
        try {
            resultSet.findColumn(column);
        } catch (SQLException missingColumn) {
            return null;
        }
        return resultSet.getString(column);
    }
}
