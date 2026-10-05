package com.rainbowHospitals.service;

import org.springframework.stereotype.Service;

import com.rainbowHospitals.dto.EmailRequest;

@Service
public interface SendEmailService {
	
	
	
	public void sendEmail(EmailRequest request);

}
