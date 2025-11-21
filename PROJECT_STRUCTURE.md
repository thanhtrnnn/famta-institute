# Cấu trúc Dự án FAMTA Institute

Tài liệu này mô tả chi tiết cấu trúc thư mục và các thành phần chính của dự án FAMTA Institute Management System.

## Tổng quan Thư mục

```
famta-institute/
├── docs/                       # Tài liệu dự án (UML, SQL scripts, Wireframes)
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── famta/
│       │           ├── controller/     # Bộ điều khiển (JavaFX Controllers)
│       │           ├── database/       # Quản lý kết nối và khởi tạo CSDL
│       │           ├── model/          # Các lớp thực thể (Entities)
│       │           ├── service/        # Lớp dịch vụ (Business Logic)
│       │           ├── session/        # Quản lý phiên làm việc người dùng
│       │           ├── util/           # Các tiện ích chung
│       │           ├── view/           # Các tiện ích hiển thị
│       │           └── FamtaApplication.java  # Điểm khởi chạy ứng dụng
│       └── resources/
│           ├── config/         # File cấu hình (application.properties)
│           ├── css/            # Stylesheets cho giao diện
│           ├── fxml/           # Các file giao diện người dùng (.fxml)
│           │   └── screens/    # Các màn hình chức năng chi tiết
│           ├── icons/          # Biểu tượng ứng dụng
│           └── images/         # Hình ảnh tài nguyên
├── target/                     # Thư mục chứa kết quả build (không commit lên git)
├── pom.xml                     # Cấu hình Maven và dependencies
└── README.md                   # Hướng dẫn sử dụng và cài đặt
```

## Chi tiết Các Package

### `com.famta.controller`
Chứa các lớp điều khiển logic cho các màn hình giao diện.
- `MainController.java`: Điều hướng chính của ứng dụng (Sidebar, Header).
- `LoginController.java`: Xử lý đăng nhập.
- `DashboardController.java`: Màn hình tổng quan.
- `StudentManagementController.java`: Quản lý học sinh.
- `TeacherManagementController.java`: Quản lý giáo viên.
- `ClassManagementController.java`: Quản lý lớp học.
- `CourseManagementController.java`: Quản lý khóa học.
- `ScoreManagementController.java`: Quản lý điểm số (bao gồm tính năng Tải lại & Lưu nháp).
- `GuardianManagementController.java`: Quản lý phụ huynh (bao gồm tìm kiếm thời gian thực).
- `MasterDataController.java`: Quản lý danh mục (Năm học, Khoa, Môn học, Phòng học) với giao diện Master-Detail.
- `AccountManagementController.java`: Quản lý tài khoản hệ thống và phân quyền.
- `ReportsController.java`: Báo cáo thống kê.

### `com.famta.model`
Chứa các lớp POJO đại diện cho dữ liệu trong hệ thống.
- `HocSinh.java`, `GiaoVien.java`, `NguoiGiamHo.java`: Thông tin con người.
- `LopHoc.java`, `KhoaHoc.java`, `MonHoc.java`: Thông tin đào tạo.
- `DiemSo.java`, `BangDiem.java`: Thông tin kết quả học tập.
- `TaiKhoan.java`, `QuyenTruyCap.java`: Thông tin xác thực.
- `NamHoc.java`, `Khoa.java`, `PhongHoc.java`: Các danh mục dữ liệu.

### `com.famta.service`
Chứa logic nghiệp vụ và truy xuất dữ liệu (DAO/Repository pattern).
- `AccountService.java`, `AuthService.java`: Xử lý tài khoản và xác thực.
- `StudentService.java`, `TeacherService.java`: Nghiệp vụ quản lý người dùng.
- `ClassService.java`, `CourseService.java`: Nghiệp vụ đào tạo.
- `ScoreService.java`: Nghiệp vụ điểm số.
- `CatalogService.java`: Nghiệp vụ danh mục chung.
- Các implementation `Jdbc...Service.java` sử dụng JDBC để tương tác với database.

### `com.famta.database`
- `DatabaseManager.java`: Quản lý kết nối đến cơ sở dữ liệu (SQLite/MySQL).
- `DatabaseBootstrapper.java`: Khởi tạo bảng và dữ liệu mẫu khi chạy lần đầu.

### `com.famta.util`
- `SecurityContext.java`: Tiện ích bảo mật.
- `AlertUtils.java`: Tiện ích hiển thị thông báo.
- `ValidationUtils.java`: Tiện ích kiểm tra dữ liệu đầu vào.

## Tài nguyên (Resources)

### `fxml/`
- `login-view.fxml`: Màn hình đăng nhập.
- `main-view.fxml`: Khung giao diện chính (Layout).
- `screens/`: Chứa các file FXML cho từng tab chức năng (students.fxml, teachers.fxml, scores.fxml, master-data.fxml, accounts.fxml, ...).

### `css/`
- `application.css`: File style chính, định nghĩa màu sắc, font chữ và giao diện các component.

### `config/`
- `application.properties`: Cấu hình kết nối database và các tham số ứng dụng.

## Cơ sở dữ liệu
Thư mục `docs/` chứa các script SQL quan trọng:
- `Tao_bang.sql`: Script tạo cấu trúc bảng.
- `Nhap_du_lieu.sql`: Script chèn dữ liệu mẫu.
- `Truy_van_du_lieu.sql`: Các câu truy vấn mẫu.
