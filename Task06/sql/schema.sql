-- =============================================================================
-- SIMATS ENGINEERING - PROGRAMMING IN JAVA (CSA09)
-- ASSIGNMENT 4 (CO4): EVENT-DRIVEN GUI DESIGN - TASK 6 DATABASE SCHEMA
-- =============================================================================
-- Description: MySQL Database creation script, table definitions, constraints,
--              and sample seed data for Campus Event Registration Application.
-- =============================================================================

CREATE DATABASE IF NOT EXISTS campus_event_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE campus_event_db;

-- Drop table if exists for clean deployment
DROP TABLE IF EXISTS event_registrations;

-- Table definition for Event Registrations
CREATE TABLE event_registrations (
    registration_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL,
    department VARCHAR(80) NOT NULL,
    event_name VARCHAR(100) NOT NULL,
    registration_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Business Constraint: A student cannot register for the same event more than once
    CONSTRAINT uk_student_event UNIQUE (student_id, event_name),
    
    -- Indexes for high-performance filtering and sorting
    INDEX idx_event_name (event_name),
    INDEX idx_student_id (student_id),
    INDEX idx_reg_time (registration_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed Sample Data for initial testing and demonstration
INSERT INTO event_registrations (student_id, student_name, email, department, event_name) VALUES
('2024CS101', 'Aditya Sharma', 'aditya.sharma@simats.edu', 'Computer Science & Engineering', 'Hackathon 2026'),
('2024CS102', 'Pooja Venkatesh', 'pooja.v@simats.edu', 'Computer Science & Engineering', 'AI & Robotics Expo'),
('2024IT201', 'Rahul Nair', 'rahul.nair@simats.edu', 'Information Technology', 'Web Development Sprint'),
('2024EC305', 'Sneha Krishnan', 'sneha.k@simats.edu', 'Electronics & Communication', 'IoT Design Challenge'),
('2024ME402', 'Vikramaditya Rao', 'vikram.rao@simats.edu', 'Mechanical Engineering', 'CAD Modeling Contest'),
('2024CS105', 'Ananya Deshmukh', 'ananya.d@simats.edu', 'Computer Science & Engineering', 'Hackathon 2026'),
('2024BT108', 'Karthik Sundaram', 'karthik.s@simats.edu', 'Biotechnology', 'Bio-Informatics Symposium'),
('2024IT208', 'Meera Subramaniam', 'meera.s@simats.edu', 'Information Technology', 'AI & Robotics Expo');

-- Verification Query
SELECT * FROM event_registrations;
