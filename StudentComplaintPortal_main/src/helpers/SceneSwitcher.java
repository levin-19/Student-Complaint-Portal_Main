package helpers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import models.Student;
import models.Complaint;

public class SceneSwitcher {
    private static Stage primaryStage;
    private static Student currentStudent;
    private static StudentDAO studentDAO = new StudentDAO();
    private static ComplaintDAO complaintDAO = new ComplaintDAO();
    private static Complaint currentComplaint;

    public static final String ADMIN_EMAIL = "admin@portal.com";
    public static final String ADMIN_PASSWORD = "admin123";

    // Fixed size for all scenes
    public static final double APP_WIDTH = 1000;
    public static final double APP_HEIGHT = 700;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
        DatabaseConfig.initializeDatabase();

        primaryStage.setResizable(false);
        primaryStage.setWidth(APP_WIDTH);
        primaryStage.setHeight(APP_HEIGHT);
        primaryStage.setMinWidth(APP_WIDTH);
        primaryStage.setMinHeight(APP_HEIGHT);
        primaryStage.setMaxWidth(APP_WIDTH);
        primaryStage.setMaxHeight(APP_HEIGHT);
        
        System.out.println("✅ Primary stage configured: " + APP_WIDTH + "x" + APP_HEIGHT);
    }

    public static void switchScene(String fxmlFile, String title) {
        System.out.println("🔄 Switching to scene: " + fxmlFile);
        
        try {
            // Build the resource path
            String resourcePath = "/scenes/" + fxmlFile;
            System.out.println("📁 Looking for FXML at: " + resourcePath);
            
            // Check if resource exists
            var resource = SceneSwitcher.class.getResource(resourcePath);
            if (resource == null) {
                System.err.println("❌ FXML file not found: " + resourcePath);
                System.err.println("💡 Make sure " + fxmlFile + " is in the src/scenes/ folder");
                throw new IOException("FXML file not found: " + resourcePath);
            }
            
            System.out.println("✅ FXML file found: " + resource.toString());
            
            // Load FXML
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            
            System.out.println("✅ FXML loaded successfully");
            
            // Get controller for debugging
            Object controller = loader.getController();
            if (controller != null) {
                System.out.println("✅ Controller loaded: " + controller.getClass().getSimpleName());
            } else {
                System.err.println("⚠️ No controller found for " + fxmlFile);
            }
            
            // Create and set scene
            Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            
            // Ensure stage properties
            primaryStage.setResizable(false);
            primaryStage.setWidth(APP_WIDTH);
            primaryStage.setHeight(APP_HEIGHT);
            primaryStage.centerOnScreen();
            
            System.out.println("✅ Scene switched successfully to: " + title);
            
        } catch (IOException e) {
            System.err.println("❌ Error switching scene: " + e.getMessage());
            e.printStackTrace();
            
            // Show detailed error information
            System.err.println("\n🔍 Debugging information:");
            System.err.println("  FXML file: " + fxmlFile);
            System.err.println("  Resource path: /scenes/" + fxmlFile);
            System.err.println("  Current working directory: " + System.getProperty("user.dir"));
            
            // List available resources in scenes folder
            try {
                var scenesUrl = SceneSwitcher.class.getResource("/scenes/");
                if (scenesUrl != null) {
                    System.err.println("  Scenes folder found: " + scenesUrl.toString());
                } else {
                    System.err.println("  ❌ Scenes folder not found in resources");
                }
            } catch (Exception ex) {
                System.err.println("  Error checking scenes folder: " + ex.getMessage());
            }
            
            throw new RuntimeException("Failed to switch scene: " + fxmlFile, e);
        }
    }

    // All other methods remain the same...
    public static Student getCurrentStudent() { return currentStudent; }
    public static void setCurrentStudent(Student student) { currentStudent = student; }

    public static Student findStudentByEmail(String email) {
        return studentDAO.findStudentByEmail(email);
    }

    public static boolean isValidLogin(String email, String password) {
        if (email == null || password == null) return false;
        Student student = findStudentByEmail(email.toLowerCase().trim());
        return student != null && password.equals(student.getPassword());
    }

    public static boolean isAdminLogin(String email, String password) {
        return ADMIN_EMAIL.equalsIgnoreCase(email.trim()) && ADMIN_PASSWORD.equals(password);
    }

    public static boolean addStudent(Student student) {
        return studentDAO.insertStudent(student);
    }

    public static boolean updateStudent(Student student) {
        return studentDAO.updateStudent(student);
    }

    public static boolean isEmailExists(String email) {
        return studentDAO.isEmailExists(email);
    }

    public static boolean isStudentIdExists(String studentId) {
        return studentDAO.isStudentIdExists(studentId);
    }

    public static boolean addComplaint(Complaint complaint) {
        return complaintDAO.insertComplaint(complaint);
    }

    public static boolean updateComplaint(Complaint complaint) {
        return complaintDAO.updateComplaint(complaint);
    }

    public static boolean deleteComplaint(int complaintId) {
        return complaintDAO.deleteComplaint(complaintId);
    }

    public static java.util.List<Complaint> getComplaintsByStudentEmail(String studentEmail) {
        return complaintDAO.getComplaintsByStudentEmail(studentEmail);
    }

    public static java.util.List<Complaint> getAllComplaints() {
        return complaintDAO.getAllComplaints();
    }

    public static java.util.List<Complaint> getComplaintsByStatus(String status) {
        return complaintDAO.getComplaintsByStatus(status);
    }

    public static Complaint getComplaintById(int id) {
        return complaintDAO.getComplaintById(id);
    }

    public static int getTotalComplaintCount() {
        return complaintDAO.getTotalComplaintCount();
    }

    public static int getComplaintCountByStatus(String status) {
        return complaintDAO.getComplaintCountByStatus(status);
    }

    public static int getComplaintCountByStudentEmail(String studentEmail) {
        return complaintDAO.getComplaintCountByStudentEmail(studentEmail);
    }

    public static int getResolvedComplaintCountByStudentEmail(String studentEmail) {
        return complaintDAO.getResolvedComplaintCountByStudentEmail(studentEmail);
    }

    public static boolean setComplaintStatus(int id, String status) {
        return complaintDAO.setComplaintStatus(id, status);
    }

    public static boolean setComplaintFeedback(int id, String feedback) {
        return complaintDAO.setComplaintFeedback(id, feedback);
    }

    public static Complaint getCurrentComplaint() { return currentComplaint; }
    public static void setCurrentComplaint(Complaint complaint) { currentComplaint = complaint; }
}
