package com.famta.service;

import com.famta.database.DatabaseManager;
import com.famta.model.BaoCaoThongKe;
import com.famta.model.DangKyHoc;
import com.famta.model.HocSinh;
import com.famta.model.Khoi;
import com.famta.model.LopHoc;
import com.famta.service.dto.ReportClassOption;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates textual reports by reusing the existing {@link BaoCaoThongKe} helpers.
 */
public class JdbcReportDocumentService {

    private static final Khoi PLACEHOLDER_KHOI = new Khoi("KH00000000", "Chưa xác định", 0);

    private static final String CLASS_OVERVIEW_SQL = """
        SELECT l.MaLopHoc, l.TenLopHoc,
               hs.MaHocSinh, hs.Ho, hs.TenLot, hs.Ten,
               hs.NgaySinh, hs.GioiTinh, hs.NgayNhapHoc,
               hsl.DiemThuongXuyen, hsl.DiemGiuaKy, hsl.DiemCuoiKy
        FROM LOPHOC l
        LEFT JOIN HOCSINH_LOPHOC hsl ON hsl.MaLopHoc = l.MaLopHoc
        LEFT JOIN HOCSINH hs ON hs.MaHocSinh = hsl.MaHocSinh
        WHERE (? IS NULL OR l.MaLopHoc = ?)
        ORDER BY l.TenLopHoc, hs.Ho, hs.TenLot, hs.Ten
        """;

    private static final String CLASS_LIST_SQL = "SELECT MaLopHoc, TenLopHoc FROM LOPHOC ORDER BY TenLopHoc";
    private static final String CLASS_LIST_BY_SEMESTER_SQL = "SELECT MaLopHoc, TenLopHoc FROM LOPHOC WHERE MaHocKy = ? ORDER BY TenLopHoc";
    private static final String STUDENT_SQL = "SELECT MaHocSinh, Ho, TenLot, Ten, NgaySinh, GioiTinh, NgayNhapHoc FROM HOCSINH";

    private final DatabaseManager databaseManager = DatabaseManager.getInstance();
    private final BaoCaoThongKe generator = new BaoCaoThongKe();

    public List<ReportClassOption> listClasses() {
        List<ReportClassOption> classes = new ArrayList<>();
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(CLASS_LIST_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("MaLopHoc");
                String name = rs.getString("TenLopHoc");
                classes.add(new ReportClassOption(id, safeTrim(name)));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải danh sách lớp phục vụ báo cáo", ex);
        }
        return classes;
    }

    public List<ReportClassOption> listClasses(String semesterId) {
        List<ReportClassOption> classes = new ArrayList<>();
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(CLASS_LIST_BY_SEMESTER_SQL)) {
            ps.setString(1, semesterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("MaLopHoc");
                    String name = rs.getString("TenLopHoc");
                    classes.add(new ReportClassOption(id, safeTrim(name)));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải danh sách lớp cho học kỳ " + semesterId, ex);
        }
        return classes;
    }

    public String buildStudentRosterReport() {
        List<LopHoc> classes = fetchClassesWithStudents(null);
        return generator.taoBaoCaoDanhSachHocVien(classes);
    }

    public String buildClassScoreReport(String classId) {
        if (classId == null || classId.isBlank()) {
            throw new IllegalArgumentException("Vui lòng chọn lớp cần xuất bảng điểm");
        }
        List<LopHoc> classes = fetchClassesWithStudents(classId.trim());
        return classes.stream()
            .findFirst()
            .map(generator::xuatBangDiemLop)
            .orElse("Lớp học chưa có dữ liệu đăng ký");
    }

    public String buildNewStudentReport(LocalDate from, LocalDate to) {
        LocalDateTime fromTime = (from == null ? LocalDate.MIN : from).atStartOfDay();
        LocalDateTime toTime = (to == null ? LocalDate.MAX : to).atTime(LocalTime.MAX);
        List<HocSinh> students = fetchStudents();
        return generator.thongKeHocVienMoi(students, fromTime, toTime);
    }

    public String buildExcellentStudentReport(double threshold) {
        if (threshold < 0 || threshold > 10) {
            throw new IllegalArgumentException("Điểm chuẩn phải nằm trong khoảng 0 - 10");
        }
        List<DangKyHoc> registrations = fetchRegistrations();
        return generator.thongKeHocVienXuatSac(registrations, (float) threshold);
    }

    private List<HocSinh> fetchStudents() {
        List<HocSinh> students = new ArrayList<>();
        try (PreparedStatement ps = databaseManager.getConnection().prepareStatement(STUDENT_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                students.add(mapStudent(rs));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải danh sách học sinh", ex);
        }
        return students;
    }

    private List<DangKyHoc> fetchRegistrations() {
        List<LopHoc> classes = fetchClassesWithStudents(null);
        List<DangKyHoc> registrations = new ArrayList<>();
        for (LopHoc lopHoc : classes) {
            registrations.addAll(lopHoc.getDanhSachDangKy());
        }
        return registrations;
    }

    private List<LopHoc> fetchClassesWithStudents(String classId) {
        Map<String, LopHoc> classMap = new LinkedHashMap<>();
        Connection connection = databaseManager.getConnection();
        try (PreparedStatement ps = connection.prepareStatement(CLASS_OVERVIEW_SQL)) {
            ps.setString(1, classId);
            ps.setString(2, classId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String lopId = rs.getString("MaLopHoc");
                    String lopName = safeTrim(rs.getString("TenLopHoc"));
                    LopHoc lopHoc = classMap.get(lopId);
                    if (lopHoc == null) {
                        lopHoc = new LopHoc(lopId, lopName, null, null, null, null, null, null);
                        classMap.put(lopId, lopHoc);
                    }
                    String studentId = rs.getString("MaHocSinh");
                    if (studentId == null || studentId.isBlank()) {
                        continue;
                    }
                    HocSinh hocSinh = mapStudent(rs);
                    DangKyHoc dangKyHoc = new DangKyHoc(hocSinh, lopHoc, PLACEHOLDER_KHOI);
                    Double tx = rs.getObject("DiemThuongXuyen") == null ? null : rs.getDouble("DiemThuongXuyen");
                    Double gk = rs.getObject("DiemGiuaKy") == null ? null : rs.getDouble("DiemGiuaKy");
                    Double ck = rs.getObject("DiemCuoiKy") == null ? null : rs.getDouble("DiemCuoiKy");
                    dangKyHoc.capNhatDiem(
                        tx == null ? null : tx.floatValue(),
                        gk == null ? null : gk.floatValue(),
                        ck == null ? null : ck.floatValue()
                    );
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể tải dữ liệu lớp học", ex);
        }
        return new ArrayList<>(classMap.values());
    }

    private HocSinh mapStudent(ResultSet rs) throws SQLException {
        String ho = safeTrim(rs.getString("Ho"));
        String tenLot = safeTrim(rs.getString("TenLot"));
        String ten = safeTrim(rs.getString("Ten"));
        LocalDate ngaySinh = toLocalDate(rs.getDate("NgaySinh"));
        LocalDate ngayNhapHoc = toLocalDate(rs.getDate("NgayNhapHoc"));
        String gioiTinh = safeTrim(rs.getString("GioiTinh"));
        HocSinh hocSinh = new HocSinh(
            rs.getString("MaHocSinh"),
            ho == null ? "" : ho,
            tenLot,
            ten == null ? "" : ten,
            ngaySinh,
            gioiTinh,
            ngayNhapHoc
        );
        return hocSinh;
    }

    private LocalDate toLocalDate(Date sqlDate) {
        return sqlDate == null ? null : sqlDate.toLocalDate();
    }

    private String safeTrim(String value) {
        return value == null ? null : value.trim();
    }
}
