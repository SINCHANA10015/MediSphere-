package com.rainbowhospitals.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(StaffIdNotFoundException.class)
	public ResponseEntity<String> handleStaffIdNotFoundException(StaffIdNotFoundException ex){
		return new ResponseEntity<String>(ex.getMessage(),HttpStatus.NOT_FOUND);
	}
	
	
	@ExceptionHandler(StaffNotFoundException.class)
	public ResponseEntity<String> handleStaffNotFoundException(StaffNotFoundException ex){
		return new ResponseEntity<String>(ex.getMessage(),HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(EmailAlreadyRegisteredException.class)
	public ResponseEntity<String> handleEmailAlreadyRegisteredException(EmailAlreadyRegisteredException ex){
		return new ResponseEntity<String>(ex.getMessage(),HttpStatus.NOT_ACCEPTABLE);
	}
	@ExceptionHandler(PhoneNumberAlreadyRegisteredException.class)
	public ResponseEntity<String> handlePhoneNumberAlreadyRegisteredException(PhoneNumberAlreadyRegisteredException ex){
		return new ResponseEntity<String>(ex.getMessage(),HttpStatus.NOT_ACCEPTABLE);
	}
	
	@ExceptionHandler(StaffNotFoundWithGivenEmail.class)
	public ResponseEntity<String> handleStaffNotFoundWithGivenEmail(StaffNotFoundWithGivenEmail ex){
		return new ResponseEntity<String>(ex.getMessage(),HttpStatus.NOT_ACCEPTABLE);
	}
	
	@ExceptionHandler
	public ResponseEntity<String> handleNoDoctorFoundWithIdException(NoDoctorFoundWithIdException ex){
		return new ResponseEntity<String>(ex.getMessage(),HttpStatus.NOT_FOUND);
	}
	@ExceptionHandler(NotADoctorException.class)
	public ResponseEntity<String> handleNotADoctorException(NotADoctorException ex)
	{
		return new ResponseEntity<String>(ex.getMessage(),HttpStatus.NOT_FOUND);
	}
}
