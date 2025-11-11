# Student CRUD Application - Software Maintenance Project

## Original Project
Cloned from: https://github.com/jonsungwoo/SpringBoot_CRUD_Postgre_Tutorial

A comprehensive Spring Boot application that performs CRUD operations on student records with enhanced features including soft delete, version history tracking, and activity logging.

## 📋 Table of Contents
- [Project Overview](#project-overview)
- [Features Implemented](#features-implemented)
- [Technologies Used](#technologies-used)
- [Prerequisites](#prerequisites)
- [Setup Instructions](#setup-instructions)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Maintenance & Testing Documentation](#maintenance--testing-documentation)

---

## 🎯 Project Overview

This project is an enhancement of a basic Spring Boot CRUD application. The original project provided simple create, read, update, and delete operations for student records.

### Enhancements Added:
1. **Soft Delete** - Records are marked as deleted rather than permanently removed
2. **Version History/Versioning** - All updates and deletions are tracked in a history table
3. **Activity Logging** - All actions (CREATE, UPDATE, DELETE, RESTORE) are logged with timestamps
4. **Restore Functionality** - Ability to restore soft-deleted students

These features are essential for systems like banking or social media where data retention and audit trails are critical.

---

## ✨ Features Implemented

### Core CRUD Operations
- ✅ Create new student records
- ✅ Read/List all active students
- ✅ Update student information (name, email)
- ✅ Delete student records (soft delete)

### Enhanced Features
- ✅ **Soft Delete**: Students are marked as deleted but remain in database
- ✅ **Student History**: Tracks all changes with old values before updates/deletes
- ✅ **Activity Logging**: Records all operations with timestamp and user
- ✅ **Restore Deleted Students**: Ability to undo soft deletes
- ✅ **View Deleted Students**: Separate endpoint to view all soft-deleted records
- ✅ **Duplicate Email Validation**: Prevents multiple students with same email

---

## 🛠️ Technologies Used

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **PostgreSQL**
- **Maven**
- **JUnit 5** (for testing)
- **AssertJ** (for test assertions)

---

## 📦 Prerequisites

Before running this application, ensure you have:

- Java Development Kit (JDK) 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- pgAdmin (optional, for database management)
- Git Bash or terminal with curl support (for API testing)

---

## 🚀 Setup Instructions

### 1. Clone the Repository
```bash
git clone [your-repository-url]
cd [your-project-directory]
```

### 2. Configure Database

Create a PostgreSQL database:
```sql
CREATE DATABASE student_db;
```

Update `src/main/resources/application.properties` with your database credentials:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/student_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 3. Reset Database (Optional)

To start with a clean database, run the SQL script:
```bash
psql -U your_username -d student_db -f reset_database.sql
```

Or run the SQL directly in pgAdmin:
```sql
TRUNCATE TABLE activity_log RESTART IDENTITY CASCADE;
TRUNCATE TABLE student_history RESTART IDENTITY CASCADE;
TRUNCATE TABLE student RESTART IDENTITY CASCADE;
ALTER SEQUENCE student_sequence RESTART WITH 1;
ALTER SEQUENCE student_history_history_id_seq RESTART WITH 1;
ALTER SEQUENCE activity_log_log_id_seq RESTART WITH 1;
```

---

## ▶️ Running the Application

### Start the Application
```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Or using Maven directly
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Verify Application is Running
Open your browser and navigate to:
```
http://localhost:8080/api/v1/student
```

You should see an empty array `[]` if the database is clean.

---

## 🔌 API Endpoints

### Student Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/student` | Get all active students |
| GET | `/api/v1/student/deleted` | Get all soft-deleted students |
| GET | `/api/v1/student/{id}/history` | Get history of changes for a student |
| POST | `/api/v1/student` | Create a new student |
| PUT | `/api/v1/student/{id}?name=X&email=Y` | Update student information |
| PUT | `/api/v1/student/{id}/restore` | Restore a soft-deleted student |
| DELETE | `/api/v1/student/{id}` | Soft delete a student |

### Activity Logs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/activity-logs` | Get all activity logs |

### Example Requests

**Create a Student:**
```bash
curl -X POST http://localhost:8080/api/v1/student \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@test.com","dob":"2000-01-15"}'
```

**Update a Student:**
```bash
curl -X PUT "http://localhost:8080/api/v1/student/1?name=John%20Updated&email=john.updated@test.com"
```

**Delete a Student (Soft Delete):**
```bash
curl -X DELETE http://localhost:8080/api/v1/student/1
```

**Restore a Student:**
```bash
curl -X PUT http://localhost:8080/api/v1/student/1/restore
```

**Get Student History:**
```bash
curl http://localhost:8080/api/v1/student/1/history
```

---

## 🧪 Testing

### 1. Automated API Testing (Bash Script)

Run the comprehensive API testing script that covers all endpoints:

```bash
# Make script executable (first time only)
chmod +x test_api.sh

# Run all tests
./test_api.sh
```

**What the script tests:**
- Creating multiple students
- Updating student information
- Duplicate email validation
- Soft delete operations
- Restore functionality
- Student history tracking
- Activity logging
- Error handling for non-existent students
- Complex workflows

### 2. JUnit Integration Tests

Run automated integration tests:

```bash
# Run all tests
./mvnw test

# Or
mvn test
```

**Test Coverage (21 test cases):**
- ✅ Student creation and duplicate validation
- ✅ Reading active and deleted students
- ✅ Updating student information
- ✅ Soft delete functionality
- ✅ Restore operations
- ✅ History tracking
- ✅ Activity logging
- ✅ Complete lifecycle workflows

**View Test Results:**
```bash
# Test reports are generated in:
target/surefire-reports/
```

### 3. Manual Browser Testing

Test GET endpoints directly in your browser:

**View all active students:**
```
http://localhost:8080/api/v1/student
```

**View deleted students:**
```
http://localhost:8080/api/v1/student/deleted
```

**View student history:**
```
http://localhost:8080/api/v1/student/1/history
```

**View activity logs:**
```
http://localhost:8080/api/v1/activity-logs
```

---

## 📁 Project Structure

```
project-root/
├── src/
│   ├── main/
│   │   └── java/com/example/demo/student/
│   │       ├── Student.java                    # Student entity
│   │       ├── StudentRepository.java          # Database queries for students
│   │       ├── StudentService.java             # Business logic
│   │       ├── StudentController.java          # REST API endpoints
│   │       ├── StudentHistory.java             # History tracking entity
│   │       ├── StudentHistoryRepository.java   # History queries
│   │       ├── ActivityLog.java                # Activity logging entity
│   │       ├── ActivityLogRepository.java      # Activity log queries
│   │       ├── ActivityLogService.java         # Logging service
│   │       └── ActivityLogController.java      # Activity log endpoint
│   └── test/
│       └── java/com/example/demo/student/
│           └── StudentServiceIntegrationTest.java  # Integration tests
├── test_api.sh                 # Bash API testing script
├── reset_database.sql          # Database reset script
├── pom.xml                     # Maven configuration
└── README.md                   # This file
```

### Key Components

**Entities:**
- `Student` - Main student record with soft delete flag
- `StudentHistory` - Tracks old versions before updates/deletes
- `ActivityLog` - Logs all CRUD operations

**Repositories:**
- Custom queries for finding active/deleted students
- History retrieval ordered by timestamp

**Service Layer:**
- `StudentService` - Core business logic with transaction management
- `ActivityLogService` - Centralized activity logging

**Controllers:**
- REST endpoints with proper HTTP methods
- Request/response handling

---

## 📊 Maintenance & Testing Documentation

### Type of Maintenance Performed

**Adaptive Maintenance**: Enhanced the existing CRUD application with new features to meet changing business requirements:
- Added soft delete capability for data retention
- Implemented version history for audit trails
- Created activity logging for compliance

### Testing Activities Performed

#### 1. **Integration Testing**
- Created 21 JUnit integration tests covering all service methods
- Tests interact with actual database using Spring Boot test context
- All tests use `@Transactional` to ensure clean state between tests

**Coverage includes:**
- CRUD operations validation
- Business rule enforcement (duplicate emails)
- History tracking verification
- Activity logging verification
- Error handling and edge cases

#### 2. **API Endpoint Testing**
- Comprehensive bash script with 14 test scenarios
- Tests all REST endpoints with various inputs
- Validates both success and failure cases
- Tests complex workflows (create → update → delete → restore)

#### 3. **Manual Testing**
- Browser-based testing of GET endpoints
- Visual verification of JSON responses
- Database inspection using pgAdmin

#### 4. **Regression Testing**
- Ensured existing CRUD operations still work after enhancements
- Verified no breaking changes to original functionality
- All 19 integration tests pass consistently

### Activities Not Performed

**Performance Testing**: Not performed due to small dataset and local development environment. In production, would need load testing for concurrent operations.

**Security Testing**: Authentication/authorization not implemented as project focuses on CRUD functionality. In production would need JWT tokens or OAuth.

**UI Testing**: Application is backend-only (REST API). Frontend testing would be needed if UI is added.

### Challenges Faced

1. **Database Sequence Management**: After multiple tests, ID sequences would increment to high numbers. Resolved by creating database reset script with sequence restart commands.

2. **Transaction Management**: Ensuring history is saved BEFORE entity updates required careful use of `@Transactional` annotation and proper ordering of save operations.

3. **Test Data Cleanup**: Initially tests were interfering with each other. Solved by using `@Transactional` rollback and `@BeforeEach` cleanup methods.

4. **Soft Delete Query Logic**: Required custom JPQL queries to filter active vs deleted students. Implemented `@Query` annotations in repositories.

---

## 📝 Notes

- The application uses **soft delete** - records are never permanently removed unless using the `hardDeleteStudent` method (admin only)
- All timestamps in `StudentHistory` and `ActivityLog` use `LocalDateTime` with system time
- Email addresses must be unique across all students (both active and deleted)
- The `age` field in `Student` entity is calculated dynamically from date of birth
