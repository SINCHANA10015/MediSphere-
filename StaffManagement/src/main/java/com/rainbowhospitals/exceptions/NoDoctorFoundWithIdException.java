package com.rainbowhospitals.exceptions;

public class NoDoctorFoundWithIdException extends RuntimeException{
	
	public NoDoctorFoundWithIdException(String message) {
		super(message);
	}

}
