package com.rainbowHospitals.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.rainbowHospitals.model.Appointment;
import org.springframework.stereotype.Component;

import com.rainbowHospitals.dao.AppointmentRepository;

@Component
public class AppointmentIdGenerator {

    private final Appointment appointment;
	
	private final AppointmentRepository appointmentRepository;
	
	public AppointmentIdGenerator(AppointmentRepository appointmentRepository, Appointment appointment) {
		this.appointmentRepository = appointmentRepository;
		this.appointment = appointment;
	}
	
	public Long generateNextAppointmentId() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMddyyyyHHmmss");
		String formattedCurrentDateTime = currentDateTime.format(dateTimeFormatter);
		String lastAppointmentId = appointmentRepository.findLastAppointmentId(formattedCurrentDateTime);
	
	int nextNumber =1;
	String appointmentIdSuffix = "";
	String appointmentId = "";
	if(lastAppointmentId != null && lastAppointmentId.startsWith(formattedCurrentDateTime)) {
		String idSuffix = lastAppointmentId.substring(14);
		nextNumber = Integer.parseInt(idSuffix) + 1;
		appointmentIdSuffix = String.format("%05d",nextNumber);
		appointmentId = formattedCurrentDateTime + appointmentIdSuffix;
		return Long.parseLong(appointmentId);
	}
	else {
		appointmentIdSuffix = String.format("%05d",nextNumber);
		appointmentId = formattedCurrentDateTime + appointmentIdSuffix;
		return Long.parseLong(appointmentId);
	}
	}

}
