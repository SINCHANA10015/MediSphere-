package com.rainbowhospitals.builder;

import org.springframework.beans.BeanUtils;

import com.rainbowhospitals.dto.StaffAddressDto;
import com.rainbowhospitals.dto.StaffResponseDto;
import com.rainbowhospitals.model.Staff;
import com.rainbowhospitals.model.StaffAddress;

public class StaffResponseDtoBuilder {
	
	
	public static StaffResponseDto buildStaffDetailsDtofromStaff(Staff staff) {
		return StaffResponseDto.builder()
				.staffId(staff.getStaffId())
		.firstName(staff.getFirstName())
		.lastName(staff.getLastName())
		.dateOfJoining(staff.getDateOfJoining())
		.gender(staff.getGender())
		.email(staff.getEmail())
		.role(staff.getRole())
		.staffType(staff.getStaffType())
		.isEmployeeActive(staff.isEmployeeActive())
		.experienceInYears(staff.getExperienceInYears())
		.canLogin(staff.isCanLogin())
		.phoneNumber(staff.getPhoneNumber())
		.specialization(staff.getSpecialization())
		.staffAddressDto(buildStaffAddressDtoFromStaffAddress(staff.getStaffAddress()))
		.build();
	}
	
	private static StaffAddressDto buildStaffAddressDtoFromStaffAddress(StaffAddress staffAddress) {
		
		StaffAddressDto staffAddressDto = new StaffAddressDto();
		BeanUtils.copyProperties(staffAddress, staffAddressDto);
		return staffAddressDto;
		
	}

}
