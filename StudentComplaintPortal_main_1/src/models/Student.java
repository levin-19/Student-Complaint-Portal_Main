package models;

public class Student {
    private String name;
    private String studentId;
    private String department;
    private String mobile;
    private String email;
    private String password;

    public Student() {}

    public Student(String name, String studentId, String department, String mobile, String email, String password) {
        this.name = name;
        this.studentId = studentId;
        this.department = department;
        this.mobile = mobile;
        this.email = email;
        this.password = password;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
