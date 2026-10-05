package com.rainbowhospitals.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rainbowhospitals.dto.BedAssignmentHistoryDTO;
import com.rainbowhospitals.dto.BedResponseDTO;
import com.rainbowhospitals.service.BedAssignmentService;

@RestController
@RequestMapping("/bedAssignment")
public class BedAssignmentController {
	
	private final BedAssignmentService bedAssignmentService;

	public BedAssignmentController(BedAssignmentService bedAssignmentService) {
		this.bedAssignmentService = bedAssignmentService;
	}
	
	@PostMapping("/assign/{bedNumber}/{patient}")
	public ResponseEntity<BedResponseDTO>  assignBed(@PathVariable(name="bedNumber") long bedNumber,@PathVariable(name="patient") String patientId ){
	return ResponseEntity.ok().body(bedAssignmentService.bedAssigntment(bedNumber, patientId));
		
	}
	
	@PutMapping("/vacatebed/{roomNumber}/{bedNumber}")
	public ResponseEntity<BedResponseDTO> vacateBed(
	        @PathVariable long roomNumber,
	        @PathVariable long bedNumber) {
		return ResponseEntity.ok().body( bedAssignmentService.vacateBed(roomNumber, bedNumber));
	}
   
	
	@GetMapping("/bed-history/{bedNumber}")
    public ResponseEntity<List<BedAssignmentHistoryDTO>> 
        getHistoryByBed(@PathVariable long bedNumber) {
		List<BedAssignmentHistoryDTO> historyByBedNumber = bedAssignmentService.getHistoryByBedNumber(bedNumber);
		return ResponseEntity.ok(historyByBedNumber);
    }
}
