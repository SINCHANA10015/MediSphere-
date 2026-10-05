package com.rainbowhospitals.builder;

import java.util.List;

import com.rainbowhospitals.dto.PatientAddressResponseDto;
import com.rainbowhospitals.dto.PatientResponseDto;
import com.rainbowhospitals.model.Patient;
import com.rainbowhospitals.model.PatientAddress;

public class PatientResponseBuilder {
	
	public static PatientResponseDto buildPatientResponseDtoFromPatient(Patient patient) {
		PatientResponseDto patientResponseDto = new PatientResponseDto();
		return patientResponseDto.builder()
				.patientId(patient.getPatientId())
				.gender(patient.getGender())
				.dateOfBirth(patient.getDateOfBirth())
				.patientEmail(patient.getPatientEmail())
				.patientName(patient.getPatientName())
				.patientPhoneNumber(patient.getPatientPhoneNumber())
				.patientAddress(buildPatientAddressResponseDtoFromPatientAddress(patient.getPatientAddress()))
				.build();
		
	}
	public static PatientAddressResponseDto buildPatientAddressResponseDtoFromPatientAddress(PatientAddress patientAddress) {
		PatientAddressResponseDto patientAddressResponseDto = new PatientAddressResponseDto();
		return patientAddressResponseDto.builder()
				.doorNumber(patientAddress.getDoorNumber())
				.city(patientAddress.getCity())
				.landmark(patientAddress.getLandmark())
				.pincode(patientAddress.getPincode())
				.state(patientAddress.getState())
				.country(patientAddress.getCountry())
				.build();
	}
	public static List<PatientResponseDto> fromListOfPatientToListOfPatientResponseDto(List<Patient> patients){
		return patients.stream().map(patient->buildPatientResponseDtoFromPatient(patient)).toList();
	}

}
