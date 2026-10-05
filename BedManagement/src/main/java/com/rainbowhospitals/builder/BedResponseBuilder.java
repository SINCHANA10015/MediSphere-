package com.rainbowhospitals.builder;

import java.util.ArrayList;
import java.util.List;

import java.util.List;

import com.rainbowhospitals.dto.BedResponseDTO;
import com.rainbowhospitals.model.Bed;

public class BedResponseBuilder {
	
	public static BedResponseDTO buildBedResponseDtoFromBed(Bed bed) {
		return BedResponseDTO.builder()
				.bedNumber(bed.getBedNumber())
				.isOccupied(bed.isOccupied())
				.roomNumber(bed.getRoom().getRoomNumber())
				.build();
	}
	
	public static List<BedResponseDTO> buildListOfBedResponseDtoFromBed(List<Bed> beds){
		return beds.stream()
				.map((eachBed)->BedResponseDTO.builder()
						.bedNumber(eachBed.getBedNumber())
						.isOccupied(eachBed.isOccupied())
						.roomNumber(eachBed.getRoom().getRoomNumber())
						.build()).toList();
	}
	

}
