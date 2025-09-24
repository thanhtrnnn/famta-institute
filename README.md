# FAMTA Institute Management System

## Mô tả
FAMTA Institute Management System là một ứng dụng JavaFX được thiết kế để quản lý trung tâm đào tạo, bao gồm quản lý học viên, giáo viên, lớp học, khóa học, điểm số và tài khoản người dùng.

## Tính năng chính
- **Quản lý học viên**: Đăng ký, theo dõi thông tin và điểm số học viên
- **Quản lý giáo viên**: Quản lý thông tin giáo viên và lịch giảng dạy
- **Quản lý lớp học**: Tạo và quản lý các lớp học, phân công giáo viên
- **Quản lý khóa học**: Tạo và quản lý các khóa học khác nhau
- **Quản lý điểm số**: Nhập và theo dõi điểm số của học viên
- **Hệ thống tài khoản**: Xác thực và phân quyền người dùng

## Cấu trúc dự án
```
famta-institute/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── famta/
│       │           ├── model/          # Các class entity
│       │           ├── controller/     # Controllers cho JavaFX
│       │           ├── view/           # View utilities
│       │           ├── service/        # Business logic
│       │           ├── database/       # Database access
│       │           └── util/           # Utility classes
│       └── resources/
│           ├── fxml/              # FXML files
│           ├── css/               # CSS stylesheets
│           ├── images/            # Hình ảnh
│           ├── icons/             # Icon files
│           └── config/            # Configuration files
├── target/                        # Maven build output
├── pom.xml                       # Maven configuration
├── .gitignore                    # Git ignore rules
└── README.md                     # This file
```

## Công nghệ sử dụng
- **Java 17**: Ngôn ngữ lập trình chính
- **JavaFX 21**: Framework UI
- **Maven**: Build tool và dependency management
- **SQLite**: Database để lưu trữ dữ liệu
- **FXML**: Định nghĩa UI layout
- **CSS**: Styling cho giao diện
- **JUnit 5**: Testing framework
- **TestFX**: Testing framework cho JavaFX

## Yêu cầu hệ thống
- Java Development Kit (JDK) 17 hoặc cao hơn
- Maven 3.6+ (hoặc sử dụng Maven wrapper có sẵn)
- JavaFX Runtime (được bao gồm trong dependencies)

## Cách chạy ứng dụng

### 1. Clone repository
```bash
git clone https://github.com/thanhtrnnn/famta-institute.git
cd famta-institute
```

### 2. Build project
```bash
mvn clean compile
```

### 3. Chạy ứng dụng
```bash
mvn javafx:run
```

### 4. Chạy với Maven wrapper (nếu có)
```bash
./mvnw javafx:run
```

## Lệnh Maven hữu ích

### Compile code
```bash
mvn compile
```

### Chạy tests
```bash
mvn test
```

### Package application
```bash
mvn package
```

### Clean build artifacts
```bash
mvn clean
```

### Chạy ứng dụng với debug mode
```bash
mvn javafx:run@debug
```

## Cấu trúc Database
Ứng dụng sử dụng SQLite database với các bảng chính:
- `hoc_vien`: Thông tin học viên
- `giao_vien`: Thông tin giáo viên
- `lop_hoc`: Thông tin lớp học
- `khoa_hoc`: Thông tin khóa học
- `diem_so`: Điểm số của học viên
- `tai_khoan`: Tài khoản người dùng

## Phân quyền người dùng
- **ADMIN**: Toàn quyền quản trị hệ thống
- **GIAO_VU**: Quản lý học vụ, khóa học, lớp học
- **GIAO_VIEN**: Quản lý lớp học được phân công, nhập điểm
- **KE_TOAN**: Quản lý học phí, báo cáo tài chính
- **PHU_HUYNH**: Xem thông tin và điểm số con em
- **HOC_VIEN**: Xem thông tin cá nhân và điểm số

## Đóng góp
1. Fork repository này
2. Tạo feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Tạo Pull Request

## Giấy phép
Dự án này được phân phối dưới giấy phép MIT. Xem file `LICENSE` để biết thêm chi tiết.

## Ghi chú phát triển
- Đảm bảo code tuân theo coding conventions
- Viết unit tests cho các chức năng mới
- Sử dụng JavaDoc cho documentation
- Tuân theo pattern MVC trong JavaFX