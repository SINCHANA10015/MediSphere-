 package com.rainbowhospitals.model;

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

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Table(name="staff_address")
public class StaffAddress {
	

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long staffAddressId;
    
	@Column(nullable=false)
    private String landmark;
    
	@Column(nullable=false)
    private String city;
    
	@Column(nullable=false)
    private String state;

	@Column(nullable=false)
    private String country;

    private String pinCode;

	public StaffAddress(String landmark, String city, String state, String country, String pinCode) {
		super();
		this.landmark = landmark;
		this.city = city;
		this.state = state;
		this.country = country;
		this.pinCode = pinCode;
	}
    
    

}
