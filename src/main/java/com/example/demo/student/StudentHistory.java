package com.example.demo.student;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_history")
public class StudentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    private Long studentId;
    private String name;
    private String email;
    private LocalDate dob;

    private String operation; // "UPDATE" or "DELETE"
    private LocalDateTime changedAt;
    private String changedBy = "admin";

    public StudentHistory() {}

    public StudentHistory(Student student, String operation) {
        this.studentId = student.getId();
        this.name = student.getName();
        this.email = student.getEmail();
        this.dob = student.getDob();
        this.operation = operation;
        this.changedAt = LocalDateTime.now();
    }

    // === Getters and Setters ===
    public Long getHistoryId() { return historyId; }
    public void setHistoryId(Long historyId) { this.historyId = historyId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
}