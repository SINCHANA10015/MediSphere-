package com.rainbowhospitals.model;

import java.time.LocalDate;

import com.rainbowhospitals.util.Gender;
import com.rainbowhospitals.util.PatientEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(value=PatientEntityListener.class)
@Builder
public class Patient {
	
	@Id
	private String patientId;
	
	private String patientName;
	
	@Enumerated(EnumType.STRING)
	private Gender gender;
	
	private String patientEmail;
	
	private String patientPhoneNumber;
	
	private LocalDate dateOfBirth;
	
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="patient_address_id")
	private PatientAddress patientAddress;
	
	

}
