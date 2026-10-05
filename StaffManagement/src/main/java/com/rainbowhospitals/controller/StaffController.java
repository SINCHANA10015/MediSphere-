package com.rainbowhospitals.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rainbowhospitals.dto.StaffRequestDto;
import com.rainbowhospitals.dto.StaffResponseDto;
import com.rainbowhospitals.enums.Specialization;
import com.rainbowhospitals.service.StaffService;

@RestController()
@RequestMapping("/staff")
public class StaffController {
	
	private final StaffService staffService;
	
	public StaffController(StaffService staffService) {
		this.staffService = staffService;
	}
	
	
	@PostMapping("/register-staff")
	private ResponseEntity<StaffResponseDto> registerStaff(@RequestBody StaffRequestDto registerStaffDto) {
		StaffResponseDto registeredStaffDto = staffService.registerStaff(registerStaffDto);
		return ResponseEntity.ok().body(registeredStaffDto);
		
	}
	@GetMapping("/staffById/{id}")
	private ResponseEntity<StaffResponseDto> getStaffById(@PathVariable(name="id") String staffId){
		StaffResponseDto fetchStaffDetailsByStaffId = staffService.fetchStaffDetailsByStaffId(staffId);
		return ResponseEntity.ok().body(fetchStaffDetailsByStaffId);
		
	}
	
	@GetMapping("/allStaff")
	private ResponseEntity<List<StaffResponseDto>> getAllStaffDetails(){
		return ResponseEntity.ok().body(staffService.fetchAllStaffDetails());
	}
	
	@GetMapping("/specilization/{staffId}")
	private ResponseEntity<Specialization> getSpecialization(@PathVariable(name="staffId") String id){
		 return  ResponseEntity.ok().body( staffService.getSpecialization(id));
		
	}
	@GetMapping("/byName")
	private ResponseEntity<List<StaffResponseDto>> searchStaffByFirstNameOrLastName(@RequestParam(name="name",required=true)String name){
	   return ResponseEntity.ok().body(staffService.findByStaffFirstNameOrLastName(name));
	}
	
	
	@GetMapping("/doctorByName/{doctorId}")
	private ResponseEntity<String> findDoctorName(@PathVariable String doctorId){
		return ResponseEntity.ok().body( staffService.getDoctorName(doctorId));
	}
	
	@PutMapping("/update/{staffId}")
	private ResponseEntity<StaffResponseDto> updateStaff(String staffId,@RequestBody StaffRequestDto staffRequestDto){
		return staffService.updateStaff(staffId, staffRequestDto);
	}
	
	@DeleteMapping("/delete/{id}")
	private ResponseEntity<String> deleteStaff(@PathVariable String id){
		staffService.deleteStaff(id);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Successfully deleted");
	}

}
