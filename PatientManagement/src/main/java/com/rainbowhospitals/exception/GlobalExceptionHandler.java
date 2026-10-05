package com.rainbowhospitals.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(EmailAlreadyRegistered.class)
	public ResponseEntity<String> handleEmailAlreadyRegistered(EmailAlreadyRegistered ex){
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ex.getMessage());	}

	@ExceptionHandler(PhoneNumberAlreadyExistsException.class)
	public ResponseEntity<String> handlePhoneNumberAlreadyExistsException(PhoneNumberAlreadyExistsException ex){
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ex.getMessage());	}
	
	@ExceptionHandler(NoPatientFoundWithGivenId.class)
	public ResponseEntity<String> handleNoPatientFoundWithGivenId(NoPatientFoundWithGivenId ex){
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

}
