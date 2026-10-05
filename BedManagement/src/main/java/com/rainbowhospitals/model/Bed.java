package com.rainbowhospitals.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name="beds")
public class Bed {
	
	@Id
	private long bedNumber;
	@ManyToOne
	@JoinColumn(name = "room_Number")

	private Room room;
	
	private boolean isOccupied;
	
	private long patientId;
	
	@OneToMany(mappedBy="bed",cascade = CascadeType.ALL)
	List<BedAssignmentHistory> bedAssignmentHistory;

	public Bed(Room room, boolean isOccupied, long patientId, List<BedAssignmentHistory> bedAssignmentHistory) {
		super();
		this.room = room;
		this.isOccupied = isOccupied;
		this.patientId = patientId;
		this.bedAssignmentHistory = bedAssignmentHistory;
	}
	
	
	

}
