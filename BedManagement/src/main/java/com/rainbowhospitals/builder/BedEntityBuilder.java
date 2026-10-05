package com.rainbowhospitals.builder;

import com.rainbowhospitals.dto.BedRequestDTO;
import com.rainbowhospitals.model.Bed;

public class BedEntityBuilder {
	
	public static Bed buildBedFromBedRequestDto(BedRequestDTO bedRequestDto) {
		Bed bed = new Bed();
		return bed.builder()
				.isOccupied(bedRequestDto.isOccupied())
				.bedNumber(bedRequestDto.getBedNumber())
				.build();
				
		
	}
	
	

}
