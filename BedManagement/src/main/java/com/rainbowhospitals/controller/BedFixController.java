package com.rainbowhospitals.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import com.rainbowhospitals.dao.BedRepository;
import com.rainbowhospitals.model.Bed;

public class BedFixController {
	
	private final BedRepository bedRepository;

	public BedFixController(BedRepository bedRepository) {
		this.bedRepository = bedRepository;
	}
   
	@PostMapping("/fix-inverted-beds")
	public ResponseEntity<String> fixInvertedBeds() {
		List<Bed> allBeds = bedRepository.findAll();
		int fixedCount = 0;
		for (Bed bed : allBeds) {
			if (bed.isOccupied() && bed.getPatientId() == 0) {
				bed.setOccupied(false);
				bedRepository.save(bed);
				fixedCount++;
			}	
	}
		return  ResponseEntity.ok("Fixed " + fixedCount + " beds with inverted occupancy status");
	}
}
