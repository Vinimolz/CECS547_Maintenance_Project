package com.example.demo.student;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentHistoryRepository studentHistoryRepository;
    private final ActivityLogService activityLogService;

    @Autowired
    public StudentService(
            StudentRepository studentRepository,
            StudentHistoryRepository studentHistoryRepository,
            ActivityLogService activityLogService
    ) {
        this.studentRepository = studentRepository;
        this.studentHistoryRepository = studentHistoryRepository;
        this.activityLogService = activityLogService;
    }

    // Get only active (non-deleted) students
    public List<Student> getStudents() {
        return studentRepository.findAllActiveStudents();
    }

    // Get student history by id
    public List<StudentHistory> getStudentHistory(Long studentId) {
        return studentHistoryRepository.findByStudentIdOrderByChangedAtDesc(studentId);
    }

    // Get deleted students
    public List<Student> getDeletedStudents() {
        return studentRepository.findAllDeletedStudents();
    }

    public void addNewStudent(Student student) {
        Optional<Student> studentOptional = studentRepository.findStudentByEmail(student.getEmail());
        if (studentOptional.isPresent()) {
            throw new IllegalStateException("email exist");
        }
        student.setDeleted(false); // Ensure new students are not marked as deleted
        studentRepository.save(student);

        //Log student
        activityLogService.logAction("CREATE", student.getId());
    }

    // Soft delete - just mark as deleted
    @Transactional
    public void deleteStudent(Long studentId) {
        Student student = studentRepository.findActiveStudentById(studentId)
                .orElseThrow(() -> new IllegalStateException(
                        "Student with ID " + studentId + " does not exist or is already deleted"));

        // Save to history
        StudentHistory history = new StudentHistory(student, "DELETE");
        studentHistoryRepository.save(history);

        student.setDeleted(true);
        studentRepository.save(student);

        //Log student
        activityLogService.logAction("DELETE", student.getId());
    }

    // Restore a soft-deleted student
    @Transactional
    public void restoreStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalStateException(
                        "Student with ID " + studentId + " does not exist"));

        if (!student.getDeleted()) {
            throw new IllegalStateException("Student is not deleted");
        }

        student.setDeleted(false);
        studentRepository.save(student);
    }

    // Hard delete - permanently remove from database (optional, for admin use)
    public void hardDeleteStudent(Long studentId) {
        boolean exist = studentRepository.existsById(studentId);
        if (!exist) {
            throw new IllegalStateException("Student with ID " + studentId + " does not exist");
        }
        studentRepository.deleteById(studentId);
    }

    @Transactional
    public void updateStudent(Long studentId, String name, String email) {
        Student student = studentRepository.findActiveStudentById(studentId)
                .orElseThrow(() -> new IllegalStateException(
                        "Student with ID " + studentId + " does not exist or is deleted"));

        boolean changed = false;

        if (name != null && !name.isEmpty() && !Objects.equals(name, student.getName())) {
            student.setName(name);
            changed = true;
        }

        if (email != null && !email.isEmpty() && !Objects.equals(email, student.getEmail())) {
            Optional<Student> studentOptional = studentRepository.findStudentByEmail(email);
            if (studentOptional.isPresent()) {
                throw new IllegalStateException("email exist");
            }
            student.setEmail(email);
            changed = true;
        }

        if (changed) {
            // Save old version to history
            StudentHistory history = new StudentHistory(student, "UPDATE");
            studentHistoryRepository.save(history);

            studentRepository.save(student);

            //Log student
            activityLogService.logAction("UPDATE", student.getId());
        }
    }
}