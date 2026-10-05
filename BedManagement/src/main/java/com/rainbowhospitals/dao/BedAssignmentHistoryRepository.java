package com.rainbowhospitals.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rainbowhospitals.model.BedAssignmentHistory;

public interface BedAssignmentHistoryRepository extends JpaRepository<BedAssignmentHistory, Long> {

	
	BedAssignmentHistory findTopByBed_BedNumberAndVacatedAtIsNullOrderByAssignedAtDesc(long bedNumber);

	List<BedAssignmentHistory> findByBed_BedNumber(long bedNumber);

}
