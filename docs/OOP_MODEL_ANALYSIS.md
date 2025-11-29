# OOP Analysis of FAMTA Institute Model

This document outlines the Object-Oriented Programming (OOP) principles applied in the `com.famta.model` package.

## OOP Principles
### 1. Abstraction

The `NguoiDung` class serves as an abstract base class for all user types in the system. It abstracts the common attributes and behaviors of a person, enforcing a contract for all user entities.

- **Class**: `com.famta.model.NguoiDung`
- **Abstracted Attributes**:
    - `ho`, `tenLot`, `ten`: Name components.
    - `taiKhoan`: User account (Composition).
- **Abstracted Behaviors**:
    - `getHoTenDayDu()`: Returns the full name.
    - Subclasses inherit these behaviors, ensuring consistent name formatting across the application.

### 2. Inheritance

The system utilizes inheritance to reuse code and establish a hierarchy among user entities. This promotes the "Don't Repeat Yourself" (DRY) principle.

- **Parent Class**: `NguoiDung`
- **Child Classes**:
    - `HocSinh` (Student): Extends `NguoiDung`. Adds student-specific attributes like `maHocSinh`, `ngaySinh`, `lopHoc`.
    - `GiaoVien` (Teacher): Extends `NguoiDung`. Adds teacher-specific attributes like `maGiaoVien`, `cacLopDangDay`.
    - `NguoiGiamHo` (Guardian): Extends `NguoiDung`. Adds guardian-specific attributes like `maNguoiGiamHo`, `quanHeVoiHocSinh`.

This structure eliminates code duplication for common fields (name, account) and ensures consistency.

### 3. Encapsulation

All model classes follow the encapsulation principle to protect internal state and ensure data integrity:
- **Private Fields**: All attributes are declared `private` to hide the internal state from direct external access.
- **Public Accessors**: Getters and Setters are provided to control access to the fields.
- **Constructor Validation**: Constructors enforce validity of essential fields (e.g., `Objects.requireNonNull`) to prevent invalid objects from being created.
- **Immutable Collections**: Lists returned by getters (e.g., `getCacLopDangDay`) are often wrapped in `Collections.unmodifiableList` to prevent external modification of the internal list structure, preserving the integrity of the collection.

### 4. Polymorphism

By using the `NguoiDung` base class, the system can treat different types of users uniformly where appropriate. This allows for flexible and extensible code.

- **Subtype Polymorphism**: Objects of `HocSinh`, `GiaoVien`, and `NguoiGiamHo` can be treated as `NguoiDung`.
- **Example Usage**:
```java
public void printUserName(NguoiDung user) {
    // Works for any subclass of NguoiDung
    System.out.println(user.getHoTenDayDu());
}
```

## Class Relationship

The model layer implements various relationships between classes to represent the domain accurately:

### 1. Composition (Strong "Has-A")
- **`NguoiDung` and `TaiKhoan`**: A `NguoiDung` (User) *has* a `TaiKhoan` (Account). The account is integral to the user's identity in the system.
- **`NamHoc` and `HocKy`**: A `NamHoc` (School Year) is composed of `HocKy` (Semesters). Semesters do not exist independently of a school year.

### 2. Aggregation (Weak "Has-A")
- **`LopHoc` and `HocSinh`**: A `LopHoc` (Class) contains a list of `HocSinh` (Students). Students can exist without the class (e.g., before assignment).
- **`Khoa` and `MonHoc`**: A `Khoa` (Department) manages a list of `MonHoc` (Subjects).

### 3. Association (Uses)
- **`LopHoc` and `GiaoVien`**: A `LopHoc` is associated with a `GiaoVien` (Teacher) who is in charge.
- **`LichHoc` and `PhongHoc`**: A `LichHoc` (Schedule) is associated with a `PhongHoc` (Room) where the class takes place.

### 4. Dependency
- **`BaoCaoThongKe`**: Depends on `HocSinh`, `LopHoc`, and `DangKyHoc` to generate statistical reports. It uses these classes but does not own them.
