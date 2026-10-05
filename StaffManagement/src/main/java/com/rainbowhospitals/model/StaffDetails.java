package com.rainbowhospitals.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "staff_details")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StaffDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "staff_id", nullable = false, unique = true)
	private long staffDetailsId;

	private String email;

	@Column(nullable = false)
	private String password;

	private String resetOtp;

	private LocalDateTime otpExpiryTime;

	private boolean requirePasswordReset = true;

	public StaffDetails(String email, String password, String resetOtp, LocalDateTime otpExpiryTime,
			boolean requirePasswordReset) {
		super();
		this.email = email;
		this.password = password;
		this.resetOtp = resetOtp;
		this.otpExpiryTime = otpExpiryTime;
		this.requirePasswordReset = requirePasswordReset;
	}

}
