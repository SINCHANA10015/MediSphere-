package com.rainbowhospitals.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BedRequestDTO {
	
	private long bedNumber;
		
	private long roomNumber;
	
	private boolean isOccupied;
	
}
