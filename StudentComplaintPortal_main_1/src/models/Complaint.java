package models;

public class Complaint {
    private int id;
    private String title;
    private String description;
    private String category;
    private String status; // Pending, In Progress, Resolved
    private String studentEmail;

    // New: admin feedback
    private String feedback;

    public Complaint() {
        this.status = "Pending";
    }

    public Complaint(String title, String description, String category, String studentEmail) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.studentEmail = studentEmail;
        this.status = "Pending";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}
