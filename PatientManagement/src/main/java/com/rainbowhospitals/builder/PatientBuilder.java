package com.rainbowhospitals.builder;

import com.rainbowhospitals.dto.PatientAddressRequestDto;
import com.rainbowhospitals.dto.PatientRequestDto;
import com.rainbowhospitals.model.Patient;
import com.rainbowhospitals.model.PatientAddress;

public class PatientBuilder {
	
	
	public static Patient buildPatientFromPatientRequestDto(PatientRequestDto patientRequestDto) {
		Patient patient = new Patient();
		System.out.println(patientRequestDto.getGender());
		return patient.builder()
				.patientName(patientRequestDto.getPatientName())
				.gender(patientRequestDto.getGender())
				.dateOfBirth(patientRequestDto.getDateOfBirth())
				.patientEmail(patientRequestDto.getPatientEmail())
				.patientPhoneNumber(patientRequestDto.getPatientPhoneNumber())
				.patientAddress(buildPatientAddressFromPatientAddressRequestDto(patientRequestDto.getPatientAddressRequestDto())
						)
				.build();
		
		
	}
	
	public static PatientAddress buildPatientAddressFromPatientAddressRequestDto(PatientAddressRequestDto patientAddressRequestDto)
	{
		PatientAddress patientAddress = new PatientAddress();
		return patientAddress.builder()
				.landmark(patientAddressRequestDto.getLandmark())
				.city(patientAddressRequestDto.getCity())
				.doorNumber(patientAddressRequestDto.getDoorNumber())
				.country(patientAddressRequestDto.getCountry())
				.state(patientAddressRequestDto.getState())
				.pincode(patientAddressRequestDto.getPincode())
				.build();
	}
}
