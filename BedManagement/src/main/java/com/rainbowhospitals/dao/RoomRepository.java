package com.rainbowhospitals.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rainbowhospitals.model.Bed;
import com.rainbowhospitals.model.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
	
	Room findByRoomNumber(Long roomNumber);
	

}
