package com.rainbowhospitals.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rainbowhospitals.model.DoctorSchedule;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long>{
	

}
