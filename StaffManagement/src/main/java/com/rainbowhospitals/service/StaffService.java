package com.rainbowhospitals.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.rainbowhospitals.dto.LoginRequest;
import com.rainbowhospitals.dto.LoginResponse;
import com.rainbowhospitals.dto.ResetPasswordRequest;
import com.rainbowhospitals.dto.StaffRequestDto;
import com.rainbowhospitals.dto.StaffResponseDto;
import com.rainbowhospitals.dto.VerifyOtpRequest;
import com.rainbowhospitals.enums.Specialization;

@Service
public interface StaffService {
	
	public StaffResponseDto registerStaff(StaffRequestDto registerStaffDto);

	public StaffResponseDto fetchStaffDetailsByStaffId(String staffId);

	public List<StaffResponseDto> fetchAllStaffDetails();

	public Specialization getSpecialization(String id);
	
	 
	public List<StaffResponseDto> findByStaffFirstNameOrLastName(String name);

	public ResponseEntity<StaffResponseDto> updateStaff(String id, StaffRequestDto registerStaffDto);


	 public String getDoctorName(String doctorId);

	 public void deleteStaff(String id);
		
}


