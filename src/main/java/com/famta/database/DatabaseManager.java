package com.famta.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database connection manager for SQLite
 */
public class DatabaseManager {
    
    private static final String DATABASE_URL = "jdbc:sqlite:famta_institute.db";
    private static DatabaseManager instance;
    private Connection connection;
    
    private DatabaseManager() {
        connect();
        createTables();
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    private void connect() {
        try {
            connection = DriverManager.getConnection(DATABASE_URL);
            System.out.println("Connected to SQLite database successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
        }
    }
    
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection: " + e.getMessage());
            connect();
        }
        return connection;
    }
    
    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            // Create hoc_vien table
            String createHocVienTable = """
                CREATE TABLE IF NOT EXISTS hoc_vien (
                    ma_hoc_vien TEXT PRIMARY KEY,
                    ho_ten TEXT NOT NULL,
                    ngay_sinh TEXT,
                    gioi_tinh TEXT,
                    dia_chi TEXT,
                    so_dien_thoai TEXT,
                    email TEXT,
                    thong_tin_nguoi_giam_ho TEXT,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
                );
                """;
            stmt.execute(createHocVienTable);
            
            // Create giao_vien table
            String createGiaoVienTable = """
                CREATE TABLE IF NOT EXISTS giao_vien (
                    ma_giao_vien TEXT PRIMARY KEY,
                    ho_ten TEXT NOT NULL,
                    ngay_sinh TEXT,
                    dia_chi TEXT,
                    so_dien_thoai TEXT,
                    email TEXT,
                    chuyen_mon TEXT,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
                );
                """;
            stmt.execute(createGiaoVienTable);
            
            // Create khoa_hoc table
            String createKhoaHocTable = """
                CREATE TABLE IF NOT EXISTS khoa_hoc (
                    ma_khoa_hoc TEXT PRIMARY KEY,
                    ten_khoa_hoc TEXT NOT NULL,
                    mo_ta TEXT,
                    hoc_phi REAL,
                    thoi_luong INTEGER,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
                );
                """;
            stmt.execute(createKhoaHocTable);
            
            // Create lop_hoc table
            String createLopHocTable = """
                CREATE TABLE IF NOT EXISTS lop_hoc (
                    ma_lop TEXT PRIMARY KEY,
                    ten_lop TEXT NOT NULL,
                    ma_khoa_hoc TEXT,
                    ma_giao_vien TEXT,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (ma_khoa_hoc) REFERENCES khoa_hoc (ma_khoa_hoc),
                    FOREIGN KEY (ma_giao_vien) REFERENCES giao_vien (ma_giao_vien)
                );
                """;
            stmt.execute(createLopHocTable);
            
            // Create diem_so table
            String createDiemSoTable = """
                CREATE TABLE IF NOT EXISTS diem_so (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    ma_hoc_vien TEXT,
                    ma_mon_hoc TEXT,
                    diem REAL,
                    loai_diem TEXT,
                    ghi_chu TEXT,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (ma_hoc_vien) REFERENCES hoc_vien (ma_hoc_vien)
                );
                """;
            stmt.execute(createDiemSoTable);
            
            // Create tai_khoan table
            String createTaiKhoanTable = """
                CREATE TABLE IF NOT EXISTS tai_khoan (
                    ten_dang_nhap TEXT PRIMARY KEY,
                    mat_khau TEXT NOT NULL,
                    quyen_truy_cap TEXT NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
                );
                """;
            stmt.execute(createTaiKhoanTable);
            
            System.out.println("Database tables created successfully.");
            
        } catch (SQLException e) {
            System.err.println("Failed to create tables: " + e.getMessage());
        }
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}