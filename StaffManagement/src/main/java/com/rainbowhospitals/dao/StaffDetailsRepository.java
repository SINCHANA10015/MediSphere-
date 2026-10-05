package com.rainbowhospitals.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rainbowhospitals.model.StaffDetails;

public interface StaffDetailsRepository extends JpaRepository<StaffDetails, Long> {
	
	Optional< StaffDetails> findByEmail(String email);
	
	
	

}
