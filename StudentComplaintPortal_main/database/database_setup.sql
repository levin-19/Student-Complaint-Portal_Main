-- =====================================================
-- Student Complaint Portal - Complete Database Setup
-- Combined SQL for XAMPP MySQL
-- =====================================================

-- Create database
CREATE DATABASE IF NOT EXISTS student_complaint_portal 
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE student_complaint_portal;

-- Drop existing tables for clean setup
DROP TABLE IF EXISTS complaints;
DROP TABLE IF EXISTS students;

-- Students table with profile picture support
CREATE TABLE students (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Complaints table with FIXED status enum
CREATE TABLE complaints (
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
  FOREIGN KEY (student_email) REFERENCES students(email)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample students
INSERT INTO students (name, student_id, department, mobile, email, password) VALUES
('Test Student', 'TEST001', 'Computer Science', '1234567890', 'test@student.com', 'test123'),
('John Doe', 'CS001', 'Computer Science', '1234567890', 'john@student.com', 'password123'),
('Jane Smith', 'IT002', 'Information Technology', '0987654321', 'jane@student.com', 'password123'),
('Alice Johnson', 'EE003', 'Electrical Engineering', '5555555555', 'alice@student.com', 'password123');

-- Insert sample complaints with all three status types
INSERT INTO complaints (title, description, category, student_email, status, feedback) VALUES
('Lab Equipment Malfunction', 'The computers in Lab A are not working properly. Need immediate attention.', 'Facility', 'john@student.com', 'Pending', NULL),
('Course Registration Issue', 'Unable to register for Advanced Database Systems course due to system error.', 'Academic', 'jane@student.com', 'In Progress', 'We are working on this issue. Expected resolution within 24 hours.'),
('Library Access Problem', 'Student ID card not working for library access after recent update.', 'Admin', 'alice@student.com', 'Resolved', 'Issue has been resolved. Please try accessing the library again.'),
('Classroom Air Conditioning', 'AC not working in Room 301, making it difficult to concentrate during lectures.', 'Facility', 'test@student.com', 'In Progress', 'Maintenance team has been notified. Work scheduled for tomorrow.'),
('Grade Discrepancy', 'There seems to be an error in my final grade calculation for Mathematics course.', 'Academic', 'test@student.com', 'Pending', NULL);

-- Verify setup
SELECT 'Database Setup Complete!' as Message;
SELECT 'Students' as Table_Name, COUNT(*) as Record_Count FROM students
UNION ALL
SELECT 'Complaints' as Table_Name, COUNT(*) as Record_Count FROM complaints;

-- Show sample login credentials
SELECT 
  'Login Credentials' as Info, '' as Email, '' as Password
UNION ALL SELECT 'Admin Login:', 'admin@portal.com', 'admin123'
UNION ALL SELECT 'Student Login:', 'test@student.com', 'test123'
UNION ALL SELECT 'Student Login:', 'john@student.com', 'password123';

-- Show status distribution
SELECT 'Status Distribution:' as Info, '' as Status, '' as Count
UNION ALL
SELECT '', status, CAST(COUNT(*) as CHAR) FROM complaints GROUP BY status;
