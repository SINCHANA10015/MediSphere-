package com.rainbowhospitals.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientAddressResponseDto {
	
	private long patientAddressId;

	private String doorNumber;
	
	private String landmark;
	
	private String city;
	
	private String state;
	
	private String country;
	
	private String pincode;

}
