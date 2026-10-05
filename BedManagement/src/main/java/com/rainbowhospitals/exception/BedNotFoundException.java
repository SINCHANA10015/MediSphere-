package com.rainbowhospitals.exception;

public class BedNotFoundException extends RuntimeException {
	
	public BedNotFoundException(String message) {
		super(message);
	}

}
