# FAMTA Institute Management System

## Giới thiệu
FAMTA Institute Management System là một giải pháp phần mềm toàn diện được xây dựng bằng JavaFX, nhằm mục đích số hóa và tối ưu hóa quy trình quản lý tại các trung tâm đào tạo. Hệ thống cung cấp các công cụ mạnh mẽ để quản lý học sinh, giáo viên, lớp học, điểm số và báo cáo thống kê.

## Mục lục
- [Giới thiệu](#giới-thiệu)
- [Tính năng Nổi bật](#tính-năng-nổi-bật)
- [Công nghệ Sử dụng](#công-nghệ-sử-dụng)
- [Yêu cầu Hệ thống](#yêu-cầu-hệ-thống)
- [Kiến trúc Ứng dụng](#kiến-trúc-ứng-dụng)
- [Cài đặt và Chạy ứng dụng](#cài-đặt-và-chạy-ứng-dụng)
- [Hướng dẫn Sử dụng nhanh](#hướng-dẫn-sử-dụng-nhanh)
- [Cấu trúc Dự án](#cấu-trúc-dự-án)
- [Đóng góp](#đóng-góp)
- [License](#license)

## Tính năng Nổi bật

### 1. Quản lý Đào tạo
- **Học sinh**: Quản lý hồ sơ chi tiết, lịch sử học tập.
- **Giáo viên**: Quản lý thông tin, chuyên môn và lịch giảng dạy.
- **Lớp học & Khóa học**: Tổ chức lớp học, phân công giáo viên và xếp lịch.
- **Danh mục (Master Data)**: Quản lý tập trung các danh mục dùng chung (Năm học, Khoa, Môn học, Phòng học) với giao diện Master-Detail trực quan.

### 2. Quản lý Điểm số & Học vụ
- **Nhập điểm**: Giao diện nhập điểm linh hoạt theo lớp và môn học.
- **Tính năng mở rộng**:
    - **Tải lại (Reload)**: Cập nhật dữ liệu mới nhất từ hệ thống.
    - **Lưu nháp (Save Draft)**: Xuất bảng điểm hiện tại ra file CSV để lưu trữ cục bộ.

### 3. Tiện ích Người dùng
- **Tìm kiếm thông minh**: Tìm kiếm Phụ huynh và Học sinh theo thời gian thực (Search-as-you-type).
- **Quản lý Tài khoản**: Phân quyền chi tiết cho Admin, Giáo viên, và Nhân viên.
- **Báo cáo**: Xuất các báo cáo thống kê về tình hình học tập và giảng dạy.

## Yêu cầu Hệ thống
- **Java Development Kit (JDK)**: Phiên bản 17 trở lên.
- **Maven**: Phiên bản 3.6 trở lên.
- **Cơ sở dữ liệu**: Microsoft SQL Server 2019 hoặc mới hơn.

## Công nghệ Sử dụng

| Thành phần   | Công nghệ          | Phiên bản   |
|--------------|--------------------|-------------|
| Backend      | Java               | 17          |
| GUI          | JavaFX             | 21.0.1      |
| UI Markup    | FXML               | 21          |
| Styling      | CSS                | -           |
| Database     | Microsoft SQL Server | 2022      |
| JDBC Driver  | mssql-jdbc         | 12.6.1      |
| Build Tool   | Maven              | 3.6+        |

## Kiến trúc Ứng dụng

```
┌──────────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER                          │
│   ┌───────────────┐  ┌───────────────┐  ┌───────────────┐        │
│   │  FXML Views   │  │  Controllers  │  │  CSS Styles   │        │
│   │ (*.fxml)      │◄─┤  (JavaFX)     │  │ (*.css)       │        │
│   └───────────────┘  └───────┬───────┘  └───────────────┘        │
└──────────────────────────────┼───────────────────────────────────┘
                               │
┌──────────────────────────────▼───────────────────────────────────┐
│                        SERVICE LAYER                             │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │  • AuthService: Xác thực đăng nhập người dùng              │  │
│  │  • DashboardService: Tổng hợp số liệu thống kê tổng quan   │  │
│  │  • ScoreService: Tra cứu và cập nhật điểm theo lớp         │  │
│  │  • CatalogService: CRUD danh mục (Năm học, Khoa, Môn, ...) │  │
│  │  • ClassService: Quản lý lớp học và thời khóa biểu         │  │
│  │  • GuardianService: Quản lý thông tin phụ huynh            │  │
│  │  • ReportService: Tạo báo cáo và phân tích điểm số         │  │
│  │  • AccountAdminService: Quản trị tài khoản hệ thống        │  │
│  └───────────────────────────┬────────────────────────────────┘  │
└──────────────────────────────┼───────────────────────────────────┘
                               │
┌──────────────────────────────▼───────────────────────────────────┐
│                      DATA ACCESS LAYER                           │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │              JDBC Repositories (JDBC Queries)              │  │
│  └───────────────────────────┬────────────────────────────────┘  │
│                              │                                   │
│  ┌───────────────────────────▼────────────────────────────────┐  │
│  │              DatabaseManager (Connection Pool)             │  │
│  └───────────────────────────┬────────────────────────────────┘  │
└──────────────────────────────┼───────────────────────────────────┘
                               │
┌──────────────────────────────▼───────────────────────────────────┐
│                   MODEL/ ENTITY (POJO Classes)                   │
└──────────────────────────────┼───────────────────────────────────┘
                               │
┌──────────────────────────────▼───────────────────────────────────┐
│                       SQL SERVER DATABASE                        │
└──────────────────────────────────────────────────────────────────┘
```

## Cài đặt và Chạy ứng dụng

### 1. Clone dự án
```bash
git clone https://github.com/thanhtrnnn/famta-institute.git
cd famta-institute
```

### 2. Cấu hình Database
Dự án sử dụng **Microsoft SQL Server**. Bạn cần:
1. Cài đặt SQL Server 2019 hoặc mới hơn.
2. Tạo database mới và chạy các script truy vấn SQL trong thư mục `docs/`:
   - `docs/Tao_bang.sql`: Tạo cấu trúc bảng.
   - `docs/Nhap_du_lieu.sql`: Thêm dữ liệu mẫu.
3. Cập nhật thông tin kết nối trong file `src/main/resources/config/application.properties`:
   ```properties
   db.url=jdbc:sqlserver://localhost:1433;databaseName=FAMTAInstitute;encrypt=true;trustServerCertificate=true
   database.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
   db.username=your_username
   db.password=your_password
   ```

### 3. Build và Chạy
Sử dụng Maven để build và chạy ứng dụng:

```bash
mvn clean compile
mvn javafx:run
```

## Hướng dẫn Sử dụng

### Đăng nhập
Khởi động ứng dụng và nhập thông tin tài khoản. Tài khoản mặc định được tạo sẵn trong `docs/Nhap_du_lieu.sql`.

### Dashboard
Màn hình tổng quan hiển thị số lượng học sinh, giáo viên, lớp học đang hoạt động cùng các thông báo quan trọng.

### Danh mục
Thiết lập dữ liệu nền tảng trước khi sử dụng các chức năng khác:
- **Năm học**: Mã, tên, ngày bắt đầu/kết thúc
- **Khoa**: Mã, tên khoa
- **Môn học**: Mã, tên môn
- **Phòng học**: Mã, tên phòng, loại phòng

### Lớp học
Tạo lớp mới với thông tin: tên lớp, môn học, giáo viên phụ trách, phòng học và lịch học. Có thể xem danh sách học sinh trong từng lớp.

### Học sinh
Quản lý hồ sơ học sinh bao gồm thông tin cá nhân, lớp học đang theo và lịch sử học tập.

### Điểm số
- Chọn lớp cần nhập điểm từ danh sách
- Nhập điểm thường xuyên, giữa kỳ, cuối kỳ cho từng học sinh
- **Tải lại**: Cập nhật dữ liệu mới nhất từ database
- **Lưu nháp**: Xuất bảng điểm hiện tại ra file CSV

### Tài khoản
Quản lý tài khoản người dùng hệ thống với các quyền: Admin, Giáo viên, Người giám hộ.

## Cấu trúc Dự án
Xem chi tiết tại file [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md).

## Đóng góp
Mọi đóng góp đều được hoan nghênh. Vui lòng tạo Pull Request hoặc mở Issue để thảo luận về các thay đổi.

## License
Dự án này được phân phối dưới giấy phép MIT. Xem file `LICENSE` để biết thêm chi tiết.