package com.rainbowhospitals.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bed_assignment_history")
@Builder
public class BedAssignmentHistory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long bedAssignmentHistoryId;
	
	@ManyToOne
	@JoinColumn(name = "bed_Number", nullable = false)
	private Bed bed;
	
	@Column(name = "patient_id", nullable = false)
	private String patientId;
	
	@Column(name = "assigned_at", nullable = false)
	private LocalDateTime assignedAt;
	
	@Column(name = "vacated_at")
	private LocalDateTime vacatedAt;

}
