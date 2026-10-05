package com.rainbowhospitals.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface DoctorAvailabilityService {

	void markDoctorAvailable(String staffId, List<LocalDate> dates);

}
