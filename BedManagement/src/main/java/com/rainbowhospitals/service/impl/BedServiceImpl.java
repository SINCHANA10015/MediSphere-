package com.rainbowhospitals.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.rainbowhospitals.builder.BedEntityBuilder;
import com.rainbowhospitals.builder.BedResponseBuilder;
import com.rainbowhospitals.dao.BedRepository;
import com.rainbowhospitals.dao.RoomRepository;
import com.rainbowhospitals.dto.BedRequestDTO;
import com.rainbowhospitals.dto.BedResponseDTO;
import com.rainbowhospitals.exception.BedNotFoundException;
import com.rainbowhospitals.exception.BedUnavailableException;
import com.rainbowhospitals.exception.RoomNotFoundException;
import com.rainbowhospitals.exception.RoomOutOfCapacityException;
import com.rainbowhospitals.model.Bed;
import com.rainbowhospitals.model.Room;
import com.rainbowhospitals.service.BedService;
@Service
public class BedServiceImpl implements BedService {

	private final BedRepository bedRepository;
	private final RoomRepository roomRepository;
	

	public BedServiceImpl(BedRepository bedRepository,RoomRepository roomRepository) {
		this.bedRepository = bedRepository;
		this.roomRepository = roomRepository;
	}

	@Override
	public ResponseEntity<String> addBed(BedRequestDTO bedRequestDTO) {
		Room existingRoom = roomRepository.findByRoomNumber(bedRequestDTO.getRoomNumber());
		if (existingRoom == null) {
			throw new RoomNotFoundException("Room Number " + bedRequestDTO.getRoomNumber() + " doesn't exist");
		}
		
		List<Bed> bedInExistingRoom = existingRoom.getBeds();
		for (Bed bed : bedInExistingRoom) {
			if (bedRequestDTO.getBedNumber() == bed.getBedNumber()) {
				throw new IllegalArgumentException("Bed Number " + bedRequestDTO.getBedNumber() + " already exists in Room Number " + bedRequestDTO.getRoomNumber());
			}
		}
		
		if (bedInExistingRoom.isEmpty() || (bedInExistingRoom.size() < existingRoom.getRoomCapacity())) {
			Bed bed = BedEntityBuilder.buildBedFromBedRequestDto(bedRequestDTO);
			bed.setRoom(existingRoom);
			BedResponseBuilder.buildBedResponseDtoFromBed(bedRepository.save(bed));
			return ResponseEntity.status(HttpStatus.CREATED).body("Successfully added Bed Number " + bedRequestDTO.getBedNumber() + " into Room Number " + bedRequestDTO.getRoomNumber());
		}

		throw new RoomOutOfCapacityException("Cannot add Bed into Room Number " + bedRequestDTO.getRoomNumber() + " as the room is full");
	}

	

	@Override
	public List<BedResponseDTO> getAllBeds() {
		
		List<Bed> allBeds = bedRepository.findAll();
	//	if(allBeds!=null || !allBeds.isEmpty()) 
		return  BedResponseBuilder.buildListOfBedResponseDtoFromBed(allBeds) ; 
	}

	@Override
	public BedResponseDTO updateBed(Long bedId,Long roomNumber, BedRequestDTO bedRequestDto) {
		Room existingRoom = roomRepository.findById(roomNumber).orElseThrow(()->new RoomNotFoundException("Room not Found with Id: "+roomNumber));
		Bed existingBed = bedRepository.findById(bedId).orElseThrow(()->new BedNotFoundException("No bed found with id "+bedId));		
		Bed updatedBedDetails = BedEntityBuilder.buildBedFromBedRequestDto(bedRequestDto);
		existingBed.setRoom(existingRoom);
		updatedBedDetails.setBedNumber(existingBed.getBedNumber());
        Bed savedBed = bedRepository.save(updatedBedDetails);
        
		return BedResponseBuilder.buildBedResponseDtoFromBed(savedBed);
	}

	@Override
	public ResponseEntity<String> removeBed(long bedNumber, long roomNumber) {
		Bed bed = bedRepository.findBedWithRoom_RoomNumber(bedNumber, roomNumber);
		bedRepository.delete(bed);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Successfully deleted");
	}

	@Override
	public List<BedResponseDTO> getBedsByRoomId(long roomNumber) {
		Room room = roomRepository.findById(roomNumber).orElseThrow(()->new RoomNotFoundException("Room not Found with Id: "+roomNumber));
		List<Bed> beds = bedRepository.findByRoomRoomNumber(roomNumber);
		if(beds.isEmpty()) {
			throw new BedNotFoundException("No beds found for room number " + roomNumber);
		}
		return beds.stream().map((bed)->BedResponseBuilder.buildBedResponseDtoFromBed(bed)).toList();
	}

	@Override
	public List<BedResponseDTO> getVacantBedsByRoomNumber(long roomNumber) {
		Room room = roomRepository.findById(roomNumber).orElseThrow(()->new RoomNotFoundException("Room not Found with Id: "+roomNumber));
		List<Bed> vacantBeds = bedRepository.findByRoomRoomNumberAndIsOccupiedFalse(roomNumber);
		if(vacantBeds.isEmpty()) {
			throw new BedUnavailableException("No vacant beds available in room number " + roomNumber);
		}
		return vacantBeds.stream()
				.map((emptyBed)->BedResponseBuilder.buildBedResponseDtoFromBed(emptyBed)).toList();
				
				
	}
	
}

	

