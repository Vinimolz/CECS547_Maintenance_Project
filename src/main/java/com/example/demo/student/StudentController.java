package com.example.demo.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/student")
public class StudentController {
    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<Student> getStudent() {
        return studentService.getStudents();
    }

    @GetMapping("/deleted")
    public List<Student> getDeletedStudents() {
        return studentService.getDeletedStudents();
    }

    @GetMapping("/{studentId}/history")
    public List<StudentHistory> getStudentHistory(@PathVariable("studentId") Long studentId) {
        return studentService.getStudentHistory(studentId);
    }

    @PostMapping
    public void registerStudent(@RequestBody Student student) {
        studentService.addNewStudent(student);
    }

    @PutMapping(path = "{studentId}")
    public void updateStudent(@PathVariable("studentId") Long studentId,
                              @RequestParam(required = false) String name,
                              @RequestParam(required = false) String email){
        studentService.updateStudent(studentId, name, email);
    }

    @PutMapping("/{studentId}/restore")
    public void restoreStudent(@PathVariable("studentId") Long studentId) {
        studentService.restoreStudent(studentId);
    }

    @DeleteMapping(path = "{studentId}")
    public void deleteStudent(@PathVariable("studentId") Long studentId) {
        studentService.deleteStudent(studentId);
    }
}
