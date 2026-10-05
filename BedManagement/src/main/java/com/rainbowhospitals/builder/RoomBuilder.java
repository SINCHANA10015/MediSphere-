package com.rainbowhospitals.builder;

import java.util.ArrayList;
import java.util.List;

import com.rainbowhospitals.dto.BedRequestDTO;
import com.rainbowhospitals.dto.RoomRequestDto;
import com.rainbowhospitals.model.Bed;
import com.rainbowhospitals.model.Room;

public class RoomBuilder {

	public static Room buildRoomFromRoomRequestDto(RoomRequestDto roomRequestDto) {
		return Room.builder()
				.roomCapacity(roomRequestDto.getRoomCapacity())
				.roomNumber(roomRequestDto.getRoomNumber())
				.roomType(roomRequestDto.getRoomType())
				.beds(buildBedsListFromBedRequestDto(roomRequestDto.getBeds()))
				.build();
				
		
	}
	
	public static List<Bed> buildBedsListFromBedRequestDto(List<BedRequestDTO> bedRequestDtos){
		List<Bed> bedList = new ArrayList<>();
		if(bedRequestDtos !=null)
			for(BedRequestDTO beds : bedRequestDtos) {
				Bed bed = new Bed();
				System.out.println(beds.getBedNumber());
				bed.setBedNumber(beds.getBedNumber());
	            bed.setOccupied(beds.isOccupied());
				bedList.add(bed);
			}
			
			return bedList;
		}
}
