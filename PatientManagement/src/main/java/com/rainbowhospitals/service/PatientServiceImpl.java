package com.rainbowhospitals.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.rainbowhospitals.builder.PatientBuilder;
import com.rainbowhospitals.builder.PatientResponseBuilder;
import com.rainbowhospitals.dao.PatientRepository;
import com.rainbowhospitals.dto.PatientRequestDto;
import com.rainbowhospitals.dto.PatientResponseDto;
import com.rainbowhospitals.exception.EmailAlreadyRegistered;
import com.rainbowhospitals.exception.NoPatientFoundWithGivenId;
import com.rainbowhospitals.exception.PhoneNumberAlreadyExistsException;
import com.rainbowhospitals.model.Patient;

@Service
public class PatientServiceImpl implements PatientService{
	
	private final PatientRepository patientRepository;

	public PatientServiceImpl(PatientRepository patientRepository) {
		this.patientRepository = patientRepository;
	}
	@Override
	public PatientResponseDto registerPatient(PatientRequestDto patientRequestDto) {
		if(patientRepository.existsByPatientEmail(patientRequestDto.getPatientEmail())) {
			throw new EmailAlreadyRegistered("Email "+patientRequestDto.getPatientEmail() +" Already registered ");
		}
		if(patientRepository.existsByPatientPhoneNumber(patientRequestDto.getPatientPhoneNumber())) {
			throw new PhoneNumberAlreadyExistsException("PhoneNumber already exists");
		}
		Patient patient = PatientBuilder.buildPatientFromPatientRequestDto(patientRequestDto);
		System.out.println("gender" +patientRequestDto.getPatientAddressRequestDto().getState());
		Patient registeredPatient = patientRepository.save(patient);
	    return PatientResponseBuilder.buildPatientResponseDtoFromPatient(registeredPatient);
	}
	@Override
	public PatientResponseDto findPatientByPatientId(String id) {
		Patient patient = patientRepository.findById(id).orElseThrow(()->new NoPatientFoundWithGivenId("No patient found with give id "+id));
		return PatientResponseBuilder.buildPatientResponseDtoFromPatient(patient);
	}
	@Override
	public List<PatientResponseDto> findAllPatients() {
		List<Patient> allPatients = patientRepository.findAll();
		System.out.println(allPatients);
		return PatientResponseBuilder.fromListOfPatientToListOfPatientResponseDto(allPatients);
		
	}
	@Override
	public PatientResponseDto updatePatient(PatientRequestDto patientRequestDto,String patientId) {
		Patient existingPatient = patientRepository.findById(patientId).orElseThrow(()->new NoPatientFoundWithGivenId("No patient found with give id "+patientId));
		Patient updatedPatient = PatientBuilder.buildPatientFromPatientRequestDto(patientRequestDto);
		updatedPatient.setPatientId(patientId);
		if(existingPatient.getPatientAddress()!=null || updatedPatient.getPatientAddress() != null) {
	    updatedPatient.getPatientAddress().setPatientAddressId(existingPatient.getPatientAddress().getPatientAddressId());
		}
		Patient savedPatient = patientRepository.save(updatedPatient);
		
		return  PatientResponseBuilder.buildPatientResponseDtoFromPatient(savedPatient);
	}
	@Override
	public ResponseEntity<String> deletePatient(String id) {
		Patient patient = patientRepository.findById(id).orElseThrow(()->new NoPatientFoundWithGivenId("No patient found with give id "+id));
		patientRepository.delete(patient);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Successfully deleted patient");
	}
	@Override
	public String getPatientName(String id) {
		Patient patient = patientRepository.findById(id).orElseThrow(()->new NoPatientFoundWithGivenId("No patient found with give id "+id));
		return patient.getPatientName();
		
	}
	
	

}
