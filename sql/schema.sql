CREATE DATABASE IF NOT EXISTS intern_tracker;
USE intern_tracker;

CREATE TABLE students (
    student_id   INT PRIMARY KEY AUTO_INCREMENT,
    name         VARCHAR(100) NOT NULL,
    email        VARCHAR(100) UNIQUE NOT NULL,
    branch       VARCHAR(50),
    year         INT CHECK (year BETWEEN 1 AND 4),
    cgpa         DECIMAL(4,2) CHECK (cgpa BETWEEN 0 AND 10),
    password     VARCHAR(255),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE skills (
    skill_id     INT PRIMARY KEY AUTO_INCREMENT,
    student_id   INT NOT NULL,
    skill_name   VARCHAR(100),
    level        ENUM('Beginner','Intermediate','Advanced') DEFAULT 'Beginner',
    verified     BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

CREATE TABLE certifications (
    cert_id      INT PRIMARY KEY AUTO_INCREMENT,
    student_id   INT NOT NULL,
    cert_name    VARCHAR(150),
    issuer       VARCHAR(100),
    issue_date   DATE,
    cert_url     VARCHAR(255),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

CREATE TABLE internships (
    intern_id        INT PRIMARY KEY AUTO_INCREMENT,
    company          VARCHAR(100),
    role             VARCHAR(100),
    location         VARCHAR(100),
    stipend          DECIMAL(10,2) DEFAULT 0,
    required_skills  VARCHAR(255),
    deadline         DATE,
    source           ENUM('manual','api') DEFAULT 'manual',
    job_url          VARCHAR(255),
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE applications (
    app_id       INT PRIMARY KEY AUTO_INCREMENT,
    student_id   INT NOT NULL,
    intern_id    INT NOT NULL,
    status       ENUM('Applied','Shortlisted','Rejected','Accepted') DEFAULT 'Applied',
    applied_on   DATE DEFAULT (CURDATE()),
    notes        TEXT,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (intern_id)  REFERENCES internships(intern_id) ON DELETE CASCADE
);

CREATE TABLE recommendations (
    rec_id       INT PRIMARY KEY AUTO_INCREMENT,
    student_id   INT NOT NULL,
    type         ENUM('course','internship'),
    title        VARCHAR(200),
    url          VARCHAR(255),
    reason       TEXT,
    generated_on DATETIME DEFAULT NOW(),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- Sample data to test with
INSERT INTO students(name,email,branch,year,cgpa,password)
VALUES ('Rahul Sharma','rahul@test.com','CSE',3,8.5,'pass123'),
       ('Priya Patel','priya@test.com','IT',2,7.8,'pass123');

INSERT INTO internships(company,role,location,stipend,required_skills,deadline,source)
VALUES ('Google','SWE Intern','Bangalore',50000,'Java,Python,DSA','2025-08-31','manual'),
       ('Microsoft','Cloud Intern','Hyderabad',40000,'Azure,Python,SQL','2025-07-15','manual'),
       ('Infosys','Web Dev Intern','Pune',15000,'HTML,CSS,JavaScript','2025-09-30','manual');