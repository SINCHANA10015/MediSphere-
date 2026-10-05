package com.rainbowhospitals.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rainbowhospitals.builder.StaffBuilder;
import com.rainbowhospitals.builder.StaffResponseDtoBuilder;
import com.rainbowhospitals.dao.StaffDetailsRepository;
import com.rainbowhospitals.dao.StaffRepository;
import com.rainbowhospitals.dto.StaffRequestDto;
import com.rainbowhospitals.dto.StaffResponseDto;
import com.rainbowhospitals.enums.Specialization;
import com.rainbowhospitals.exceptions.NoDoctorFoundWithIdException;
import com.rainbowhospitals.exceptions.StaffIdNotFoundException;
import com.rainbowhospitals.exceptions.StaffNotFoundException;
import com.rainbowhospitals.model.Staff;
import com.rainbowhospitals.service.StaffService;

@Service
public class StaffServiceImpl implements StaffService {

	private final StaffRepository staffRepository;

	private final StaffDetailsRepository staffDetailsRepository;

	private final PasswordEncoder passwordEncoder;

	public StaffServiceImpl(StaffRepository staffRepository, StaffDetailsRepository staffDetailsRepository,
			PasswordEncoder passwordEncoder) {
		this.staffRepository = staffRepository;
		this.staffDetailsRepository = staffDetailsRepository;
		this.passwordEncoder = passwordEncoder;

	}

	@Override
	public StaffResponseDto registerStaff(StaffRequestDto registerStaffDto) {
		Staff staff = new Staff();
		Staff registerStaff = StaffBuilder.buildStaffFromRegisterStaffDto(registerStaffDto);
		Staff savedStaff = staffRepository.save(registerStaff);
		return StaffResponseDtoBuilder.buildStaffDetailsDtofromStaff(savedStaff);

	}

	@Override
	public StaffResponseDto fetchStaffDetailsByStaffId(String staffId) {
		Staff staff = staffRepository.findByStaffId(staffId);
		// .orElseThrow(() -> new StaffIdNotFoundException("No Staff details found with
		// id " + staffId));
		return StaffResponseDtoBuilder.buildStaffDetailsDtofromStaff(staff);

	}

	@Override
	public List<StaffResponseDto> fetchAllStaffDetails() {
		List<StaffResponseDto> staffDetailsDtoList = staffRepository.findByIsEmployeeActiveTrue().stream()
				.map(staff -> StaffResponseDtoBuilder.buildStaffDetailsDtofromStaff(staff)).toList();
//		List<Staff> allStaffs = staffRepository.findAll();
//		List<StaffDetailsDto> staffDetailsDtoList = new ArrayList<>();
//		for (Staff eachStaff : allStaffs) {
//			StaffDetailsDto staffDetailsDtofromStaff = StaffDetailsDtoBuilder.buildStaffDetailsDtofromStaff(eachStaff);
//			staffDetailsDtoList.add(staffDetailsDtofromStaff);
//		}
		return staffDetailsDtoList;
	}

	@Override
	public Specialization getSpecialization(String id) {
		Staff staff = staffRepository.findByStaffId(id);
		// .orElseThrow(()->new StaffIdNotFoundException("No staff found with Id " +id
		// +","
		// + "Please enter correct id"));
		return staff.getSpecialization();

	}

	@Override
	public List<StaffResponseDto> findByStaffFirstNameOrLastName(String name) {
		System.out.println("name = [" + name + "]");
		List<Staff> staffs = staffRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name,
				name); // findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase
		System.out.println(staffs);
		List<StaffResponseDto> staffDetailsDtoList = new ArrayList<>();
		if (staffs.isEmpty()) {
			throw new StaffNotFoundException("No Staff found with the name " + name + ",Please enter correct name");
		}

		for (Staff staff : staffs) {
			staffDetailsDtoList.add(StaffResponseDtoBuilder.buildStaffDetailsDtofromStaff(staff));
		}

		return staffDetailsDtoList;
	}

	@Override
	public String getDoctorName(String doctorId) {
		Staff doctor = staffRepository.findById(doctorId)
				.orElseThrow(() -> new NoDoctorFoundWithIdException("No Doctor found with given Id " + doctorId));
		return doctor.getFirstName() + doctor.getLastName();
	}

	@Override
	public ResponseEntity<StaffResponseDto> updateStaff(String id, StaffRequestDto registerStaffDto) {
		Staff staff = staffRepository.findById(id).orElseThrow(()-> new StaffIdNotFoundException("No Staff details found with id " + id));
		Staff updatedStaff = StaffBuilder.buildStaffFromRegisterStaffDto(registerStaffDto);
		updatedStaff.setStaffId(staff.getStaffId());
		Staff updatedAndsaved= staffRepository.save(updatedStaff);
		StaffResponseDto staffDetailsDtofromStaff = StaffResponseDtoBuilder.buildStaffDetailsDtofromStaff(updatedAndsaved);
		return  ResponseEntity.ok().body(staffDetailsDtofromStaff);
	}

	@Override
	public void deleteStaff(String id) {
		Staff staff = staffRepository.findById(id).orElseThrow(()-> new StaffIdNotFoundException("No Staff details found with id " + id));
						staff.setCanLogin(false);
						staff.setEmployeeActive(false);
	}

}
