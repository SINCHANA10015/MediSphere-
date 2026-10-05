package com.rainbowHospitals.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rainbowHospitals.dto.EmailRequest;
import com.rainbowHospitals.service.SendEmailService;

@RestController
@RequestMapping("api/notifications")
public class NotificationController {
	
	private final SendEmailService sendEmailService;
	
	public NotificationController(SendEmailService sendEmailService) {
		this.sendEmailService = sendEmailService;
	}
	
	@PostMapping("/send")
	public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request){
		sendEmailService.sendEmail(request);
		return ResponseEntity.ok("EmailSent Successfully");
	}

}
