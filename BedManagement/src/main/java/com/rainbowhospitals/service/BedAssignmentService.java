package com.rainbowhospitals.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rainbowhospitals.dto.BedAssignmentHistoryDTO;
import com.rainbowhospitals.dto.BedResponseDTO;
@Service
public interface BedAssignmentService {

	BedResponseDTO bedAssigntment(long bedNumber, String patientId);

	BedResponseDTO vacateBed(long roomNumber, long bedNumber);

	List<BedAssignmentHistoryDTO> getHistoryByBedNumber(long bedNumber);

}
