package com.rainbowhospitals.builder;

import java.util.ArrayList;
import java.util.List;

import com.rainbowhospitals.dto.BedResponseDTO;
import com.rainbowhospitals.dto.RoomResponseDto;
import com.rainbowhospitals.model.Bed;
import com.rainbowhospitals.model.Room;

public class RoomResponseBuilder {
	
	public static RoomResponseDto buildRoomResponseDtoFromRoom(Room room) {
		return RoomResponseDto.builder()
				.roomCapacity(room.getRoomCapacity())
				.roomNumber(room.getRoomNumber())
				.roomType(room.getRoomType())
				.beds(buildBedDetailsResponseDtos(room.getBeds()))
				.build();
	}
	public static List<BedResponseDTO> buildBedDetailsResponseDtos(List<Bed> beds) {

		List<BedResponseDTO> bedDetailsResponseDto = new ArrayList<>();
		
		for (Bed bed : beds) {
			bedDetailsResponseDto.add(  BedResponseBuilder.buildBedResponseDtoFromBed(bed));
		}
		
		return bedDetailsResponseDto;
	}
}
