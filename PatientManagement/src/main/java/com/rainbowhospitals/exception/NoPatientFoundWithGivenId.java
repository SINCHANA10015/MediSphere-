package com.rainbowhospitals.exception;

public class NoPatientFoundWithGivenId extends RuntimeException {
	
	public NoPatientFoundWithGivenId(String message) {
		super(message);
	}

}
