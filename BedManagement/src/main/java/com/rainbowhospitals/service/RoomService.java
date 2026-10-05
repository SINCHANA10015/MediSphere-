package com.rainbowhospitals.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.rainbowhospitals.dto.RoomRequestDto;
import com.rainbowhospitals.dto.RoomResponseDto;

@Service

public interface RoomService {

	RoomResponseDto addRoom(RoomRequestDto roomRequestDto);

	ResponseEntity<String> removeRoom(long roomNumber);

	RoomResponseDto updateRoomDetails(long roomNumber, RoomRequestDto roomRequestDto);

	List<RoomResponseDto> getAllRoomDetails();

}
