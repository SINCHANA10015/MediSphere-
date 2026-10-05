package com.rainbowhospitals.util;

import org.springframework.stereotype.Component;

import com.rainbowhospitals.dao.StaffRepository;

@Component
public class StaffIdGenerator {

	private final StaffRepository staffRepository;

	public StaffIdGenerator(StaffRepository staffRepository) {
		this.staffRepository = staffRepository;
	}
	
	public String generateNextStaffId() {
		long nextNumber = 1;
		String lastStaffId =  staffRepository.findLastStaffId();
		
		if(lastStaffId != null && lastStaffId.startsWith("RBW-")) {
			String numberPart = lastStaffId.substring(4);
			 nextNumber = Integer.parseInt(numberPart) + 1; //converting numberPart(String) to int and adding
			 return String.format("RBW-%05d", nextNumber);
			
		}
		return String.format("RBW-%05d", nextNumber);
		
	}

}
