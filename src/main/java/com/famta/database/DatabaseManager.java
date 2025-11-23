package com.famta.database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * Database connection manager tuned for SQL Server.
 *
 * <p>The manager loads connection details from {@code config/application.properties}
 * and keeps the schema in sync with the SQL scripts under the {@code docs/} folder.
 * Use {@link #initializeSchemaIfNeeded()} to create tables described in
 * {@code docs/Tao_bang.sql}, and {@link #seedSampleData()} to load the demo data
 * from {@code docs/Nhap_du_lieu.sql}.</p>
 */
public class DatabaseManager {

    private static final String CONFIG_RESOURCE = "/config/application.properties";
    private static final Path DEFAULT_DOCS_DIRECTORY = Path.of("docs");
    private static final String SCHEMA_SCRIPT = "Tao_bang.sql";
    private static final String SEED_SCRIPT = "Nhap_du_lieu.sql";
    private static final String DEFAULT_HEALTH_TABLE = "HOCSINH";

    private static DatabaseManager instance;

    private final String url;
    private final String driverClass;
    private final String username;
    private final String password;
    private final Path docsDirectory;
    private final String healthCheckTable;

    private Connection connection;

    private DatabaseManager() {
        Properties properties = loadConfiguration();
        url = properties.getProperty("database.url", "").trim();
        driverClass = properties.getProperty("database.driver", "").trim();
        username = properties.getProperty("database.username", "").trim();
        password = properties.getProperty("database.password", "").trim();
        String configuredDocsPath = properties.getProperty("database.docs.path", "").trim();
        docsDirectory = configuredDocsPath.isEmpty() ? DEFAULT_DOCS_DIRECTORY : Path.of(configuredDocsPath);
        healthCheckTable = properties.getProperty("database.health.table", DEFAULT_HEALTH_TABLE).trim();
        validateConfiguration();
        connect();
        initializeSchemaIfNeeded();
        ensureAccountTable();
        ensureSchemaUpdates();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private Properties loadConfiguration() {
        Properties properties = new Properties();
        try (InputStream inputStream = DatabaseManager.class.getResourceAsStream(CONFIG_RESOURCE)) {
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                System.err.println("Configuration file " + CONFIG_RESOURCE + " not found on classpath.");
            }
        } catch (IOException ex) {
            System.err.println("Failed to read configuration: " + ex.getMessage());
        }
        return properties;
    }

    private void validateConfiguration() {
        if (url.isEmpty()) {
            throw new IllegalStateException("database.url must be configured for SQL Server connection");
        }
    }

    private void connect() {
        try {
            if (!driverClass.isEmpty()) {
                Class.forName(driverClass);
            }
            if (username.isEmpty()) {
                connection = DriverManager.getConnection(url);
            } else {
                connection = DriverManager.getConnection(url, username, password);
            }
            System.out.println("Connected to database successfully.");
        } catch (SQLException | ClassNotFoundException e) {
            throw new IllegalStateException("Failed to establish database connection", e);
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to validate SQL connection", e);
        }
        return connection;
    }

    /**
     * Ensures the SQL Server schema matches the DDL defined in {@code docs/Tao_bang.sql}.
     * The script will only be executed when the anchor table (default {@code HOCSINH})
     * is missing.
     */
    public synchronized void initializeSchemaIfNeeded() {
        if (!tableExists(healthCheckTable)) {
            executeDocScript(SCHEMA_SCRIPT);
        }
    }

    private void ensureAccountTable() {
        if (tableExists("TAIKHOAN")) {
            return;
        }
        String createSql = """
            CREATE TABLE TAIKHOAN(
                TenDangNhap NVARCHAR(50) PRIMARY KEY,
                MatKhauHash NVARCHAR(88) NOT NULL,
                Quyen       NVARCHAR(20) NOT NULL
            )
            """;
        try (Statement statement = getConnection().createStatement()) {
            statement.execute(createSql);
            System.out.println("Created TAIKHOAN table for authentication module.");
        } catch (SQLException ex) {
            throw new IllegalStateException("Unable to create TAIKHOAN table", ex);
        }
    }

    private void ensureSchemaUpdates() {
        try (Statement stmt = getConnection().createStatement()) {
            // Check and add TenDangNhap to HOCSINH
            if (!columnExists("HOCSINH", "TenDangNhap")) {
                stmt.execute("ALTER TABLE HOCSINH ADD TenDangNhap NVARCHAR(50)");
                System.out.println("Added TenDangNhap column to HOCSINH table.");
            }
            // Check and add TenDangNhap to GIAOVIEN
            if (!columnExists("GIAOVIEN", "TenDangNhap")) {
                stmt.execute("ALTER TABLE GIAOVIEN ADD TenDangNhap NVARCHAR(50)");
                System.out.println("Added TenDangNhap column to GIAOVIEN table.");
            }
            // Check and add TenDangNhap to NGUOIGIAMHO
            if (!columnExists("NGUOIGIAMHO", "TenDangNhap")) {
                stmt.execute("ALTER TABLE NGUOIGIAMHO ADD TenDangNhap NVARCHAR(50)");
                System.out.println("Added TenDangNhap column to NGUOIGIAMHO table.");
            }
            
            // Populate TenDangNhap if null (Simple mapping for demo data)
            populateTenDangNhap(stmt);
            
        } catch (SQLException ex) {
            System.err.println("Failed to apply schema updates: " + ex.getMessage());
        }
    }

    private void populateTenDangNhap(Statement stmt) throws SQLException {
        // Students
        stmt.executeUpdate("UPDATE HOCSINH SET TenDangNhap = 'hs.anhpt' WHERE MaHocSinh = 'HS00000001' AND TenDangNhap IS NULL");
        stmt.executeUpdate("UPDATE HOCSINH SET TenDangNhap = 'hs.chaunb' WHERE MaHocSinh = 'HS00000002' AND TenDangNhap IS NULL");
        stmt.executeUpdate("UPDATE HOCSINH SET TenDangNhap = 'hs.khoilm' WHERE MaHocSinh = 'HS00000003' AND TenDangNhap IS NULL");
        stmt.executeUpdate("UPDATE HOCSINH SET TenDangNhap = 'hs.hongdt' WHERE MaHocSinh = 'HS00000004' AND TenDangNhap IS NULL");
        stmt.executeUpdate("UPDATE HOCSINH SET TenDangNhap = 'hs.phuctg' WHERE MaHocSinh = 'HS00000005' AND TenDangNhap IS NULL");
        stmt.executeUpdate("UPDATE HOCSINH SET TenDangNhap = 'hs.linhvk' WHERE MaHocSinh = 'HS00000006' AND TenDangNhap IS NULL");

        // Teachers
        stmt.executeUpdate("UPDATE GIAOVIEN SET TenDangNhap = 'gv.thipd' WHERE MaGiaoVien = 'GV00000001' AND TenDangNhap IS NULL");
        stmt.executeUpdate("UPDATE GIAOVIEN SET TenDangNhap = 'gv.hoangnv' WHERE MaGiaoVien = 'GV00000002' AND TenDangNhap IS NULL");
        stmt.executeUpdate("UPDATE GIAOVIEN SET TenDangNhap = 'gv.lantt' WHERE MaGiaoVien = 'GV00000003' AND TenDangNhap IS NULL");
        stmt.executeUpdate("UPDATE GIAOVIEN SET TenDangNhap = 'gv.minhlq' WHERE MaGiaoVien = 'GV00000004' AND TenDangNhap IS NULL");

        // Guardians
        stmt.executeUpdate("UPDATE NGUOIGIAMHO SET TenDangNhap = 'ngh.thanhtx' WHERE MaNguoiGiamHo = 'GH00000001' AND TenDangNhap IS NULL");
        stmt.executeUpdate("UPDATE NGUOIGIAMHO SET TenDangNhap = 'ngh.mailt' WHERE MaNguoiGiamHo = 'GH00000002' AND TenDangNhap IS NULL");
        stmt.executeUpdate("UPDATE NGUOIGIAMHO SET TenDangNhap = 'ngh.phucnd' WHERE MaNguoiGiamHo = 'GH00000003' AND TenDangNhap IS NULL");
        stmt.executeUpdate("UPDATE NGUOIGIAMHO SET TenDangNhap = 'ngh.huongpt' WHERE MaNguoiGiamHo = 'GH00000004' AND TenDangNhap IS NULL");
        stmt.executeUpdate("UPDATE NGUOIGIAMHO SET TenDangNhap = 'ngh.ducdm' WHERE MaNguoiGiamHo = 'GH00000005' AND TenDangNhap IS NULL");
        stmt.executeUpdate("UPDATE NGUOIGIAMHO SET TenDangNhap = 'ngh.thanhvt' WHERE MaNguoiGiamHo = 'GH00000006' AND TenDangNhap IS NULL");
    }

    private boolean columnExists(String tableName, String columnName) throws SQLException {
        DatabaseMetaData meta = getConnection().getMetaData();
        try (ResultSet rs = meta.getColumns(null, null, tableName, columnName)) {
            return rs.next();
        }
    }

    /**
     * Applies the seed data contained in {@code docs/Nhap_du_lieu.sql}.
     * This is idempotent as long as the script itself is safe to rerun.
     */
    public synchronized void seedSampleData() {
        executeDocScript(SEED_SCRIPT);
    }

    /**
     * Executes a SQL script stored in the {@code docs/} directory.
     *
     * @param scriptFileName the file to execute, e.g. {@code Tao_bang.sql}
     */
    public synchronized void executeDocScript(String scriptFileName) {
        Objects.requireNonNull(scriptFileName, "scriptFileName");
        Path scriptPath = docsDirectory.resolve(scriptFileName);
        if (!Files.exists(scriptPath)) {
            System.err.println("SQL script not found: " + scriptPath.toAbsolutePath());
            return;
        }
        try {
            String script = Files.readString(scriptPath, StandardCharsets.UTF_8);
            executeScript(script, scriptFileName);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read SQL script: " + scriptPath, ex);
        }
    }

    private void executeScript(String scriptContent, String origin) {
        List<String> statements = parseStatements(scriptContent);
        if (statements.isEmpty()) {
            System.out.println("No executable statements found in " + origin);
            return;
        }
        Connection conn = getConnection();
        boolean previousAutoCommit;
        try {
            previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to switch SQL connection to transactional mode", e);
        }

        try (Statement statement = conn.createStatement()) {
            for (String sql : statements) {
                String trimmed = sql.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                statement.execute(trimmed);
            }
            conn.commit();
            System.out.println("Executed script " + origin + " successfully (" + statements.size() + " statements).");
        } catch (SQLException ex) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.addSuppressed(ex);
                throw new IllegalStateException("Failed to execute script and rollback", rollbackEx);
            }
            throw new IllegalStateException("Failed to execute script " + origin, ex);
        } finally {
            try {
                conn.setAutoCommit(previousAutoCommit);
            } catch (SQLException e) {
                System.err.println("Failed to restore auto-commit: " + e.getMessage());
            }
        }
    }

    private boolean tableExists(String tableName) {
        String normalized = Optional.ofNullable(tableName).map(name -> name.trim().toUpperCase(Locale.ROOT)).orElse("");
        if (normalized.isEmpty()) {
            return false;
        }
        try {
            DatabaseMetaData metaData = getConnection().getMetaData();
            try (ResultSet resultSet = metaData.getTables(null, null, normalized, new String[] { "TABLE" })) {
                if (resultSet.next()) {
                    return true;
                }
            }
            try (ResultSet lowerCase = metaData.getTables(null, null, normalized.toLowerCase(Locale.ROOT), new String[] { "TABLE" })) {
                return lowerCase.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to inspect existing tables", e);
        }
    }

    private List<String> parseStatements(String scriptContent) {
        String sanitized = scriptContent.replaceAll("(?m)^\\s*--.*$", "");
        sanitized = sanitized.replaceAll("(?mi)^\\s*GO\\s*$", "");

        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean singleQuote = false;
        boolean doubleQuote = false;

        for (int i = 0; i < sanitized.length(); i++) {
            char c = sanitized.charAt(i);
            if (c == '\'' && !doubleQuote) {
                singleQuote = !singleQuote;
            } else if (c == '"' && !singleQuote) {
                doubleQuote = !doubleQuote;
            }

            if (c == ';' && !singleQuote && !doubleQuote) {
                String statement = current.toString().trim();
                if (!statement.isBlank()) {
                    statements.add(statement);
                }
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        String tail = current.toString().trim();
        if (!tail.isBlank()) {
            statements.add(tail);
        }

        return statements;
    }

    public void closeConnection() {
        if (connection == null) {
            return;
        }
        try {
            if (!connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}