package com.rainbowhospitals.serviceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.rainbowhospitals.dao.DoctorScheduleRepository;
import com.rainbowhospitals.dao.StaffRepository;
import com.rainbowhospitals.enums.StaffType;
import com.rainbowhospitals.exceptions.NoDoctorFoundWithIdException;
import com.rainbowhospitals.exceptions.StaffNotFoundException;
import com.rainbowhospitals.model.Staff;
import com.rainbowhospitals.service.DoctorAvailabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DoctorAvailabilityImpl implements DoctorAvailabilityService {

	private final DoctorScheduleRepository doctorScheduleRepository;

	private final StaffRepository staffRepository;

	public DoctorAvailabilityImpl(DoctorScheduleRepository doctorScheduleRepository,StaffRepository staffRepository) {
		this.doctorScheduleRepository = doctorScheduleRepository;
		this.staffRepository = staffRepository;
	}

	@Override
	public ResponseEntity<String> markDoctorAvailable(String staffId, List<LocalDate> dates) {
		if(dates==null || dates.isEmpty()) {
			throw new IllegalArgumentException("Please select atleast one date");
		}
		Staff staff = staffRepository.findById(staffId).orElseThrow(()->new StaffNotFoundException("No staff Found with given id"));
		if(staff.getStaffType()!= StaffType.DOCTOR) {
			throw new NoDoctorFoundWithIdException("Staff with ID " + staffId + " is not a doctor");
		}
		LocalDate today = LocalDate.now();
		List<LocalDate> invalidDates = new ArrayList<>();
		List<LocalDate> processedDates = new ArrayList<>();

		for(LocalDate eachDate : dates) {
			if(eachDate.isBefore(today))
			{
				invalidDates.add(eachDate);
			}

			if(!invalidDates.isEmpty()) {
				throw new IllegalArgumentException("Cannot mark availability for past dates: " + invalidDates);
			}
			for(LocalDate date : dates) {
				doctorScheduleRepository.findByUnavailableDate(date);;

			}
		}

        return null;
    }

}
