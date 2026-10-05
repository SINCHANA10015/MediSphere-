package com.rainbowhospitals.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.rainbowhospitals.model.Patient;

public interface PatientRepository extends JpaRepository<Patient, String>{
	
	@Query(value="select patient_id from patients ORDER BY patient_id desc limit 1",nativeQuery=true)
	String findLastPatiendId();
	
	boolean existsByPatientEmail(String email);
	
	Patient findByPatientPhoneNumber(String phoneNumber);

	boolean existsByPatientPhoneNumber(String phoneNumber);
	
}
