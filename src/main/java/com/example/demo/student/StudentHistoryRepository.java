package com.example.demo.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentHistoryRepository extends JpaRepository<StudentHistory, Long> {

    @Query("SELECT h FROM StudentHistory h WHERE h.studentId = ?1 ORDER BY h.changedAt DESC")
    List<StudentHistory> findByStudentIdOrderByChangedAtDesc(Long studentId);
}
