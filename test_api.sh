#!/bin/bash

# Student Management API - Complete Testing Script
# Base URL
BASE_URL="http://localhost:8080/api/v1"

echo "=========================================="
echo "STUDENT MANAGEMENT API - TEST SUITE"
echo "=========================================="

# ============================================
# 1. CREATE STUDENTS (POST)
# ============================================
echo -e "\n[TEST 1] Creating new students..."

echo "→ Creating Student 1: Alice"
curl -X POST $BASE_URL/student \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice Johnson","email":"alice@test.com","dob":"2000-01-15"}'

echo -e "\n→ Creating Student 2: Bob"
curl -X POST $BASE_URL/student \
  -H "Content-Type: application/json" \
  -d '{"name":"Bob Smith","email":"bob@test.com","dob":"1998-05-20"}'

echo -e "\n→ Creating Student 3: Charlie"
curl -X POST $BASE_URL/student \
  -H "Content-Type: application/json" \
  -d '{"name":"Charlie Brown","email":"charlie@test.com","dob":"2001-08-30"}'

# ============================================
# 2. GET ALL ACTIVE STUDENTS
# ============================================
echo -e "\n\n[TEST 2] Getting all active students..."
curl -X GET $BASE_URL/student

# ============================================
# 3. UPDATE STUDENT (PUT)
# ============================================
echo -e "\n\n[TEST 3] Updating student information..."

echo "→ Update Student 1 - Change name only"
curl -X PUT "$BASE_URL/student/1?name=Alice%20Marie%20Johnson"

echo -e "\n→ Update Student 1 - Change email only"
curl -X PUT "$BASE_URL/student/1?email=alice.johnson@test.com"

echo -e "\n→ Update Student 2 - Change both name and email"
curl -X PUT "$BASE_URL/student/2?name=Robert%20Smith&email=robert.smith@test.com"

# ============================================
# 4. TEST DUPLICATE EMAIL (Should fail)
# ============================================
echo -e "\n\n[TEST 4] Testing duplicate email validation..."
echo "→ Attempting to create student with existing email (should fail)"
curl -X POST $BASE_URL/student \
  -H "Content-Type: application/json" \
  -d '{"name":"Duplicate User","email":"alice.johnson@test.com","dob":"1995-01-01"}'

echo -e "\n→ Attempting to update to existing email (should fail)"
curl -X PUT "$BASE_URL/student/3?email=alice.johnson@test.com"

# ============================================
# 5. GET STUDENT HISTORY
# ============================================
echo -e "\n\n[TEST 5] Getting student history..."
echo "→ History for Student 1 (should show 2 updates)"
curl -X GET $BASE_URL/student/1/history

echo -e "\n→ History for Student 2 (should show 1 update)"
curl -X GET $BASE_URL/student/2/history

echo -e "\n→ History for Student 3 (should be empty)"
curl -X GET $BASE_URL/student/3/history

# ============================================
# 6. SOFT DELETE STUDENT
# ============================================
echo -e "\n\n[TEST 6] Soft deleting students..."
echo "→ Deleting Student 3"
curl -X DELETE $BASE_URL/student/3

echo -e "\n→ Verify Student 3 is no longer in active list"
curl -X GET $BASE_URL/student

# ============================================
# 7. GET DELETED STUDENTS
# ============================================
echo -e "\n\n[TEST 7] Getting deleted students..."
curl -X GET $BASE_URL/student/deleted

# ============================================
# 8. TEST OPERATIONS ON DELETED STUDENT (Should fail)
# ============================================
echo -e "\n\n[TEST 8] Testing operations on deleted student..."
echo "→ Attempting to update deleted student (should fail)"
curl -X PUT "$BASE_URL/student/3?name=Should%20Fail"

echo -e "\n→ Attempting to delete already deleted student (should fail)"
curl -X DELETE $BASE_URL/student/3

# ============================================
# 9. RESTORE DELETED STUDENT
# ============================================
echo -e "\n\n[TEST 9] Restoring deleted student..."
echo "→ Restoring Student 3"
curl -X PUT $BASE_URL/student/3/restore

echo -e "\n→ Verify Student 3 is back in active list"
curl -X GET $BASE_URL/student

# ============================================
# 10. TEST RESTORE ON NON-DELETED STUDENT (Should fail)
# ============================================
echo -e "\n\n[TEST 10] Testing restore on active student..."
echo "→ Attempting to restore active student (should fail)"
curl -X PUT $BASE_URL/student/1/restore

# ============================================
# 11. VIEW ACTIVITY LOGS
# ============================================
echo -e "\n\n[TEST 11] Viewing all activity logs..."
curl -X GET $BASE_URL/activity-logs

# ============================================
# 12. TEST NON-EXISTENT STUDENT (Should fail)
# ============================================
echo -e "\n\n[TEST 12] Testing operations on non-existent student..."
echo "→ Get history for non-existent student ID 999"
curl -X GET $BASE_URL/student/999/history

echo -e "\n→ Update non-existent student"
curl -X PUT "$BASE_URL/student/999?name=Ghost"

echo -e "\n→ Delete non-existent student"
curl -X DELETE $BASE_URL/student/999

# ============================================
# 13. COMPLEX WORKFLOW TEST
# ============================================
echo -e "\n\n[TEST 13] Complex workflow - Create, Update, Delete, Check History..."

echo "→ Creating Student 4: Diana"
curl -X POST $BASE_URL/student \
  -H "Content-Type: application/json" \
  -d '{"name":"Diana Prince","email":"diana@test.com","dob":"1999-12-25"}'

echo -e "\n→ First update - Change name"
curl -X PUT "$BASE_URL/student/4?name=Diana%20Prince-Wayne"

echo -e "\n→ Second update - Change email"
curl -X PUT "$BASE_URL/student/4?email=wonderwoman@test.com"

echo -e "\n→ Third update - Change both"
curl -X PUT "$BASE_URL/student/4?name=Wonder%20Woman&email=ww@test.com"

echo -e "\n→ Delete student"
curl -X DELETE $BASE_URL/student/4

echo -e "\n→ View complete history (should show 4 entries: 3 updates + 1 delete)"
curl -X GET $BASE_URL/student/4/history

# ============================================
# 14. EDGE CASES
# ============================================
echo -e "\n\n[TEST 14] Testing edge cases..."

echo "→ Update with empty parameters (should do nothing)"
curl -X PUT "$BASE_URL/student/1?name=&email="

echo -e "\n→ Update with same values (should not create history entry)"
curl -X PUT "$BASE_URL/student/1?name=Alice%20Marie%20Johnson&email=alice.johnson@test.com"

echo -e "\n\n=========================================="
echo "TEST SUITE COMPLETED"
echo "=========================================="
echo -e "\nTo view formatted JSON responses, pipe through jq:"
echo "curl -X GET $BASE_URL/student | jq"
