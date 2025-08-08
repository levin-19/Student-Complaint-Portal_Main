package helpers;

import models.Student;

import java.sql.*;

public class StudentDAO {

    private boolean hasColumn(ResultSet rs, String columnLabel) {
        try {
            ResultSetMetaData md = rs.getMetaData();
            int count = md.getColumnCount();
            for (int i = 1; i <= count; i++) {
                if (columnLabel.equalsIgnoreCase(md.getColumnLabel(i))) return true;
            }
        } catch (SQLException ignored) {}
        return false;
    }

    public boolean insertStudent(Student student) {
        String sql = """
            INSERT INTO students 
              (name, student_id, department, mobile, email, password, profile_picture, profile_picture_mime) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String normalizedEmail = student.getEmail() != null ? student.getEmail().toLowerCase().trim() : null;

            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getStudentId());
            pstmt.setString(3, student.getDepartment());
            pstmt.setString(4, student.getMobile());
            pstmt.setString(5, normalizedEmail);
            pstmt.setString(6, student.getPassword());

            if (student.getProfilePicture() != null) {
                pstmt.setBytes(7, student.getProfilePicture());
                pstmt.setString(8, student.getProfilePictureMime());
            } else {
                pstmt.setNull(7, Types.BLOB);
                pstmt.setNull(8, Types.VARCHAR);
            }

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting student: " + e.getMessage());
            // Fallback for old schema without picture columns
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "INSERT INTO students (name, student_id, department, mobile, email, password) VALUES (?, ?, ?, ?, ?, ?)")) {
                String normalizedEmail = student.getEmail() != null ? student.getEmail().toLowerCase().trim() : null;
                pstmt.setString(1, student.getName());
                pstmt.setString(2, student.getStudentId());
                pstmt.setString(3, student.getDepartment());
                pstmt.setString(4, student.getMobile());
                pstmt.setString(5, normalizedEmail);
                pstmt.setString(6, student.getPassword());
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e2) {
                System.err.println("Fallback insert failed: " + e2.getMessage());
                return false;
            }
        }
    }

    public Student findStudentByEmail(String email) {
        String sql = "SELECT * FROM students WHERE email = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email.toLowerCase().trim());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Student student = new Student();
                student.setName(rs.getString("name"));
                student.setStudentId(rs.getString("student_id"));
                student.setDepartment(rs.getString("department"));
                student.setMobile(rs.getString("mobile"));
                student.setEmail(rs.getString("email"));
                student.setPassword(rs.getString("password"));

                if (hasColumn(rs, "profile_picture")) {
                    byte[] pic = rs.getBytes("profile_picture");
                    if (pic != null && pic.length > 0) {
                        student.setProfilePicture(pic);
                        if (hasColumn(rs, "profile_picture_mime")) {
                            student.setProfilePictureMime(rs.getString("profile_picture_mime"));
                        }
                    }
                }
                return student;
            }
        } catch (SQLException e) {
            System.err.println("Error finding student: " + e.getMessage());
        }
        return null;
    }

    public boolean updateStudent(Student student) {
        // First update basic info
        String sql = """
            UPDATE students 
               SET name = ?, student_id = ?, department = ?, mobile = ?
             WHERE email = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getStudentId());
            pstmt.setString(3, student.getDepartment());
            pstmt.setString(4, student.getMobile());
            pstmt.setString(5, student.getEmail().toLowerCase().trim());

            boolean basicUpdateSuccess = pstmt.executeUpdate() > 0;

            // Then update profile picture if present
            if (basicUpdateSuccess && student.getProfilePicture() != null) {
                updateStudentProfilePicture(student.getEmail(), student.getProfilePicture(), student.getProfilePictureMime());
            }

            return basicUpdateSuccess;

        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStudentProfilePicture(String email, byte[] picture, String mime) {
        // Try with picture columns; if fails, simply ignore (older schema)
        String sql = "UPDATE students SET profile_picture = ?, profile_picture_mime = ? WHERE email = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (picture != null) {
                pstmt.setBytes(1, picture);
                pstmt.setString(2, mime);
            } else {
                pstmt.setNull(1, Types.BLOB);
                pstmt.setNull(2, Types.VARCHAR);
            }
            pstmt.setString(3, email.toLowerCase().trim());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Skipping profile picture update (column missing): " + e.getMessage());
            return false;
        }
    }

    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM students WHERE email = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email.toLowerCase().trim());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
        }

        return false;
    }

    public boolean isStudentIdExists(String studentId) {
        String sql = "SELECT COUNT(*) FROM students WHERE student_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking student ID: " + e.getMessage());
        }

        return false;
    }
}
