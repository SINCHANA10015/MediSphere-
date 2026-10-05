package com.rainbowhospitals.exceptions;

public class StaffNotFoundWithGivenEmail extends RuntimeException{
	
	public StaffNotFoundWithGivenEmail(String message) {
		super(message);
	}

}
