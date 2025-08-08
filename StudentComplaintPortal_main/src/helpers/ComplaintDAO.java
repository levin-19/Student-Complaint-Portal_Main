package helpers;

import models.Complaint;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDAO {

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

    public boolean insertComplaint(Complaint complaint) {
        String sql = "INSERT INTO complaints (title, description, category, student_email, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, complaint.getTitle());
            pstmt.setString(2, complaint.getDescription());
            pstmt.setString(3, complaint.getCategory());
            pstmt.setString(4, complaint.getStudentEmail().toLowerCase().trim());
            pstmt.setString(5, "Pending"); // Always start as Pending

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    complaint.setId(generatedKeys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error inserting complaint: " + e.getMessage());
        }

        return false;
    }

    private Complaint mapRow(ResultSet rs) throws SQLException {
        Complaint c = new Complaint();
        c.setId(rs.getInt("id"));
        c.setTitle(rs.getString("title"));
        c.setDescription(rs.getString("description"));
        c.setCategory(rs.getString("category"));
        c.setStatus(rs.getString("status")); // Direct mapping, no normalization
        c.setStudentEmail(rs.getString("student_email"));
        if (hasColumn(rs, "feedback")) {
            c.setFeedback(rs.getString("feedback"));
        }
        return c;
    }

    public List<Complaint> getComplaintsByStudentEmail(String studentEmail) {
        List<Complaint> complaints = new ArrayList<>();
        String sql = "SELECT * FROM complaints WHERE student_email = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentEmail.toLowerCase().trim());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                complaints.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting complaints: " + e.getMessage());
        }

        return complaints;
    }

    public List<Complaint> getAllComplaints() {
        List<Complaint> complaints = new ArrayList<>();
        String sql = "SELECT * FROM complaints ORDER BY created_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                complaints.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all complaints: " + e.getMessage());
        }

        return complaints;
    }

    public List<Complaint> getComplaintsByStatus(String status) {
        List<Complaint> complaints = new ArrayList<>();
        String sql = "SELECT * FROM complaints WHERE status = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status); // Use exact status value
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                complaints.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting complaints by status: " + e.getMessage());
        }

        return complaints;
    }

    public boolean updateComplaint(Complaint complaint) {
        String sql = "UPDATE complaints SET title = ?, description = ?, category = ?, status = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, complaint.getTitle());
            pstmt.setString(2, complaint.getDescription());
            pstmt.setString(3, complaint.getCategory());
            pstmt.setString(4, complaint.getStatus()); // Direct status update
            pstmt.setInt(5, complaint.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating complaint: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteComplaint(int complaintId) {
        String sql = "DELETE FROM complaints WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, complaintId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting complaint: " + e.getMessage());
            return false;
        }
    }

    public Complaint getComplaintById(int id) {
        String sql = "SELECT * FROM complaints WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting complaint by ID: " + e.getMessage());
        }

        return null;
    }

    public int getComplaintCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM complaints WHERE status = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status); // Use exact status value
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting complaint count: " + e.getMessage());
        }

        return 0;
    }

    public int getTotalComplaintCount() {
        String sql = "SELECT COUNT(*) FROM complaints";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting total count: " + e.getMessage());
        }

        return 0;
    }

    // FIXED: Status update method with proper validation
    public boolean setComplaintStatus(int id, String status) {
        // Validate status before updating
        if (!isValidStatus(status)) {
            System.err.println("Invalid status: " + status);
            return false;
        }

        String sql = "UPDATE complaints SET status = ?, updated_at = NOW() WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status.trim()); // Trim whitespace
            pstmt.setInt(2, id);
            
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Status updated to: " + status + " for complaint ID: " + id);
                return true;
            } else {
                System.err.println("❌ No rows updated for complaint ID: " + id);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error setting complaint status: " + e.getMessage());
            return false;
        }
    }

    // Helper method to validate status values
    private boolean isValidStatus(String status) {
        if (status == null) return false;
        String trimmed = status.trim();
        return trimmed.equals("Pending") || 
               trimmed.equals("In Progress") || 
               trimmed.equals("Resolved");
    }

    public boolean setComplaintFeedback(int id, String feedback) {
        String sql = "UPDATE complaints SET feedback = ?, feedback_updated_at = NOW() WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (feedback != null && !feedback.isBlank()) {
                pstmt.setString(1, feedback);
            } else {
                pstmt.setNull(1, Types.VARCHAR);
            }
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error setting complaint feedback: " + e.getMessage());
            return false;
        }
    }

    public int getComplaintCountByStudentEmail(String studentEmail) {
        String sql = "SELECT COUNT(*) FROM complaints WHERE student_email = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentEmail.toLowerCase().trim());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting student complaint count: " + e.getMessage());
        }

        return 0;
    }

    public int getResolvedComplaintCountByStudentEmail(String studentEmail) {
        String sql = "SELECT COUNT(*) FROM complaints WHERE student_email = ? AND status = 'Resolved'";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentEmail.toLowerCase().trim());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting resolved complaint count: " + e.getMessage());
        }

        return 0;
    }
}
