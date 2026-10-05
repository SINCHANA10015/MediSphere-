package com.rainbowhospitals.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rainbowhospitals.dto.BedRequestDTO;
import com.rainbowhospitals.dto.BedResponseDTO;
import com.rainbowhospitals.service.BedService;

@RestController
@RequestMapping("/bed")
public class BedController {

	private final BedService bedService;

	public BedController(BedService bedService) {
		this.bedService = bedService;
	}

	@PostMapping("/add")
	private ResponseEntity<String> addBed(@RequestBody BedRequestDTO bedRequestDto) {
		return bedService.addBed(bedRequestDto);
	}

	@GetMapping("/allBeds")
	private ResponseEntity<List<BedResponseDTO>> getAllBeds() {
		return ResponseEntity.ok().body(bedService.getAllBeds());

	}

	@PutMapping("/updateBed/{id}")
	private ResponseEntity<BedResponseDTO> updateBedDetails(@PathVariable(name = "id") Long bedId,
			@PathVariable(name = "roomNumber") Long roomNumber, BedRequestDTO bedRequestDto) {
		return ResponseEntity.ok().body(bedService.updateBed(bedId, roomNumber, bedRequestDto));

	}

	@DeleteMapping("/delete-bed/{bedNumber}/{roomNumber}")
	public ResponseEntity<String> removeBed(@PathVariable(name = "bedNumber") long bedNumber,
			@PathVariable(name = "roomNumber") long roomNumber) {
		return bedService.removeBed(bedNumber, roomNumber);
	}

	@GetMapping("/room/{roomNumber}")
	public ResponseEntity<List<BedResponseDTO>> getAllBedsInRoom(@PathVariable long roomNumber) {
		return ResponseEntity.ok(bedService.getBedsByRoomId(roomNumber));
	}

	@GetMapping("/vacant/room/{roomNumber}")
	public ResponseEntity<List<BedResponseDTO>> getVacantBeds(@PathVariable long roomNumber) {
		return ResponseEntity.ok(bedService.getVacantBedsByRoomNumber(roomNumber));
	}
}