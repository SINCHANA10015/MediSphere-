package com.rainbowhospitals.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rainbowhospitals.dto.PatientRequestDto;
import com.rainbowhospitals.dto.PatientResponseDto;
import com.rainbowhospitals.service.PatientService;

@RestController
@RequestMapping("/patient")
public class PatientController {

	private final PatientService patientService;

	public PatientController(PatientService patientService) {
		this.patientService = patientService;
	}

	@PostMapping("/register-patient")
	public ResponseEntity<PatientResponseDto> registerPatient(
			@RequestBody PatientRequestDto registerPatientRequestDto) {
		return ResponseEntity.ok().body(patientService.registerPatient(registerPatientRequestDto));
	}

	@GetMapping("/get-all-patients")
	public ResponseEntity<List<PatientResponseDto>> fetchAllPatients() {
		return ResponseEntity.ok().body(patientService.findAllPatients());

	}

	@GetMapping("/get-by-id/{id}")
	public ResponseEntity<PatientResponseDto> findPatientById(@PathVariable String id) {
		return ResponseEntity.ok().body(patientService.findPatientByPatientId(id));
	}

	@PutMapping("/patientDetails/{id}")
	public ResponseEntity<PatientResponseDto> updatePatientDetails(@PathVariable(name = "id") String patientId,
			@RequestBody PatientRequestDto patientRequestDto) {
		return ResponseEntity.ok().body(patientService.updatePatient(patientRequestDto, patientId));

	}

	@DeleteMapping("/delete/{patientId}")
	public ResponseEntity<String> deletePatient(@PathVariable(name = "patientId") String id) {
		return patientService.deletePatient(id);
	}
	
	@GetMapping("/getPatientName/{id}")
	public String getPatientName(@PathVariable String id) {
		return patientService.getPatientName(id);
	}

}
