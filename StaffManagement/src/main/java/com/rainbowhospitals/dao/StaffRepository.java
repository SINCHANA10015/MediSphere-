package com.rainbowhospitals.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rainbowhospitals.model.Staff;

public interface StaffRepository extends JpaRepository<Staff, String> {
	
	@Query(value = "select staff_id from Staff where staff_id like 'RBW-%' order By staff_id DESC LIMIT 1",nativeQuery = true)
	 String findLastStaffId();
	
	Staff findByStaffId(String id);
	
	List<Staff> findByFirstNameContainingIgnoreCase(String name);
    
//	@Query(value="select from Staff where first_Name like ' )
//	List<Staff> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
	
	@Query("""
		    SELECT s
		    FROM Staff s
		    WHERE LOWER(s.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))
		       OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))
		""")
		List<Staff> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
		        @Param("firstName") String firstName,
		        @Param("lastName") String lastName);
    List<Staff> findByIsEmployeeActiveTrue();
}
