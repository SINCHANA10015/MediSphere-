package com.rainbowHospitals.service;

import org.springframework.stereotype.Service;

import com.rainbowHospitals.dto.AppointmentRequestDto;
import com.rainbowHospitals.dto.AppointmentResponseDto;

@Service
public interface AppointmentService {

	AppointmentResponseDto bookAppointment(AppointmentRequestDto appointmentRequestDto);

}
