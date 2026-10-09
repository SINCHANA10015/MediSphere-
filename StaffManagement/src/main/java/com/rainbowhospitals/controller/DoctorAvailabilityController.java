package com.rainbowhospitals.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rainbowhospitals.service.DoctorAvailabilityService;

@RestController
public class DoctorAvailabilityController {

	private final DoctorAvailabilityService doctorAvailabilityService;

	public DoctorAvailabilityController(DoctorAvailabilityService doctorAvailabilityService){
		this.doctorAvailabilityService = doctorAvailabilityService;
	}

	private ResponseEntity<String> markDoctorAvailable(@RequestParam("staffId") String staffId, @RequestBody List<LocalDate> dates)
	{
		return	doctorAvailabilityService.markDoctorAvailable(staffId, dates);
	}
}
