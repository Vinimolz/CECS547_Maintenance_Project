-- Delete all data and reset sequences
TRUNCATE TABLE activity_log RESTART IDENTITY CASCADE;
TRUNCATE TABLE student_history RESTART IDENTITY CASCADE;
TRUNCATE TABLE student RESTART IDENTITY CASCADE;

-- Reset all sequences to start from 1
ALTER SEQUENCE student_sequence RESTART WITH 1;
ALTER SEQUENCE student_history_history_id_seq RESTART WITH 1;
ALTER SEQUENCE activity_log_log_id_seq RESTART WITH 1;

-- Verify they're reset (should all show 1)
SELECT last_value FROM student_sequence;
SELECT last_value FROM student_history_history_id_seq;
SELECT last_value FROM activity_log_log_id_seq;