package com.rainbowHospitals.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import groovy.transform.builder.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequestDto {
	
	private String patientId;

	private String doctorId;
	
	private LocalDate appointmentDate;
	
	private LocalTime startTime;

	private LocalTime endTime;

	private String notes;

	private String reasonForVisit;

}
