CREATE DATABASE hospital1;
USE hospital1;
CREATE TABLE Patients (
    patient_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender ENUM('Male','Female','Other') NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100)
);
CREATE TABLE Doctors (
    doctor_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    specialty VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100)
);
CREATE TABLE Departments (
    department_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(100)
);
CREATE TABLE Appointments (
    appointment_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT,
    doctor_id INT,
    department_id INT,
    appointment_date DATETIME NOT NULL,
    status ENUM('Scheduled','Completed','Cancelled') DEFAULT 'Scheduled',
    FOREIGN KEY (patient_id) REFERENCES Patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES Doctors(doctor_id) ON DELETE SET NULL,
    FOREIGN KEY (department_id) REFERENCES Departments(department_id) ON DELETE SET NULL
);
CREATE TABLE MedicalRecords (
    record_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    diagnosis VARCHAR(255),
    treatment VARCHAR(255),
    record_date DATETIME NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES Patients(patient_id) ON DELETE CASCADE
);
CREATE TABLE Prescriptions (
    prescription_id INT PRIMARY KEY AUTO_INCREMENT,
    record_id INT NOT NULL,
    medication VARCHAR(100) NOT NULL,
    dosage VARCHAR(50),
    duration VARCHAR(50),
    FOREIGN KEY (record_id) REFERENCES MedicalRecords(record_id) ON DELETE CASCADE
);
CREATE TABLE Rooms (
    room_id INT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(20) NOT NULL,
    department_id INT,
    room_type ENUM('General','ICU','Operation','Private'),
    capacity INT DEFAULT 1,
    FOREIGN KEY (department_id) REFERENCES Departments(department_id) ON DELETE SET NULL
);
CREATE TABLE Admissions (
    admission_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    room_id INT ,
    admission_date DATETIME NOT NULL,
    discharge_date DATETIME,
    FOREIGN KEY (patient_id) REFERENCES Patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES Rooms(room_id) ON DELETE SET NULL
);
CREATE TABLE Staff (
    staff_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role VARCHAR(50),
    department_id INT,
    phone VARCHAR(20),
    email VARCHAR(100),
    FOREIGN KEY (department_id) REFERENCES Departments(department_id) ON DELETE SET NULL
);
CREATE TABLE Billing (
    bill_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    admission_id INT,
    total_amount DECIMAL(10,2) NOT NULL,
    payment_status ENUM('Paid','Pending','Cancelled') DEFAULT 'Pending',
    FOREIGN KEY (patient_id) REFERENCES Patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (admission_id) REFERENCES Admissions(admission_id) ON DELETE SET NULL
);
CREATE TABLE LabTests (
    test_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    test_name VARCHAR(100),
    test_date DATETIME,
    result VARCHAR(255),
    FOREIGN KEY (patient_id) REFERENCES Patients(patient_id) ON DELETE CASCADE
);
SELECT a.appointment_id, a.appointment_date, a.status,
       p.first_name AS patient_first, p.last_name AS patient_last,
       d.first_name AS doctor_first, d.last_name AS doctor_last,
       dept.name AS department
FROM Appointments a
JOIN Patients p ON a.patient_id = p.patient_id
JOIN Doctors d ON a.doctor_id = d.doctor_id
JOIN Departments dept ON a.department_id = dept.department_id
WHERE a.status = 'Scheduled'
ORDER BY a.appointment_date;
SELECT mr.record_id, mr.diagnosis, mr.treatment, mr.record_date,
       pr.medication, pr.dosage, pr.duration
FROM MedicalRecords mr
LEFT JOIN Prescriptions pr ON mr.record_id = pr.record_id
WHERE mr.patient_id = 101; -- Replace 101 with patient_id