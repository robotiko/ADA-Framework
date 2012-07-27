package com.desandroid.framework.ada.validators;

/**
 * This class represent the result of an individual validation process.
 * @version 1.4.5
 * @author DesAndrOId
 */
public final class ValidationResult {
	
	private Boolean isOK = false;
	private String message = "";
	
	
	public Boolean IsOK() {
		return this.isOK;
	}
	
	public void IsOK(Boolean pValue) {
		this.isOK = pValue;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
