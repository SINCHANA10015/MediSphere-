package com.rainbowhospitals.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rainbowhospitals.dto.RoomRequestDto;
import com.rainbowhospitals.dto.RoomResponseDto;
import com.rainbowhospitals.service.RoomService;

@RestController
@RequestMapping("/room")
public class RoomController {
	
	private final RoomService roomService;

	public RoomController(RoomService roomService) {
		this.roomService = roomService;
	}
	
	@PostMapping("/add")
	public ResponseEntity<RoomResponseDto> addRoom(@RequestBody RoomRequestDto roomRequestDto) {
	  return ResponseEntity.ok(  roomService.addRoom(roomRequestDto));

}
	@DeleteMapping("/delete/{roomNumber}")
	public ResponseEntity<String> removeRoom(@PathVariable("roomNumber") long roomNumber) {
		return roomService.removeRoom(roomNumber);	 
	}
	
	@PutMapping("/update/{roomNumber}")
	public ResponseEntity<RoomResponseDto> updateRoomDetails(@PathVariable long roomNumber,
			@RequestBody RoomRequestDto roomRequestDto) {
		RoomResponseDto updatedRoomDetails = roomService.updateRoomDetails(roomNumber, roomRequestDto);
		return ResponseEntity.ok(updatedRoomDetails);
		
	}
	
	@GetMapping("/getRooms")
	public ResponseEntity<List<RoomResponseDto>> getAllRoomDetails(){
		List<RoomResponseDto> allRoomDetails = roomService.getAllRoomDetails();
		return ResponseEntity.status(HttpStatus.OK).body(allRoomDetails);
		
	} 
}
