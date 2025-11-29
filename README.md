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

Hệ thống tuân theo mô hình **MVC (Model-View-Controller)**, đảm bảo sự phân tách rõ ràng giữa giao diện, logic và dữ liệu.

### Sơ đồ Kiến trúc

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
│                 BUSINESS & DATA ACCESS LAYER                     │
│  ┌───────────────────────────┐  ┌─────────────────────────────┐  │
│  │    Service Interfaces     │◄─┤    JDBC Implementations     │  │
│  │    (Contracts)            │  │    (Logic & SQL Queries)    │  │
│  └───────────────────────────┘  └──────────────┬──────────────┘  │
└────────────────────────────────────────────────┼─────────────────┘
                                                 │
                                  ┌──────────────▼──────────────┐
                                  │      DatabaseManager        │
                                  │      (Connection Pool)      │
                                  └──────────────┬──────────────┘
                                                 │
┌────────────────────────────────────────────────▼─────────────────┐
│                       SQL SERVER DATABASE                        │
└──────────────────────────────────────────────────────────────────┘
```

### Các thành phần chính

1. **Presentation Layer (View & Controller)**
   - **FXML**: Định nghĩa cấu trúc giao diện người dùng.
   - **CSS**: Định nghĩa giao diện và chủ đề (Theme).
   - **Controller**: Xử lý sự kiện từ người dùng, điều hướng và cập nhật View.

2. **Business Logic & Data Access Layer (Service)**
   - Nằm trong `com.famta.service`.
   - Kết hợp logic nghiệp vụ và truy cập dữ liệu (DAO pattern).
   - **Interfaces**: Định nghĩa các chức năng (ví dụ: `ScoreService`).
   - **Implementations**: Thực thi các truy vấn JDBC trực tiếp (ví dụ: `JdbcScoreService`).

3. **Domain Model**
   - Các lớp POJO trong `com.famta.model` đại diện cho các thực thể.

4. **Infrastructure**
   - **DatabaseManager**: Singleton quản lý kết nối đến SQL Server.
   - **UserSession**: Quản lý trạng thái đăng nhập.
   - **SecurityContext**: Kiểm soát phân quyền (Role-based Access Control).

## Cài đặt và Chạy ứng dụng

### 1. Clone dự án
```bash
git clone https://github.com/thanhtrnnn/famta-institute.git
cd famta-institute
```

### 2. Cấu hình Database
Dự án sử dụng **Microsoft SQL Server**. Bạn cần thực hiện các bước sau để thiết lập môi trường cơ sở dữ liệu. Hướng dẫn này dành cho người dùng Windows.

#### Bước 1: Cài đặt và Cấu hình SQL Server
1. **Cài đặt SQL Server**<br>
Tải và cài đặt SQL Server 2019 (hoặc phiên bản mới hơn) từ [trang chủ Microsoft](https://www.microsoft.com/en-us/sql-server/sql-server-downloads). Trong quá trình cài đặt, hãy chọn **Mixed Mode Authentication** để hỗ trợ đăng nhập bằng cả tài khoản Windows và tài khoản SQL Server (sa).

2. **Kiểm tra cổng (Port)**
   - Mở Command Prompt (cmd) hoặc PowerShell và chạy lệnh:
     ```bash
     netstat -ano | findstr :1433
     ```
   - Nếu cổng 1433 chưa bị chiếm (không có kết quả trả về), bạn có thể sử dụng cổng mặc định này.
   - Nếu cổng đã bị chiếm, hãy chọn một cổng khác chưa sử dụng (ví dụ: 1434).

3. **Cấu hình TCP/IP**
   - Mở **SQL Server Configuration Manager** (Tìm kiếm trong Windows hoặc mở Run > `SQLServerManager15.msc` cho bản 2019, `SQLServerManager16.msc` cho bản 2022).
   - Mở rộng mục **SQL Server Network Configuration**.
   - Chọn **Protocols for <your_instance_name>**.
   - Chuột phải vào **TCP/IP** và chọn **Enable**.
   - Chuột phải vào **TCP/IP** lần nữa, chọn **Properties**, chuyển sang tab **IP Addresses**. Kéo xuống phần **IPAll**, đặt **TCP Port** thành `1433` (hoặc cổng bạn đã chọn ở bước trên).
   - Sau khi thay đổi, vào mục **SQL Server Services**, chuột phải vào **SQL Server (<your_instance_name>)** và chọn **Restart** để áp dụng.

4. **Tạo tài khoản đăng nhập (Login)**
   - Mở **SQL Server Management Studio (SSMS)** và kết nối bằng Windows Authentication.
   - Trong Object Explorer, mở rộng **Security** > **Logins**.
   - Chuột phải vào **Logins** > **New Login...**.
   - Nhập **Login name** (ví dụ: `famta_user`), chọn **SQL Server authentication**, và nhập mật khẩu.
   - Trong trang **Server Roles**, tích chọn `sysadmin` (để cấp quyền đầy đủ cho môi trường dev) hoặc cấu hình quyền hạn hẹp hơn tùy nhu cầu.
   - Nhấn **OK** để tạo.

#### Bước 2: Tạo Database và Dữ liệu mẫu
1. Mở SSMS, ngắt kết nối hiện tại và kết nối lại bằng **SQL Server Authentication** với tài khoản vừa tạo.
2. Mở file `docs/Tao_bang.sql` trong SSMS và nhấn **Execute** (F5) để tạo cấu trúc bảng.
3. Mở file `docs/Nhap_du_lieu.sql` và nhấn **Execute** (F5) để thêm dữ liệu mẫu.

#### Bước 3: Kiểm tra kết nối
Mở terminal và chạy lệnh sau để kiểm tra kết nối đến database (thay thế `<username>`, `<password>` và cổng `1433` nếu cần):

```bash
sqlcmd -S tcp:localhost,1433 -U <username> -P <password> -d FAMTAInstitute -Q "SELECT CURRENT_USER"
```
Nếu kết quả trả về tên người dùng hiện tại, kết nối đã thành công.

#### Bước 4: Cập nhật cấu hình ứng dụng
Cập nhật thông tin kết nối trong file `src/main/resources/config/application.properties`:
   ```properties
   db.url=jdbc:sqlserver://localhost:1433;databaseName=FAMTAInstitute;encrypt=true;trustServerCertificate=true
   database.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
   db.username=<your_username>
   db.password=<your_password>
   ```
   Tham khảo file `example.application.properties` để biết thêm cấu hình mẫu.

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