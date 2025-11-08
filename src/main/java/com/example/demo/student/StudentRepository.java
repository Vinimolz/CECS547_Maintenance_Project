package com.example.demo.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s WHERE s.email=?1")
    Optional<Student> findStudentByEmail(String email);

    // Find all active (non-deleted) students
    @Query("SELECT s FROM Student s WHERE s.deleted = false")
    List<Student> findAllActiveStudents();

    // Find all deleted students
    @Query("SELECT s FROM Student s WHERE s.deleted = true")
    List<Student> findAllDeletedStudents();

    // Find active student by ID
    @Query("SELECT s FROM Student s WHERE s.id = ?1 AND s.deleted = false")
    Optional<Student> findActiveStudentById(Long id);
}
