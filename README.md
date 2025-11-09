# Student CRUD Application - Software Maintenance Project

## Original Project
Cloned from: https://github.com/jonsungwoo/SpringBoot_CRUD_Postgre_Tutorial

## New Features Added
- **Soft Delete functionality** - Deleted records are marked as deleted but retained in database
- **Student history/versioning** - Track all modifications to student records
- **Activity logging for CRUD operations** - Log all create, update, and delete actions

## Technologies
- Spring Boot 3.0.6
- PostgreSQL 16
- Java 21
- Maven

---

## Getting Started

### Prerequisites
- Java 21 (or Java 17+)
- PostgreSQL 16 installed and running
- Maven

### Setup Instructions

1. **Clone the repository**
```bash
   git clone 
   cd 
```

2. **Create the database**
   
   Open pgAdmin or psql and run:
```sql
   CREATE DATABASE studentdb;
```

3. **Configure database connection**
   
   Edit `src/main/resources/application.properties` with your PostgreSQL credentials:
```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/studentdb
   spring.datasource.username=postgres
   spring.datasource.password=YOUR_PASSWORD
```

4. **Run the application**
```bash
   mvn spring-boot:run
```

   The application will start on `http://localhost:8080`

5. **Verify setup**
   
   Open browser and navigate to:
```
   http://localhost:8080/api/v1/student
```
   You should see an empty array `[]`

---

## Initial Data Setup (Optional)

If you want to populate the database with sample students on startup, create this file:

**File:** `src/main/java/com/example/demo/student/StudentConfig.java`
```java
package com.example.demo.student;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

import static java.time.Month.*;

@Configuration
public class StudentConfig {

    @Bean
    CommandLineRunner commandLineRunner(StudentRepository repository){
        return args -> {
            Student mariam = new Student(
                    "Mariam",
                    "mariam.jamal@gmail.com",
                    LocalDate.of(2000, JANUARY, 5)
            );
            Student alex = new Student(
                    "Alex",
                    "alex@gmail.com",
                    LocalDate.of(2004, JANUARY, 5)
            );

            repository.saveAll(
                    List.of(mariam, alex)
            );
        };
    }
}
```

**Note:** This file will insert the two sample students every time you restart the application. Delete this file if you don't want auto-population.

---

## API Endpoints & Testing

### Base URL
```
http://localhost:8080/api/v1
```

---

### 1. **Create a Student**
```bash
curl -X POST http://localhost:8080/api/v1/student \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "dob": "2002-05-15"
  }'
```

---

### 2. **Get All Active Students**
```bash
curl -X GET http://localhost:8080/api/v1/student
```

---

### 3. **Update a Student**

Update name:
```bash
curl -X PUT "http://localhost:8080/api/v1/student/1?name=Jane%20Doe"
```

Update email:
```bash
curl -X PUT "http://localhost:8080/api/v1/student/1?email=jane.doe@example.com"
```

Update both:
```bash
curl -X PUT "http://localhost:8080/api/v1/student/1?name=Jane%20Doe&email=jane.doe@example.com"
```

---

### 4. **Soft Delete a Student**
```bash
curl -X DELETE http://localhost:8080/api/v1/student/1
```
*Note: This marks the student as deleted but retains the record in the database*

---

### 5. **Get All Deleted Students**
```bash
curl -X GET http://localhost:8080/api/v1/student/deleted
```

---

### 6. **View Student History**
```bash
curl -X GET http://localhost:8080/api/v1/student/1/history
```
*Shows all previous versions of a student (tracks updates and deletes)*

---

### 7. **View Activity Logs**
```bash
curl -X GET http://localhost:8080/api/v1/activity-logs
```
*Shows all CREATE, UPDATE, and DELETE operations*

---

## Testing Workflow Example

Here's a complete workflow to test all features:
```bash
# 1. Create a student
curl -X POST http://localhost:8080/api/v1/student \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice Smith",
    "email": "alice@example.com",
    "dob": "2001-03-20"
  }'

# 2. View all students
curl -X GET http://localhost:8080/api/v1/student

# 3. Update the student (use ID from step 2, likely ID=1)
curl -X PUT "http://localhost:8080/api/v1/student/1?name=Alice%20Johnson"

# 4. Update again
curl -X PUT "http://localhost:8080/api/v1/student/1?email=alice.johnson@example.com"

# 5. View student history (should show 2 versions)
curl -X GET http://localhost:8080/api/v1/student/1/history

# 6. Soft delete the student
curl -X DELETE http://localhost:8080/api/v1/student/1

# 7. View active students (should not include deleted student)
curl -X GET http://localhost:8080/api/v1/student

# 8. View deleted students (should show the deleted student)
curl -X GET http://localhost:8080/api/v1/student/deleted

# 9. View activity logs (should show all operations)
curl -X GET http://localhost:8080/api/v1/activity-logs
```

---

## Database Tables

The application creates these tables:

- **`student`** - Main student records with soft delete flag
- **`student_history`** - Historical versions of student records
- **`activity_log`** - Audit trail of all operations

---

## Features Implementation Details

### Soft Delete
- Deleted students are marked with `deleted = true` flag
- Normal queries filter out deleted students
- Deleted students can be viewed via `/api/v1/student/deleted`
- Records remain in database for audit purposes

### Versioning
- Before any UPDATE or DELETE, the old version is saved to `student_history` table
- Each history record includes: old values, operation type, timestamp, and user
- View complete history via `/api/v1/student/{id}/history`

### Activity Logging
- All CREATE, UPDATE, and DELETE operations are logged
- Each log includes: action type, student ID, username (hardcoded as "admin"), and timestamp
- View all logs via `/api/v1/activity-logs`

---

## Troubleshooting

### Application won't start
- Verify PostgreSQL is running
- Check database credentials in `application.properties`
- Ensure database `studentdb` exists

### Port 8080 already in use
- Stop other applications using port 8080
- Or change port in `application.properties`: `server.port=8081`

### Database connection errors
- Verify PostgreSQL service is running
- Test connection: `psql -U postgres -d studentdb`

---

## Author
Vinicius Molz - CECS 547 Software Maintenance Project

## Acknowledgments
- Original project by jonsungwoo
- Enhanced with soft delete, versioning, and activity logging features
