package com.rainbowHospitals.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rainbowHospitals.dto.AppointmentRequestDto;
import com.rainbowHospitals.dto.AppointmentResponseDto;
import com.rainbowHospitals.service.AppointmentService;


@RestController
@RequestMapping("/appoimtment")
public class AppointmentController {
	
	private final AppointmentService appointmentService;
	
	public AppointmentController(AppointmentService appointmentService) {
		this.appointmentService = appointmentService;
	}
	
	@PostMapping("/bookAppointment")
	public ResponseEntity<AppointmentResponseDto> bookAppointment(@RequestBody AppointmentRequestDto appointmentRequestDto) {		  
		AppointmentResponseDto appointmentResponseDto = appointmentService.bookAppointment(appointmentRequestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(appointmentResponseDto);
	}

}
