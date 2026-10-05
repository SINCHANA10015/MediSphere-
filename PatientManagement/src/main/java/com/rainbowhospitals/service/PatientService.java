package com.rainbowhospitals.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.rainbowhospitals.dto.PatientRequestDto;
import com.rainbowhospitals.dto.PatientResponseDto;

public interface PatientService {

	PatientResponseDto registerPatient(PatientRequestDto registerPatientRequestDto);

	PatientResponseDto findPatientByPatientId(String id);

	List<PatientResponseDto> findAllPatients();

	PatientResponseDto updatePatient(PatientRequestDto patientRequestDto,String patientId);

	ResponseEntity<String> deletePatient(String id);

	String getPatientName(String id);

}
