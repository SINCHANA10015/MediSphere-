package com.rainbowHospitals.builder;

import com.rainbowHospitals.dto.AppointmentRequestDto;
import com.rainbowHospitals.model.Appointment;

public class AppointmentBuilder {
	
	public static Appointment buildAppointmentFromAppointmentRequestDto(AppointmentRequestDto appointmentRequestDto) {
		return Appointment.builder()
				.patientId(appointmentRequestDto.getPatientId())
				.appointmentDate(appointmentRequestDto.getAppointmentDate())
				.appointmentDate(appointmentRequestDto.getAppointmentDate())
				.notes(appointmentRequestDto.getNotes())
				.doctorId(appointmentRequestDto.getDoctorId())
				.endTime(appointmentRequestDto.getEndTime())
				.startTime(appointmentRequestDto.getStartTime())
				.reasonForVisit(appointmentRequestDto.getReasonForVisit())
				.build();
		
		
	}

}
