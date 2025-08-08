package helpers;

import models.Student;
import java.sql.*;

public class StudentDAO {

    public boolean insertStudent(Student student) {
        String sql = "INSERT INTO students (name, student_id, department, mobile, email, password) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String normalizedEmail = student.getEmail() != null ? student.getEmail().toLowerCase().trim() : null;

            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getStudentId());
            pstmt.setString(3, student.getDepartment());
            pstmt.setString(4, student.getMobile());
            pstmt.setString(5, normalizedEmail);
            pstmt.setString(6, student.getPassword());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting student: " + e.getMessage());
            return false;
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
                return student;
            }
        } catch (SQLException e) {
            System.err.println("Error finding student: " + e.getMessage());
        }
        return null;
    }

    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET name = ?, student_id = ?, department = ?, mobile = ? WHERE email = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getStudentId());
            pstmt.setString(3, student.getDepartment());
            pstmt.setString(4, student.getMobile());
            pstmt.setString(5, student.getEmail().toLowerCase().trim());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
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

    public int getTotalStudentCount() {
        String sql = "SELECT COUNT(*) FROM students";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting total student count: " + e.getMessage());
        }

        return 0;
    }
}
