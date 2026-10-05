package com.rainbowhospitals.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(BedNotFoundException.class)
	public ResponseEntity<String> handleBedNotFoundException(BedNotFoundException ex){
		return new ResponseEntity<String>(ex.getMessage(),HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(RoomNotFoundException.class)
	public ResponseEntity<String> handleRoomNotFoundException(RoomNotFoundException ex){
		return new ResponseEntity<String>(ex.getMessage(),HttpStatus.NOT_FOUND);
	}
   
	@ExceptionHandler(RoomOutOfCapacityException.class)
	public ResponseEntity<String> handleRoomOutOfCapacityException(RoomOutOfCapacityException ex){
		return new ResponseEntity<String>(ex.getMessage(),HttpStatus.NOT_ACCEPTABLE);
	}
}