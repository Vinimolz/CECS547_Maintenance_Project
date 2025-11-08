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

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // Get only active (non-deleted) students
    public List<Student> getStudents() {
        return studentRepository.findAllActiveStudents();
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
    }

    // Soft delete - just mark as deleted
    @Transactional
    public void deleteStudent(Long studentId) {
        Student student = studentRepository.findActiveStudentById(studentId)
                .orElseThrow(() -> new IllegalStateException(
                        "Student with ID " + studentId + " does not exist or is already deleted"));

        student.setDeleted(true);
        studentRepository.save(student);
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

        if (name != null && name.length() > 0 && !Objects.equals(name, student.getName())) {
            student.setName(name);
        }

        if (email != null && email.length() > 0 && !Objects.equals(email, student.getEmail())) {
            Optional<Student> studentOptional = studentRepository.findStudentByEmail(email);
            if (studentOptional.isPresent()) {
                throw new IllegalStateException("email exist");
            }
            student.setEmail(email);
        }

        studentRepository.save(student);
    }
}