package com.rainbowhospitals.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rainbowhospitals.model.Patient;

import jakarta.persistence.PrePersist;

@Component
public class PatientEntityListener {

	public static PatientIdGenerator patientIdGenerator;
	
	@Autowired
	public void init(PatientIdGenerator patientIdGenerator) {
		this.patientIdGenerator = patientIdGenerator;
	}
	
	@PrePersist
	public void generatePatientId(Patient patient) {
		if(patient.getPatientId()==null) {
			String nextPatientId = patientIdGenerator.generateNextPatientId();
			patient.setPatientId(nextPatientId);
		}
	}
}
