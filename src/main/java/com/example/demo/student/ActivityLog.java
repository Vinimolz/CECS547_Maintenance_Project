package com.example.demo.student;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_log")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    private String action; // "CREATE", "UPDATE", "DELETE"
    private Long studentId;
    private String username = "admin"; // hardcoded
    private LocalDateTime timestamp;

    // Default constructor
    public ActivityLog() {}

    // Constructor used by service
    public ActivityLog(String action, Long studentId) {
        this.action = action;
        this.studentId = studentId;
        this.username = "admin";
        this.timestamp = LocalDateTime.now();
    }

    // === Getters and Setters ===
    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}