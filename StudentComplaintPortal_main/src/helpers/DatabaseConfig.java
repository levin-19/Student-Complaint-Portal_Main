package helpers;

import java.sql.*;

public class DatabaseConfig {
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "student_complaint_portal";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME +
            "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
    private static final String DB_URL_WITHOUT_DB = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT +
            "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }

    public static void initializeDatabase() {
        System.out.println("🔄 Initializing database...");

        // Ensure database exists
        try (Connection conn = DriverManager.getConnection(DB_URL_WITHOUT_DB, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            System.out.println("✅ Database ensured");
        } catch (SQLException e) {
            System.err.println("❌ Error creating database: " + e.getMessage());
            return;
        }

        // Create tables with proper status enum
        createTables();
    }

    private static void createTables() {
        String createStudentsTable = """
            CREATE TABLE IF NOT EXISTS students (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                student_id VARCHAR(50) UNIQUE NOT NULL,
                department VARCHAR(100) NOT NULL,
                mobile VARCHAR(15) NOT NULL,
                email VARCHAR(100) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                profile_picture LONGBLOB NULL,
                profile_picture_mime VARCHAR(100) NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                INDEX idx_email (email),
                INDEX idx_student_id (student_id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        """;

        // FIXED: Proper status enum with exact values
        String createComplaintsTable = """
            CREATE TABLE IF NOT EXISTS complaints (
                id INT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(200) NOT NULL,
                description TEXT NOT NULL,
                category ENUM('Academic', 'Facility', 'Admin', 'Other') NOT NULL,
                status ENUM('Pending', 'In Progress', 'Resolved') DEFAULT 'Pending',
                feedback TEXT NULL,
                feedback_updated_at TIMESTAMP NULL DEFAULT NULL,
                student_email VARCHAR(100) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                INDEX idx_student_email (student_email),
                INDEX idx_status (status),
                INDEX idx_category (category),
                FOREIGN KEY (student_email) REFERENCES students(email) ON DELETE CASCADE ON UPDATE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(createStudentsTable);
            stmt.executeUpdate(createComplaintsTable);
            System.out.println("✅ Tables created successfully");

            // Fix existing status column if needed
            fixStatusColumn(stmt);
            
            insertSampleData(stmt);
        } catch (SQLException e) {
            System.err.println("❌ Error creating tables: " + e.getMessage());
        }
    }

    private static void fixStatusColumn(Statement stmt) {
        try {
            // Update the status column to ensure it has the correct ENUM values
            stmt.executeUpdate("ALTER TABLE complaints MODIFY COLUMN status ENUM('Pending', 'In Progress', 'Resolved') DEFAULT 'Pending'");
            System.out.println("✅ Status column updated with correct ENUM values");
        } catch (SQLException e) {
            System.out.println("ℹ️ Status column already correct or table doesn't exist yet");
        }
    }

    private static void insertSampleData(Statement stmt) {
        try {
            // Check if sample data already exists
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM students WHERE email = 'test@student.com'");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate("""
                    INSERT INTO students (name, student_id, department, mobile, email, password) 
                    VALUES ('Test Student', 'TEST001', 'Computer Science', '1234567890', 'test@student.com', 'test123')
                """);
                System.out.println("✅ Sample student inserted");
            }
            
            rs = stmt.executeQuery("SELECT COUNT(*) FROM complaints");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate("""
                    INSERT INTO complaints (title, description, category, student_email, status) 
                    VALUES 
                    ('Sample Complaint', 'This is a sample complaint', 'Academic', 'test@student.com', 'Pending'),
                    ('Course Registration Issue', 'Registration blocked due to error', 'Academic', 'test@student.com', 'In Progress'),
                    ('Library Access Problem', 'Cannot access library resources', 'Admin', 'test@student.com', 'Resolved')
                """);
                System.out.println("✅ Sample complaints inserted");
            }
        } catch (SQLException e) {
            System.out.println("ℹ️ Sample data insertion skipped or already present");
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("✅ Database connection successful");
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            return false;
        }
    }
}
