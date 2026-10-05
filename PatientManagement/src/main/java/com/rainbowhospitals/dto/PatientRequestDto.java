package com.rainbowhospitals.dto;

import java.time.LocalDate;

import com.rainbowhospitals.util.Gender;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientRequestDto {
		
	private String patientName;
	
	@Enumerated(EnumType.STRING)
	private Gender gender;
	
	private String patientEmail;
	
	private String patientPhoneNumber;
	
	private LocalDate dateOfBirth;
	
	private PatientAddressRequestDto patientAddressRequestDto;

}
