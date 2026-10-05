package com.rainbowhospitals.dto;

import java.time.LocalDate;

import com.rainbowhospitals.util.Gender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PatientResponseDto {
	
private String patientId;
	
	private String patientName;
	
	private Gender gender;
	
	private String patientEmail;
	
	private String patientPhoneNumber;
	
	private LocalDate dateOfBirth;

	private PatientAddressResponseDto patientAddress;

}
