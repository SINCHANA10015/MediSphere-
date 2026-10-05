package com.rainbowhospitals.builder;

import java.util.Random;

import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.rainbowhospitals.dto.StaffAddressDto;
import com.rainbowhospitals.dto.StaffRequestDto;
import com.rainbowhospitals.model.Staff;
import com.rainbowhospitals.model.StaffAddress;
import com.rainbowhospitals.model.StaffDetails;

public class StaffBuilder {

	private static ThreadLocal<String> plainPasswordHolder = new ThreadLocal<>();

	private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public static Staff buildStaffFromRegisterStaffDto(StaffRequestDto registerStaffDto) {
//		Staff staff = new Staff();
//		staff.setFirstName(registerStaffDto.getFirstName());
		
		return Staff.builder()
				
		.firstName(registerStaffDto.getFirstName())
		.lastName(registerStaffDto.getLastName())
		.gender(registerStaffDto.getGender())
		.phoneNumber(registerStaffDto.getPhoneNumber())
		.role(registerStaffDto.getRole())
		.experienceInYears(registerStaffDto.getExperienceInYears())
		.email(registerStaffDto.getEmail())
		.canLogin(true)
		.isEmployeeActive(true)
		.dateOfJoining(registerStaffDto.getDateOfJoining())
		.specialization(registerStaffDto.getSpecialization())
		.staffType(registerStaffDto.getStaffType())
		.staffAddress(StaffBuilder.buildStaffAddressFromStaffAddressDto(registerStaffDto.getStaffAddressDto()))
		.staffDetails(buildStaffDetailsFromStaffDetailsDto(registerStaffDto.getEmail())).build();
		
	
	}

	public static StaffAddress buildStaffAddressFromStaffAddressDto(StaffAddressDto staffAddressDto) {
		StaffAddress staffAddress = new StaffAddress();
		BeanUtils.copyProperties(staffAddressDto, staffAddress);
		return staffAddress;

	}

	public static StaffDetails buildStaffDetailsFromStaffDetailsDto(String email) {
					String plainPassword = generateSixDigitPassword();
					String hashedPassword = passwordEncoder.encode(plainPassword);
					plainPasswordHolder.set(plainPassword);
					return StaffDetails.builder()
							.email(email)
							.password(hashedPassword)
							.requirePasswordReset(true)
							.build();
					
	}

	   			public static String generateSixDigitPassword() {
	   					Random random = new Random();
	   					int password = 100000  + random.nextInt(90000);
	   					return String.valueOf(password);  //Type conversion
}
	   			public static String getPlainPassword() {
	   				return plainPasswordHolder.get();
	   			}
	   			
	   			public static void clearPlainPassword() {
	   				plainPasswordHolder.remove();
	   			}
}