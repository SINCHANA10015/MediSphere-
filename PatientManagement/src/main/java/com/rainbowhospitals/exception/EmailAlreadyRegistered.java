package com.rainbowhospitals.exception;

public class EmailAlreadyRegistered extends RuntimeException {
	
	public EmailAlreadyRegistered(String message) {
		super(message);
	}

}
