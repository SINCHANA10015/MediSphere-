package com.rainbowhospitals.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.rainbowhospitals.model.Bed;

public interface BedRepository extends JpaRepository<Bed, Long>{
	
	@Query(value="select b from Bed b where bedNumber = :bedNumber and b.room.roomNumber =:roomNumber")
	Bed findBedWithRoom_RoomNumber(long bedNumber, long roomNumber);
	
	List<Bed> findByRoomRoomNumber(long roomNumber);

	List<Bed> findByRoomRoomNumberAndIsOccupiedFalse(long roomNumber);
	

}
