package com.rainbowhospitals.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.rainbowhospitals.dto.BedRequestDTO;
import com.rainbowhospitals.dto.BedResponseDTO;
import com.rainbowhospitals.model.Bed;
@Service
public interface BedService {

	ResponseEntity<String> addBed(BedRequestDTO bedRequestDto);

	List<BedResponseDTO> getAllBeds();

	BedResponseDTO updateBed(Long bedId, Long roomNumber,BedRequestDTO bedRequestDto);

	ResponseEntity<String> removeBed(long bedNumber, long roomNumber);

	List<BedResponseDTO> getBedsByRoomId(long roomNumber);

	List<BedResponseDTO> getVacantBedsByRoomNumber(long roomNumber);
	
	

}
