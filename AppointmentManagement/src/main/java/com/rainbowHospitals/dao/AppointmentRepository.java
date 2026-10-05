package com.rainbowHospitals.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rainbowHospitals.model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, String> {

	String findLastAppointmentId(String formatCurrentDateTime);

}
