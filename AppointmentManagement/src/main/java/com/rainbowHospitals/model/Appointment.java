package com.rainbowHospitals.model;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.web.bind.annotation.RequestMapping;

import com.rainbowHospitals.util.AppointmentEntityListner;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="appointments")
@Builder
@RequestMapping("/appointment")
@EntityListeners(value=AppointmentEntityListner.class)
public class Appointment {
	
	@Id
	@Column(name="appointment_id", nullable = false, unique = true)
	private Long appointmentId;
	
	private String patientId;

	private String doctorId;
	
	private LocalDate appointmentDate;
	
	private LocalTime startTime;

	private LocalTime endTime;

	private String status;

	private String notes;

	private String reasonForVisit;

	public Appointment(String patientId, String doctorId, LocalDate appointmentDate, LocalTime startTime,
			LocalTime endTime, String status, String notes, String reasonForVisit) {
		super();
		this.patientId = patientId;
		this.doctorId = doctorId;
		this.appointmentDate = appointmentDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
		this.notes = notes;
		this.reasonForVisit = reasonForVisit;
	}
	
	


}
