package com.example.demo.student;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for StudentService
 * Tests the complete flow including database operations
 */
@SpringBootTest
@Transactional // Rollback after each test
public class StudentServiceIntegrationTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentHistoryRepository studentHistoryRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    private Student testStudent;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        activityLogRepository.deleteAll();
        studentHistoryRepository.deleteAll();
        studentRepository.deleteAll();

        // Create a test student
        testStudent = new Student(
                "John Doe",
                "john.doe@test.com",
                LocalDate.of(2000, 1, 1)
        );
    }

    // ============================================
    // CREATE TESTS
    // ============================================

    @Test
    void shouldCreateNewStudent() {
        // When
        studentService.addNewStudent(testStudent);

        // Then
        List<Student> students = studentService.getStudents();
        assertThat(students).hasSize(1);
        assertThat(students.get(0).getName()).isEqualTo("John Doe");
        assertThat(students.get(0).getEmail()).isEqualTo("john.doe@test.com");
        assertThat(students.get(0).getDeleted()).isFalse();
    }

    @Test
    void shouldNotCreateStudentWithDuplicateEmail() {
        // Given
        studentService.addNewStudent(testStudent);

        // When/Then
        Student duplicate = new Student(
                "Jane Doe",
                "john.doe@test.com",
                LocalDate.of(1999, 5, 5)
        );

        assertThatThrownBy(() -> studentService.addNewStudent(duplicate))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("email exist");
    }

    @Test
    void shouldLogActivityWhenCreatingStudent() {
        // When
        studentService.addNewStudent(testStudent);

        // Then
        List<ActivityLog> logs = activityLogRepository.findAll();
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getAction()).isEqualTo("CREATE");
    }

    // ============================================
    // READ TESTS
    // ============================================

    @Test
    void shouldGetOnlyActiveStudents() {
        // Given
        studentService.addNewStudent(testStudent);

        Student anotherStudent = new Student(
                "Jane Smith",
                "jane@test.com",
                LocalDate.of(1998, 3, 15)
        );
        studentService.addNewStudent(anotherStudent);

        // Delete one student
        Long studentId = studentRepository.findAll().get(0).getId();
        studentService.deleteStudent(studentId);

        // When
        List<Student> activeStudents = studentService.getStudents();

        // Then
        assertThat(activeStudents).hasSize(1);
        assertThat(activeStudents.get(0).getName()).isEqualTo("Jane Smith");
    }

    @Test
    void shouldGetDeletedStudents() {
        // Given
        studentService.addNewStudent(testStudent);
        Long studentId = studentRepository.findAll().get(0).getId();
        studentService.deleteStudent(studentId);

        // When
        List<Student> deletedStudents = studentService.getDeletedStudents();

        // Then
        assertThat(deletedStudents).hasSize(1);
        assertThat(deletedStudents.get(0).getDeleted()).isTrue();
    }

    // ============================================
    // UPDATE TESTS
    // ============================================

    @Test
    void shouldUpdateStudentName() {
        // Given
        studentService.addNewStudent(testStudent);
        Long studentId = studentRepository.findAll().get(0).getId();

        // When
        studentService.updateStudent(studentId, "John Updated", null);

        // Then
        Student updated = studentRepository.findById(studentId).get();
        assertThat(updated.getName()).isEqualTo("John Updated");
        assertThat(updated.getEmail()).isEqualTo("john.doe@test.com"); // unchanged
    }

    @Test
    void shouldUpdateStudentEmail() {
        // Given
        studentService.addNewStudent(testStudent);
        Long studentId = studentRepository.findAll().get(0).getId();

        // When
        studentService.updateStudent(studentId, null, "new.email@test.com");

        // Then
        Student updated = studentRepository.findById(studentId).get();
        assertThat(updated.getName()).isEqualTo("John Doe"); // unchanged
        assertThat(updated.getEmail()).isEqualTo("new.email@test.com");
    }

    @Test
    void shouldNotUpdateToExistingEmail() {
        // Given
        studentService.addNewStudent(testStudent);

        Student another = new Student(
                "Jane",
                "jane@test.com",
                LocalDate.of(1999, 1, 1)
        );
        studentService.addNewStudent(another);

        Long johnId = studentRepository.findStudentByEmail("john.doe@test.com").get().getId();

        // When/Then
        assertThatThrownBy(() ->
                studentService.updateStudent(johnId, null, "jane@test.com"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("email exist");
    }

    @Test
    void shouldCreateHistoryEntryOnUpdate() {
        // Given
        studentService.addNewStudent(testStudent);
        Long studentId = studentRepository.findAll().get(0).getId();

        // When
        studentService.updateStudent(studentId, "Updated Name", null);

        // Then
        List<StudentHistory> history = studentService.getStudentHistory(studentId);
        assertThat(history).hasSize(1);
        assertThat(history.get(0).getOperation()).isEqualTo("UPDATE");
        assertThat(history.get(0).getName()).isEqualTo("John Doe"); // OLD value
    }

    @Test
    void shouldNotCreateHistoryIfNoChanges() {
        // Given
        studentService.addNewStudent(testStudent);
        Long studentId = studentRepository.findAll().get(0).getId();

        // When - update with same values
        studentService.updateStudent(studentId, "John Doe", "john.doe@test.com");

        // Then
        List<StudentHistory> history = studentService.getStudentHistory(studentId);
        assertThat(history).isEmpty();
    }

    // ============================================
    // DELETE TESTS (Soft Delete)
    // ============================================

    @Test
    void shouldSoftDeleteStudent() {
        // Given
        studentService.addNewStudent(testStudent);
        Long studentId = studentRepository.findAll().get(0).getId();

        // When
        studentService.deleteStudent(studentId);

        // Then
        Student deleted = studentRepository.findById(studentId).get();
        assertThat(deleted.getDeleted()).isTrue();

        // Should not appear in active students
        List<Student> activeStudents = studentService.getStudents();
        assertThat(activeStudents).isEmpty();
    }

    @Test
    void shouldCreateHistoryEntryOnDelete() {
        // Given
        studentService.addNewStudent(testStudent);
        Long studentId = studentRepository.findAll().get(0).getId();

        // When
        studentService.deleteStudent(studentId);

        // Then
        List<StudentHistory> history = studentService.getStudentHistory(studentId);
        assertThat(history).hasSize(1);
        assertThat(history.get(0).getOperation()).isEqualTo("DELETE");
    }

    @Test
    void shouldNotDeleteAlreadyDeletedStudent() {
        // Given
        studentService.addNewStudent(testStudent);
        Long studentId = studentRepository.findAll().get(0).getId();
        studentService.deleteStudent(studentId);

        // When/Then
        assertThatThrownBy(() -> studentService.deleteStudent(studentId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("does not exist or is already deleted");
    }

    @Test
    void shouldNotDeleteNonExistentStudent() {
        // When/Then
        assertThatThrownBy(() -> studentService.deleteStudent(999L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("does not exist");
    }

    // ============================================
    // RESTORE TESTS
    // ============================================

    @Test
    void shouldRestoreDeletedStudent() {
        // Given
        studentService.addNewStudent(testStudent);
        Long studentId = studentRepository.findAll().get(0).getId();
        studentService.deleteStudent(studentId);

        // When
        studentService.restoreStudent(studentId);

        // Then
        Student restored = studentRepository.findById(studentId).get();
        assertThat(restored.getDeleted()).isFalse();

        List<Student> activeStudents = studentService.getStudents();
        assertThat(activeStudents).hasSize(1);
    }

    @Test
    void shouldNotRestoreActiveStudent() {
        // Given
        studentService.addNewStudent(testStudent);
        Long studentId = studentRepository.findAll().get(0).getId();

        // When/Then
        assertThatThrownBy(() -> studentService.restoreStudent(studentId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Student is not deleted");
    }

    @Test
    void shouldLogActivityOnRestore() {
        // Given
        studentService.addNewStudent(testStudent);
        Long studentId = studentRepository.findAll().get(0).getId();
        studentService.deleteStudent(studentId);
        activityLogRepository.deleteAll(); // Clear previous logs

        // When
        studentService.restoreStudent(studentId);

        // Then
        List<ActivityLog> logs = activityLogRepository.findAll();
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getAction()).isEqualTo("RESTORE");
    }

    // ============================================
    // COMPLEX WORKFLOW TESTS
    // ============================================

    @Test
    void shouldTrackCompleteStudentLifecycle() {
        // Create
        studentService.addNewStudent(testStudent);
        Long studentId = studentRepository.findAll().get(0).getId();

        // Update twice
        studentService.updateStudent(studentId, "John Updated", null);
        studentService.updateStudent(studentId, null, "updated@test.com");

        // Delete
        studentService.deleteStudent(studentId);

        // Restore
        studentService.restoreStudent(studentId);

        // Verify history
        List<StudentHistory> history = studentService.getStudentHistory(studentId);
        assertThat(history).hasSize(3); // 2 updates + 1 delete

        // Verify activity logs
        List<ActivityLog> logs = activityLogRepository.findAll();
        assertThat(logs).hasSize(5); // CREATE, UPDATE, UPDATE, DELETE, RESTORE
        assertThat(logs.stream().map(ActivityLog::getAction))
                .containsExactlyInAnyOrder("CREATE", "UPDATE", "UPDATE", "DELETE", "RESTORE");
    }
}
