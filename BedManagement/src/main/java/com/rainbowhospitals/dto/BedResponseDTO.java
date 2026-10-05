package com.rainbowhospitals.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BedResponseDTO {
	
	private long bedNumber;
	
	private long roomNumber;
	
	private boolean isOccupied;
	

}