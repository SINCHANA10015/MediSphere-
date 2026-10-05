package com.rainbowhospitals.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.rainbowhospitals.builder.BedEntityBuilder;
import com.rainbowhospitals.builder.RoomBuilder;
import com.rainbowhospitals.builder.RoomResponseBuilder;
import com.rainbowhospitals.dao.RoomRepository;
import com.rainbowhospitals.dto.BedRequestDTO;
import com.rainbowhospitals.dto.RoomRequestDto;
import com.rainbowhospitals.dto.RoomResponseDto;
import com.rainbowhospitals.exception.RoomNotFoundException;
import com.rainbowhospitals.model.Bed;
import com.rainbowhospitals.model.Room;
import com.rainbowhospitals.service.RoomService;
@Service
public class RoomServiceImpl implements RoomService{
	
	private final RoomRepository roomRepository;
	
	public RoomServiceImpl(RoomRepository roomRepository) {
		this.roomRepository = roomRepository;
	}

	@Override
	public RoomResponseDto addRoom(RoomRequestDto roomRequestDto) {
		Room room = RoomBuilder.buildRoomFromRoomRequestDto(roomRequestDto);
		if(room.getBeds()!=null ) {
			 List<Bed> bedEntities = new ArrayList<>();
			 for (BedRequestDTO bedDto : roomRequestDto.getBeds()) {
				 Bed bedEntity = BedEntityBuilder.buildBedFromBedRequestDto(bedDto);
				 bedEntities.add(bedEntity);
		            bedEntity.setRoom(room);
			 }
			 room.setBeds(bedEntities);
		}
		Room savedRoom = roomRepository.save(room);
		return RoomResponseBuilder.buildRoomResponseDtoFromRoom(savedRoom);
		
	}

	@Override
	public ResponseEntity<String> removeRoom(long roomNumber) {
		Room room = roomRepository.findById(roomNumber).orElseThrow(()-> new RoomNotFoundException("No room found with room number "+roomNumber));
		roomRepository.delete(room);
		return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("Deleted Successfully");  
	}

	@Override
	public RoomResponseDto updateRoomDetails(long roomNumber, RoomRequestDto roomRequestDto) {
		Room existingRoom = roomRepository.findById(roomNumber).orElseThrow(()-> new RoomNotFoundException("No room found with room number "+roomNumber));
		int currentBedCount = existingRoom.getBeds() != null ? existingRoom.getBeds().size() : 0;
		if (roomRequestDto.getRoomCapacity() < currentBedCount) {
			throw new IllegalArgumentException("Cannot reduce room capacity to " + roomRequestDto.getRoomCapacity() + 
					" as room currently has " + currentBedCount + " beds");
		}

		existingRoom.setRoomType(roomRequestDto.getRoomType());
		existingRoom.setRoomCapacity(roomRequestDto.getRoomCapacity());

		Room room = roomRepository.save(existingRoom);
		return RoomResponseBuilder.buildRoomResponseDtoFromRoom(room);
	}

	@Override
	public List<RoomResponseDto> getAllRoomDetails() {
		List<Room> all = roomRepository.findAll();
		List<RoomResponseDto> roomResponse=new ArrayList<>();
		for(Room room : all) {
			RoomResponseDto roomResponseDtoFromRoom = RoomResponseBuilder.buildRoomResponseDtoFromRoom(room);
			roomResponse.add(roomResponseDtoFromRoom);	
		}
		return roomResponse;
	}

}
