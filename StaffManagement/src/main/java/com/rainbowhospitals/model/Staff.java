package com.rainbowhospitals.model;

import java.time.LocalDate;

import com.rainbowhospitals.enums.Specialization;
import com.rainbowhospitals.enums.StaffType;
import com.rainbowhospitals.util.StaffEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "staff")
@Builder
@EntityListeners(value=StaffEntityListener.class)
public class Staff {

	@Column(name = "staff_id", unique = true, nullable = false)
	@Id
	private String staffId;

	@Column(nullable = false)
	private String firstName;

	@Column(nullable = false)
	private String lastName;

	@Column(nullable = false)
	private String gender;

	@Column(nullable = false)
	private String phoneNumber;

	@Column(nullable = false)
	private String role;

	@Column(nullable = false)
	private StaffType staffType;

	@Column(nullable = false)
	private Specialization specialization;

	@Column(nullable = false)
	private LocalDate dateOfJoining;

	@Column(nullable = false)
	private int experienceInYears;

	private String email;

	@Column(nullable = false)
	private boolean canLogin;

	@Column(nullable = false)
	private boolean isEmployeeActive;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "staffAddressId")
	private StaffAddress staffAddress;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "staffDetailsId")
	private StaffDetails staffDetails;

	public Staff(String firstName, String lastName, String gender, String phoneNumber, String role, StaffType staffType,
			Specialization specialization, LocalDate dateOfJoining, int experienceInYears, String email,
			boolean canLogin, boolean isEmployeeActive, StaffAddress staffAddress, StaffDetails staffDetails) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.phoneNumber = phoneNumber;
		this.role = role;
		this.staffType = staffType;
		this.specialization = specialization;
		this.dateOfJoining = dateOfJoining;
		this.experienceInYears = experienceInYears;
		this.email = email;
		this.canLogin = canLogin;
		this.isEmployeeActive = isEmployeeActive;
		this.staffAddress = staffAddress;
		this.staffDetails = staffDetails;
	}
	
	

}
