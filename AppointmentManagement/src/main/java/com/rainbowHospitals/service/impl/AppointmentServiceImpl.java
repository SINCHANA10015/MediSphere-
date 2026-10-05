package com.rainbowHospitals.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.rainbowHospitals.builder.AppointmentBuilder;
import com.rainbowHospitals.dao.AppointmentRepository;
import com.rainbowHospitals.dto.AppointmentRequestDto;
import com.rainbowHospitals.dto.AppointmentResponseDto;
import com.rainbowHospitals.exception.InvalidDateSelectionException;
import com.rainbowHospitals.model.Appointment;
import com.rainbowHospitals.service.AppointmentService;

@Service
public class AppointmentServiceImpl implements AppointmentService {
	
	private final AppointmentRepository appointmentRepository;
	
	public AppointmentServiceImpl(AppointmentRepository appointmentRepository) {
		this.appointmentRepository = appointmentRepository;
	}

	@Override
	public AppointmentResponseDto bookAppointment(AppointmentRequestDto appointmentRquestDto) {
		Appointment appointment = AppointmentBuilder.buildAppointmentFromAppointmentRequestDto(appointmentRquestDto);
		if(appointment.getAppointmentDate().isBefore(LocalDate.now())) {
			return new InvalidDateSelectionException("Selected date is invalid");
		}
		return null;
	}

}
