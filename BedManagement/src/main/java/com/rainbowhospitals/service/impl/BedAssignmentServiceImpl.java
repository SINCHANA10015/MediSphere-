package com.rainbowhospitals.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rainbowhospitals.builder.BedAssignmentHistoryBuilder;
import com.rainbowhospitals.builder.BedResponseBuilder;
import com.rainbowhospitals.dao.BedAssignmentHistoryRepository;
import com.rainbowhospitals.dao.BedRepository;
import com.rainbowhospitals.dto.BedAssignmentHistoryDTO;
import com.rainbowhospitals.dto.BedResponseDTO;
import com.rainbowhospitals.exception.BedNotFoundException;
import com.rainbowhospitals.exception.BedUnavailableException;
import com.rainbowhospitals.model.Bed;
import com.rainbowhospitals.model.BedAssignmentHistory;
import com.rainbowhospitals.service.BedAssignmentService;

@Service
public class BedAssignmentServiceImpl implements BedAssignmentService {

	private final BedAssignmentHistoryRepository bedAssignmentHistoryRepository;

	private final BedRepository bedRepository;

	public BedAssignmentServiceImpl(BedAssignmentHistoryRepository bedAssignmentHistoryRepository,
			BedRepository bedRepository) {
		this.bedAssignmentHistoryRepository = bedAssignmentHistoryRepository;
		this.bedRepository = bedRepository;
	}

	@Override
	public BedResponseDTO bedAssigntment(long bedNumber, String patientId) {
		Bed bed = bedRepository.findById(bedNumber).orElseThrow(() -> new BedNotFoundException("No bed found"));
		if (bed.isOccupied()) {
			throw new BedUnavailableException("Bed " + bedNumber + " is already occupied");
		}
		bed.setPatientId(Long.parseLong(patientId));
		bed.setOccupied(true);
		bedRepository.save(bed);
		BedAssignmentHistory bedHistory = new BedAssignmentHistory();
		bedHistory.setBed(bed);
		bedHistory.setAssignedAt(LocalDateTime.now());
		bedHistory.setPatientId(patientId);
		BedAssignmentHistory savedHistory = bedAssignmentHistoryRepository.save(bedHistory);
		return BedResponseBuilder.buildBedResponseDtoFromBed(bed);

	}

	@Override
	public BedResponseDTO vacateBed(long roomNumber, long bedNumber) {
		Bed bed = bedRepository.findBedWithRoom_RoomNumber(bedNumber, roomNumber);
		BedAssignmentHistory activeAssignment = bedAssignmentHistoryRepository
				.findTopByBed_BedNumberAndVacatedAtIsNullOrderByAssignedAtDesc(bedNumber);
		if (activeAssignment == null) {
			throw new IllegalStateException("No active bed assignment found for bed " + bedNumber);
		}

		activeAssignment.setVacatedAt(LocalDateTime.now());

		bed.setOccupied(false);
		bed.setPatientId(0);
		bedRepository.save(bed);
		bedAssignmentHistoryRepository.save(activeAssignment);
		return BedResponseBuilder.buildBedResponseDtoFromBed(bed);

	}

	@Override
	public List<BedAssignmentHistoryDTO> getHistoryByBedNumber(long bedNumber) {
		List<BedAssignmentHistory> bedHistory = bedAssignmentHistoryRepository.findByBed_BedNumber(bedNumber);
		return BedAssignmentHistoryBuilder.buildBedHistoryDTOFromBedHistory(bedHistory);

	}

}
