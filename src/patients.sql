SELECT adm.admission_id, p.first_name, p.last_name, r.room_number, r.room_type, adm.admission_date
FROM Admissions adm
JOIN Patients p ON adm.patient_id = p.patient_id
JOIN Rooms r ON adm.room_id = r.room_id
WHERE adm.discharge_date IS NULL;

SELECT p.patient_id, p.first_name, p.last_name, SUM(b.total_amount) AS total_billed
FROM Billing b
JOIN Patients p ON b.patient_id = p.patient_id
GROUP BY p.patient_id, p.first_name, p.last_name
ORDER BY total_billed DESC;

SELECT d.doctor_id, d.first_name, d.last_name, COUNT(a.appointment_id) AS total_appointments
FROM Doctors d
LEFT JOIN Appointments a ON d.doctor_id = a.doctor_id
GROUP BY d.doctor_id, d.first_name, d.last_name
ORDER BY total_appointments DESC;